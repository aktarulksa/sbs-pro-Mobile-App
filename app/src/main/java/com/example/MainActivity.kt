package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SbsTopBar
import com.example.ui.dialogs.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SbsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SbsViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeOption = uiState.appTheme,
                darkTheme = uiState.isDarkMode
            ) {
                SbsApp(viewModel)
            }
        }
    }
}

@Composable
fun SbsApp(viewModel: SbsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val allSystemTransactions by viewModel.allSystemTransactions.collectAsStateWithLifecycle()
    val pendingTransactions by viewModel.pendingTransactions.collectAsStateWithLifecycle()
    val smsLogs by viewModel.smsLogs.collectAsStateWithLifecycle()
    val isRtdbConnected by viewModel.isRtdbConnected.collectAsStateWithLifecycle()
    val rtdbStatus by viewModel.rtdbConnectionStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Show toast on success
    LaunchedEffect(uiState.successToast) {
        uiState.successToast?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.dismissSuccessToast()
        }
    }

    // Android back handling
    BackHandler(enabled = currentUser != null) {
        when {
            uiState.showAdminAddBalanceDialog -> viewModel.closeAdminAddBalanceDialog()
            uiState.showAdminRejectDialog -> viewModel.closeAdminRejectDialog()
            uiState.showThemeDialog -> viewModel.toggleThemeDialog(false)
            uiState.showSwitchAccountDialog -> viewModel.toggleSwitchAccountDialog(false)
            uiState.showSecurityInfo -> viewModel.toggleSecurityInfo(false)
            uiState.isRechargeDialogOpen -> viewModel.closeRechargeDialog()
            uiState.isMfsDialogOpen -> viewModel.closeMfsDialog()
            uiState.isBillPayDialogOpen -> viewModel.closeBillPayDialog()
            uiState.lastCompletedTxn != null -> viewModel.dismissLastTxnReceipt()
            uiState.activeReportDetail != null -> viewModel.closeReportDetail()
            uiState.selectedCountry != null -> viewModel.clearSelectedCountry()
            uiState.activeTab != "RECHARGE" -> viewModel.setActiveTab("RECHARGE")
            else -> {
                viewModel.toggleSwitchAccountDialog(true)
            }
        }
    }

    if (currentUser == null) {
        // MOBILE NUMBER AUTHENTICATION SCREEN: LOGIN & ACCOUNT CREATION
        AuthScreen(
            savedUsers = allUsers,
            onLoginSubmit = viewModel::loginWithMobile,
            onRegisterSubmit = { mobile, name, shopName, pin, initialBalance, tier ->
                viewModel.createAccountWithMobile(mobile, name, shopName, pin, initialBalance, tier)
            },
            isProcessing = uiState.isProcessing,
            errorMessage = uiState.errorMessage
        )
    } else if (!uiState.isDomainConnected) {
        // Domain Setup Screen
        DomainScreen(
            domainInput = uiState.domainInput,
            onDomainChange = viewModel::onDomainChanged,
            onSubmit = viewModel::submitDomain,
            isProcessing = uiState.isProcessing,
            errorMessage = uiState.errorMessage
        )
    } else {
        // MAIN SBS PRO APPLICATION (PERSONALIZED FOR LOGGED-IN MOBILE ACCOUNT)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SbsTopBar(
                    userAccount = currentUser,
                    activeTab = uiState.activeTab,
                    onTabSelected = viewModel::setActiveTab,
                    onHomeClick = {
                        viewModel.clearSelectedCountry()
                        viewModel.setActiveTab("RECHARGE")
                    },
                    balanceExpanded = uiState.balanceExpanded,
                    onToggleBalance = viewModel::toggleBalanceExpanded,
                    onShieldClick = { viewModel.toggleSecurityInfo(true) },
                    onAccountClick = { viewModel.toggleSwitchAccountDialog(true) },
                    onThemeClick = { viewModel.toggleThemeDialog(true) },
                    onAdminClick = { viewModel.setActiveTab("ADMIN") },
                    pendingApprovalsCount = pendingTransactions.size
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.activeTab) {
                    "RECHARGE" -> {
                        val currentCountryId = uiState.selectedCountry?.id ?: "bd"
                        val operators = viewModel.getOperatorsForCountry(currentCountryId)

                        RechargeScreen(
                            countries = viewModel.countries,
                            selectedCountry = uiState.selectedCountry,
                            operators = operators,
                            selectedService = uiState.selectedService,
                            onServiceSelected = viewModel::selectService,
                            onCountrySelected = viewModel::selectCountry,
                            onBackToCountries = viewModel::clearSelectedCountry,
                            onOperatorSelected = viewModel::openRechargeDialog
                        )
                    }

                    "REPORTS" -> {
                        ReportsScreen(
                            transactions = transactions,
                            smsLogs = smsLogs,
                            activeDetailCategory = uiState.activeReportDetail,
                            onTileClick = viewModel::openReportDetail,
                            onBackToGrid = viewModel::closeReportDetail
                        )
                    }

                    "ADMIN" -> {
                        if (currentUser?.isAdmin == true) {
                            AdminPanelScreen(
                                pendingTransactions = pendingTransactions,
                                allTransactions = allSystemTransactions,
                                allAgents = allUsers,
                                activeSubTab = uiState.adminActiveSubTab,
                                onSubTabChanged = viewModel::setAdminActiveSubTab,
                                onApproveTransaction = viewModel::approveTransaction,
                                onRejectTransactionClick = viewModel::openAdminRejectDialog,
                                onApproveAllPending = viewModel::approveAllPending,
                                onOpenAddBalance = viewModel::openAdminAddBalanceDialog,
                                isProcessing = uiState.isProcessing,
                                isRtdbConnected = isRtdbConnected,
                                rtdbStatus = rtdbStatus,
                                onTestRtdbSync = viewModel::testRtdbSync
                            )
                        } else {
                            // Regular users are prevented from seeing the Admin Control Panel
                            LaunchedEffect(Unit) {
                                viewModel.setActiveTab("RECHARGE")
                            }
                        }
                    }

                    "CHANGE PIN" -> {
                        ChangePinScreen(
                            currentAgentPin = currentUser?.pin ?: "1234",
                            onPinChangeSubmit = viewModel::changePin,
                            errorMessage = uiState.errorMessage,
                            currentTheme = uiState.appTheme,
                            onOpenThemeDialog = { viewModel.toggleThemeDialog(true) }
                        )
                    }
                }
            }
        }

        // Dialogs
        if (uiState.showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = uiState.appTheme,
                isDarkMode = uiState.isDarkMode,
                onThemeSelected = viewModel::setTheme,
                onDarkModeToggled = viewModel::toggleDarkMode,
                onDismiss = { viewModel.toggleThemeDialog(false) }
            )
        }

        if (uiState.showAdminAddBalanceDialog && uiState.selectedAgentForBalance != null) {
            AdminAddBalanceDialog(
                agent = uiState.selectedAgentForBalance!!,
                onDismiss = viewModel::closeAdminAddBalanceDialog,
                onConfirm = { amount, type, remarks ->
                    viewModel.adminAddBalance(uiState.selectedAgentForBalance!!.mobileNumber, amount, type, remarks)
                },
                isProcessing = uiState.isProcessing
            )
        }

        if (uiState.showAdminRejectDialog && uiState.selectedTxnForReject != null) {
            AdminRejectDialog(
                txn = uiState.selectedTxnForReject!!,
                onDismiss = viewModel::closeAdminRejectDialog,
                onConfirm = { reason ->
                    viewModel.rejectTransaction(uiState.selectedTxnForReject!!.id, reason)
                },
                isProcessing = uiState.isProcessing
            )
        }

        if (uiState.isRechargeDialogOpen && uiState.selectedOperator != null) {
            val op = uiState.selectedOperator!!
            val packages = viewModel.getPackagesForOperator(op.code)
            RechargeDialog(
                operator = op,
                packageOffers = packages,
                onDismiss = viewModel::closeRechargeDialog,
                onSubmitRecharge = viewModel::executeRecharge,
                isProcessing = uiState.isProcessing,
                errorMessage = uiState.errorMessage
            )
        }

        if (uiState.isMfsDialogOpen) {
            MfsDialog(
                serviceType = uiState.selectedService,
                onDismiss = viewModel::closeMfsDialog,
                onSubmit = viewModel::executeMfs,
                isProcessing = uiState.isProcessing,
                errorMessage = uiState.errorMessage
            )
        }

        if (uiState.isBillPayDialogOpen) {
            BillPayDialog(
                onDismiss = viewModel::closeBillPayDialog,
                onSubmit = viewModel::executeBillPay,
                isProcessing = uiState.isProcessing,
                errorMessage = uiState.errorMessage
            )
        }

        if (uiState.lastCompletedTxn != null) {
            ReceiptDialog(
                transaction = uiState.lastCompletedTxn!!,
                onDismiss = viewModel::dismissLastTxnReceipt
            )
        }

        if (uiState.showSecurityInfo) {
            SecurityInfoDialog(
                userAccount = currentUser,
                onDismiss = { viewModel.toggleSecurityInfo(false) },
                onSwitchDomain = viewModel::disconnectDomain
            )
        }

        if (uiState.showSwitchAccountDialog) {
            SwitchAccountDialog(
                currentUser = currentUser,
                allUsers = allUsers,
                onSelectUser = viewModel::switchUser,
                onAddNewAccount = {
                    viewModel.toggleSwitchAccountDialog(false)
                    viewModel.logout()
                },
                onOpenThemeDialog = {
                    viewModel.toggleSwitchAccountDialog(false)
                    viewModel.toggleThemeDialog(true)
                },
                onOpenAdminPanel = {
                    viewModel.toggleSwitchAccountDialog(false)
                    viewModel.setActiveTab("ADMIN")
                },
                onUnlockAdmin = viewModel::unlockAdminMode,
                onLogout = {
                    viewModel.toggleSwitchAccountDialog(false)
                    viewModel.logout()
                },
                onDismiss = { viewModel.toggleSwitchAccountDialog(false) }
            )
        }
    }
}
