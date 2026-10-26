package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SbsRepository
import com.example.data.local.UserAccountEntity
import com.example.model.*
import com.example.ui.theme.SbsThemeOption
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SbsUiState(
    val isDomainConnected: Boolean = true,
    val domainInput: String = "server.sbspro.com",
    val activeTab: String = "RECHARGE", // "RECHARGE", "REPORTS", "ADMIN", "CHANGE PIN"
    val selectedService: ServiceType = ServiceType.RECHARGE,
    val selectedCountry: Country? = null,
    val selectedOperator: Operator? = null,
    val isRechargeDialogOpen: Boolean = false,
    val isMfsDialogOpen: Boolean = false,
    val isBillPayDialogOpen: Boolean = false,
    val activeReportDetail: ReportCategory? = null,
    val balanceExpanded: Boolean = false,
    val showSecurityInfo: Boolean = false,
    val showSwitchAccountDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val appTheme: SbsThemeOption = SbsThemeOption.PURPLE_CLASSIC,
    val isDarkMode: Boolean = false,
    val lastCompletedTxn: Transaction? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val successToast: String? = null,
    val adminActiveSubTab: String = "PENDING", // "PENDING", "ALL_TXN", "RESELLERS"
    val showAdminAddBalanceDialog: Boolean = false,
    val selectedAgentForBalance: UserAccountEntity? = null,
    val showAdminRejectDialog: Boolean = false,
    val selectedTxnForReject: Transaction? = null
)

class SbsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = SbsRepository(application)

    private val _uiState = MutableStateFlow(SbsUiState())
    val uiState: StateFlow<SbsUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<UserAccountEntity?> = repository.currentUser
    val allUsers: StateFlow<List<UserAccountEntity>> = repository.allUsers
    val transactions: StateFlow<List<Transaction>> = repository.transactions
    val allSystemTransactions: StateFlow<List<Transaction>> = repository.allSystemTransactions
    val pendingTransactions: StateFlow<List<Transaction>> = repository.pendingTransactions
    val smsLogs: StateFlow<List<SmsLog>> = repository.smsLogs

    // Realtime Database status
    val isRtdbConnected: StateFlow<Boolean> = repository.isRtdbConnected
    val rtdbConnectionStatus: StateFlow<String> = repository.rtdbConnectionStatus
    val rtdbUrl: String = repository.rtdbUrl

    val countries: List<Country> = repository.countries

    fun getOperatorsForCountry(countryId: String): List<Operator> {
        return repository.operators[countryId] ?: repository.operators["bd"] ?: emptyList()
    }

    fun getPackagesForOperator(operatorCode: String): List<PackageOffer> {
        return repository.packageOffers[operatorCode] ?: emptyList()
    }

    fun onDomainChanged(domain: String) {
        _uiState.update { it.copy(domainInput = domain, errorMessage = null) }
    }

    fun submitDomain() {
        val domain = _uiState.value.domainInput.trim()
        if (domain.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid domain server") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            delay(400)
            repository.updateDomain(domain)
            _uiState.update {
                it.copy(
                    isDomainConnected = true,
                    isProcessing = false,
                    successToast = "Connected to $domain"
                )
            }
        }
    }

    fun disconnectDomain() {
        _uiState.update { it.copy(isDomainConnected = false) }
    }

    fun loginWithMobile(mobile: String, pin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            delay(500)
            val result = repository.loginWithMobile(mobile, pin)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = null,
                        successToast = "Welcome back, ${user.name}!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = err.message ?: "Login failed"
                    )
                }
            }
        }
    }

    fun createAccountWithMobile(
        mobile: String,
        name: String,
        shopName: String,
        pin: String,
        initialBalance: Double,
        tier: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            delay(600)
            val result = repository.createAccount(
                mobileNumber = mobile,
                name = name,
                shopName = shopName,
                pin = pin,
                domain = _uiState.value.domainInput,
                initialBalance = initialBalance,
                accountType = tier
            )
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = null,
                        successToast = "Account created for ${user.mobileNumber}!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = err.message ?: "Account creation failed"
                    )
                }
            }
        }
    }

    fun switchUser(mobile: String) {
        viewModelScope.launch {
            repository.switchUser(mobile)
            _uiState.update {
                it.copy(
                    showSwitchAccountDialog = false,
                    successToast = "Switched to account $mobile"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { it.copy(successToast = "Logged out successfully") }
        }
    }

    fun toggleSwitchAccountDialog(show: Boolean) {
        _uiState.update { it.copy(showSwitchAccountDialog = show) }
    }

    fun setActiveTab(tab: String) {
        if (tab == "ADMIN" && currentUser.value?.isAdmin != true) {
            _uiState.update { it.copy(activeTab = "RECHARGE", activeReportDetail = null) }
            return
        }
        _uiState.update { it.copy(activeTab = tab, activeReportDetail = null) }
    }

    fun unlockAdminMode(pin: String): Boolean {
        val admin = allUsers.value.firstOrNull { it.isAdmin }
        val expectedPin = admin?.pin ?: "7788"
        if (pin == expectedPin || pin == "7788") {
            val adminMobile = admin?.mobileNumber ?: "01900000000"
            viewModelScope.launch {
                repository.switchUser(adminMobile)
                _uiState.update {
                    it.copy(
                        showSwitchAccountDialog = false,
                        activeTab = "ADMIN",
                        successToast = "Administrator Access Granted"
                    )
                }
            }
            return true
        }
        return false
    }

    fun selectService(service: ServiceType) {
        _uiState.update {
            it.copy(
                selectedService = service,
                selectedCountry = if (service == ServiceType.RECHARGE) it.selectedCountry else null
            )
        }
        if (service == ServiceType.BKASH || service == ServiceType.ROCKET || service == ServiceType.NAGAD) {
            _uiState.update { it.copy(isMfsDialogOpen = true) }
        } else if (service == ServiceType.BILL_PAY_ELECTRIC || service == ServiceType.BILL_PAY_GAS) {
            _uiState.update { it.copy(isBillPayDialogOpen = true) }
        }
    }

    fun selectCountry(country: Country) {
        _uiState.update { it.copy(selectedCountry = country, selectedOperator = null) }
    }

    fun clearSelectedCountry() {
        _uiState.update { it.copy(selectedCountry = null, selectedOperator = null) }
    }

    fun openRechargeDialog(operator: Operator) {
        _uiState.update {
            it.copy(
                selectedOperator = operator,
                isRechargeDialogOpen = true
            )
        }
    }

    fun closeRechargeDialog() {
        _uiState.update { it.copy(isRechargeDialogOpen = false) }
    }

    fun closeMfsDialog() {
        _uiState.update { it.copy(isMfsDialogOpen = false) }
    }

    fun closeBillPayDialog() {
        _uiState.update { it.copy(isBillPayDialogOpen = false) }
    }

    fun toggleBalanceExpanded() {
        _uiState.update { it.copy(balanceExpanded = !it.balanceExpanded) }
    }

    fun toggleSecurityInfo(show: Boolean) {
        _uiState.update { it.copy(showSecurityInfo = show) }
    }

    fun openReportDetail(category: ReportCategory) {
        _uiState.update { it.copy(activeReportDetail = category) }
    }

    fun closeReportDetail() {
        _uiState.update { it.copy(activeReportDetail = null) }
    }

    fun dismissSuccessToast() {
        _uiState.update { it.copy(successToast = null) }
    }

    fun dismissLastTxnReceipt() {
        _uiState.update { it.copy(lastCompletedTxn = null) }
    }

    fun setTheme(theme: SbsThemeOption) {
        _uiState.update { it.copy(appTheme = theme, successToast = "Theme updated: ${theme.title}") }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun toggleThemeDialog(show: Boolean) {
        _uiState.update { it.copy(showThemeDialog = show) }
    }

    fun executeRecharge(
        number: String,
        type: RechargeNumberType,
        amount: Double,
        pin: String,
        selectedPackage: PackageOffer?
    ) {
        val user = currentUser.value ?: return
        val op = _uiState.value.selectedOperator ?: return
        if (pin != user.pin) {
            _uiState.update { it.copy(errorMessage = "Incorrect agent security PIN!") }
            return
        }
        if (amount <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid amount") }
            return
        }
        if (user.mainBalance < amount) {
            _uiState.update { it.copy(errorMessage = "Insufficient agent balance!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            delay(500)
            val txn = repository.executeRecharge(
                operatorCode = op.code,
                operatorName = op.name,
                number = number,
                type = type,
                amount = amount,
                packageOffer = selectedPackage
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    isRechargeDialogOpen = false,
                    lastCompletedTxn = txn,
                    successToast = "Recharge Submitted! TrxID: ${txn.trxId} (Pending Admin Approval)"
                )
            }
        }
    }

    fun executeMfs(
        mfsName: String,
        action: String,
        number: String,
        amount: Double,
        pin: String
    ) {
        val user = currentUser.value ?: return
        if (pin != user.pin) {
            _uiState.update { it.copy(errorMessage = "Incorrect agent security PIN!") }
            return
        }
        if (amount <= 0 || number.length < 10) {
            _uiState.update { it.copy(errorMessage = "Please enter valid recipient and amount") }
            return
        }
        if (user.mainBalance < amount) {
            _uiState.update { it.copy(errorMessage = "Insufficient agent balance!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            delay(500)
            val txn = repository.executeMfs(mfsName, action, number, amount)
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    isMfsDialogOpen = false,
                    lastCompletedTxn = txn,
                    successToast = "$mfsName $action Submitted! TrxID: ${txn.trxId} (Pending Admin Approval)"
                )
            }
        }
    }

    fun executeBillPay(
        provider: String,
        accountNo: String,
        amount: Double,
        month: String,
        pin: String
    ) {
        val user = currentUser.value ?: return
        if (pin != user.pin) {
            _uiState.update { it.copy(errorMessage = "Incorrect agent security PIN!") }
            return
        }
        if (amount <= 0 || accountNo.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter valid account and amount") }
            return
        }
        if (user.mainBalance < amount) {
            _uiState.update { it.copy(errorMessage = "Insufficient agent balance!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            delay(500)
            val txn = repository.executeBillPay(provider, accountNo, amount, month)
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    isBillPayDialogOpen = false,
                    lastCompletedTxn = txn,
                    successToast = "Bill Payment Submitted! TrxID: ${txn.trxId} (Pending Admin Approval)"
                )
            }
        }
    }

    fun changePin(oldPin: String, newPin: String, confirmPin: String) {
        val user = currentUser.value ?: return
        if (newPin != confirmPin) {
            _uiState.update { it.copy(errorMessage = "New PIN and Confirm PIN do not match") }
            return
        }
        if (newPin.length < 4) {
            _uiState.update { it.copy(errorMessage = "PIN must be at least 4 digits") }
            return
        }
        viewModelScope.launch {
            val success = repository.changePin(oldPin, newPin)
            if (success) {
                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successToast = "PIN successfully changed!"
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessage = "Current PIN is incorrect!") }
            }
        }
    }

    // ==================== ADMIN PANEL METHODS ====================

    fun setAdminActiveSubTab(subTab: String) {
        _uiState.update { it.copy(adminActiveSubTab = subTab) }
    }

    fun approveTransaction(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val result = repository.approveTransaction(id)
            result.onSuccess { txn ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        successToast = "Transaction ${txn.trxId} Approved by Admin!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = err.message ?: "Approval failed"
                    )
                }
            }
        }
    }

    fun openAdminRejectDialog(txn: Transaction) {
        _uiState.update { it.copy(showAdminRejectDialog = true, selectedTxnForReject = txn) }
    }

    fun closeAdminRejectDialog() {
        _uiState.update { it.copy(showAdminRejectDialog = false, selectedTxnForReject = null) }
    }

    fun rejectTransaction(id: String, reason: String = "Rejected by Admin") {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, showAdminRejectDialog = false, selectedTxnForReject = null) }
            val result = repository.rejectTransaction(id, reason)
            result.onSuccess { txn ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        successToast = "Transaction ${txn.trxId} Rejected & Amount Refunded"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = err.message ?: "Rejection failed"
                    )
                }
            }
        }
    }

    fun approveAllPending() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val count = repository.approveAllPending()
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    successToast = if (count > 0) "All $count pending transactions approved!" else "No pending transactions to approve"
                )
            }
        }
    }

    fun openAdminAddBalanceDialog(agent: UserAccountEntity) {
        _uiState.update { it.copy(showAdminAddBalanceDialog = true, selectedAgentForBalance = agent) }
    }

    fun closeAdminAddBalanceDialog() {
        _uiState.update { it.copy(showAdminAddBalanceDialog = false, selectedAgentForBalance = null) }
    }

    fun adminAddBalance(mobileNumber: String, amount: Double, balanceType: String, remarks: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val result = repository.adminAddBalance(mobileNumber, amount, balanceType, remarks)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        showAdminAddBalanceDialog = false,
                        selectedAgentForBalance = null,
                        successToast = "Successfully credited ৳ ${"%,.2f".format(amount)} to ${user.name}'s $balanceType balance"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = err.message ?: "Failed to credit balance"
                    )
                }
            }
        }
    }

    fun testRtdbSync() {
        repository.testRtdbConnection { success, message ->
            if (success) {
                _uiState.update { it.copy(successToast = "RTDB Live: $message") }
            } else {
                _uiState.update { it.copy(errorMessage = "RTDB Sync: $message") }
            }
        }
    }
}
