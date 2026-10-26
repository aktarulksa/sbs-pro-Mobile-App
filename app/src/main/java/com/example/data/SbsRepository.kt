package com.example.data

import android.content.Context
import com.example.data.local.*
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.random.Random

import com.example.data.realtime.RealtimeDatabaseManager
import com.example.data.realtime.RtdbTransaction

class SbsRepository(context: Context) {

    private val db = SbsDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val transactionDao = db.transactionDao()
    private val smsDao = db.smsDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    val rtdbManager = RealtimeDatabaseManager(context)
    val isRtdbConnected: StateFlow<Boolean> = rtdbManager.isConnected
    val rtdbConnectionStatus: StateFlow<String> = rtdbManager.connectionStatus
    val rtdbUrl: String = RealtimeDatabaseManager.RTDB_URL

    private val _currentUser = MutableStateFlow<UserAccountEntity?>(null)
    val currentUser: StateFlow<UserAccountEntity?> = _currentUser.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserAccountEntity>>(emptyList())
    val allUsers: StateFlow<List<UserAccountEntity>> = _allUsers.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _allSystemTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allSystemTransactions: StateFlow<List<Transaction>> = _allSystemTransactions.asStateFlow()

    private val _pendingTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val pendingTransactions: StateFlow<List<Transaction>> = _pendingTransactions.asStateFlow()

    private val _smsLogs = MutableStateFlow<List<SmsLog>>(emptyList())
    val smsLogs: StateFlow<List<SmsLog>> = _smsLogs.asStateFlow()

    val countries: List<Country> = listOf(
        Country("bd", "Bangladesh", "flag_bd", "+880"),
        Country("in", "India", "flag_in", "+91"),
        Country("indth", "India DTH", "flag_in_dth", "+91", isDth = true),
        Country("pk", "Pakistan", "flag_pk", "+92"),
        Country("np", "Nepal", "flag_np", "+977"),
        Country("mm", "Myanmar", "flag_mm", "+95"),
        Country("lk", "Sri Lanka", "flag_lk", "+94"),
        Country("sg", "Singapore", "flag_sg", "+65"),
        Country("my", "Malaysia", "flag_my", "+60"),
        Country("ph", "Philippines", "flag_ph", "+63"),
        Country("ai", "Anguilla", "flag_ai", "+1264"),
        Country("ar", "Argentina", "flag_ar", "+54")
    )

    val operators: Map<String, List<Operator>> = mapOf(
        "bd" to listOf(
            Operator("gp", "GRA", "GrameenPhone", "bd", listOf("017", "013")),
            Operator("robi", "ROB", "Robi", "bd", listOf("018")),
            Operator("bl", "BAN", "Banglalink", "bd", listOf("019", "014")),
            Operator("teletalk", "TEL", "TeleTalk", "bd", listOf("015")),
            Operator("airtel", "AIR", "Airtel", "bd", listOf("016")),
            Operator("skitto", "SKI", "Skitto", "bd", listOf("017", "013"))
        ),
        "in" to listOf(
            Operator("in_jio", "JIO", "Reliance Jio", "in", listOf("9", "8", "7")),
            Operator("in_airtel", "AIR", "Airtel India", "in", listOf("9", "8", "7")),
            Operator("in_vi", "VI", "Vodafone Idea", "in", listOf("9", "8", "7")),
            Operator("in_bsnl", "BSN", "BSNL Mobile", "in", listOf("9", "8", "7"))
        ),
        "indth" to listOf(
            Operator("dth_tataplay", "TAT", "Tata Play", "indth", isDth = true),
            Operator("dth_dishtv", "DSH", "Dish TV", "indth", isDth = true),
            Operator("dth_airteldth", "ADTH", "Airtel Digital TV", "indth", isDth = true),
            Operator("dth_sundirect", "SUN", "Sun Direct", "indth", isDth = true)
        ),
        "pk" to listOf(
            Operator("pk_jazz", "JAZ", "Jazz", "pk", listOf("030")),
            Operator("pk_telenor", "TEL", "Telenor PK", "pk", listOf("034")),
            Operator("pk_zong", "ZON", "Zong 4G", "pk", listOf("031")),
            Operator("pk_ufone", "UFO", "Ufone", "pk", listOf("033"))
        ),
        "np" to listOf(
            Operator("np_ntc", "NTC", "Nepal Telecom", "np", listOf("984", "985")),
            Operator("np_ncell", "NCL", "Ncell Axiata", "np", listOf("980", "981"))
        ),
        "my" to listOf(
            Operator("my_maxis", "MAX", "Maxis", "my", listOf("012")),
            Operator("my_celcom", "CEL", "Celcom", "my", listOf("019")),
            Operator("my_digi", "DIG", "Digi", "my", listOf("016"))
        )
    )

    val packageOffers: Map<String, List<PackageOffer>> = mapOf(
        "GRA" to listOf(
            PackageOffer("gp1", "GRA", "30 GB + 800 Min Drive Pack", "30 GB", "800 Min", "30 Days", 498.0, cashback = 120.0, category = "Drive"),
            PackageOffer("gp2", "GRA", "50 GB Internet Exclusive", "50 GB", "0 Min", "30 Days", 399.0, cashback = 80.0, category = "Internet"),
            PackageOffer("gp3", "GRA", "1000 Minutes Talk Time", "0 GB", "1000 Min", "30 Days", 460.0, cashback = 50.0, category = "Minute"),
            PackageOffer("gp4", "GRA", "15 GB + 400 Min Super Pack", "15 GB", "400 Min", "30 Days", 349.0, cashback = 65.0, category = "Combo"),
            PackageOffer("gp5", "GRA", "5 GB Weekly Fast Pack", "5 GB", "0 Min", "7 Days", 108.0, cashback = 15.0, category = "Internet")
        ),
        "ROB" to listOf(
            PackageOffer("rob1", "ROB", "40 GB + 900 Min Robi Drive", "40 GB", "900 Min", "30 Days", 478.0, cashback = 140.0, category = "Drive"),
            PackageOffer("rob2", "ROB", "60 GB High Speed Data", "60 GB", "0 Min", "30 Days", 420.0, cashback = 90.0, category = "Internet"),
            PackageOffer("rob3", "ROB", "500 Minutes Any Net", "0 GB", "500 Min", "30 Days", 280.0, cashback = 40.0, category = "Minute"),
            PackageOffer("rob4", "ROB", "20 GB + 500 Min Special", "20 GB", "500 Min", "30 Days", 360.0, cashback = 70.0, category = "Combo")
        ),
        "BAN" to listOf(
            PackageOffer("bl1", "BAN", "50 GB Banglalink Dhamaka", "50 GB", "850 Min", "30 Days", 468.0, cashback = 130.0, category = "Drive"),
            PackageOffer("bl2", "BAN", "35 GB Monthly Internet", "35 GB", "0 Min", "30 Days", 330.0, cashback = 60.0, category = "Internet"),
            PackageOffer("bl3", "BAN", "750 Min Talk Time Pack", "0 GB", "750 Min", "30 Days", 390.0, cashback = 45.0, category = "Minute")
        ),
        "TEL" to listOf(
            PackageOffer("tel1", "TEL", "TeleTalk Student Special 25GB", "25 GB", "300 Min", "30 Days", 299.0, cashback = 40.0, category = "Drive"),
            PackageOffer("tel2", "TEL", "10 GB 15 Days", "10 GB", "0 Min", "15 Days", 149.0, cashback = 20.0, category = "Internet")
        ),
        "AIR" to listOf(
            PackageOffer("air1", "AIR", "Airtel 45 GB + 800 Min Drive", "45 GB", "800 Min", "30 Days", 450.0, cashback = 125.0, category = "Drive"),
            PackageOffer("air2", "AIR", "30 GB Monthly Nonstop", "30 GB", "0 Min", "30 Days", 319.0, cashback = 55.0, category = "Internet")
        )
    )

    init {
        scope.launch {
            initSeedAndListen()
        }
    }

    private suspend fun initSeedAndListen() {
        // Collect all users
        userDao.getAllUsers().collect { list ->
            _allUsers.value = list
            if (list.isEmpty()) {
                // Seed default primary account (Regular Agent)
                val primary = UserAccountEntity(
                    mobileNumber = "01712345678",
                    name = "Aktarul Telecom",
                    shopName = "Aktarul Telecom & MFS Center",
                    pin = "1234",
                    domain = "server.sbspro.com",
                    mainBalance = 24850.50,
                    driveBalance = 8420.00,
                    bankBalance = 15200.00,
                    accountType = "Master Reseller",
                    operator = "Grameenphone",
                    isCurrentActive = true
                )
                userDao.insertUser(primary)

                // Seed dedicated System Administrator account (HQ Admin only)
                val adminUser = UserAccountEntity(
                    mobileNumber = "01900000000",
                    name = "Central Server Admin",
                    shopName = "SBS Pro HQ & Server Operations",
                    pin = "7788",
                    domain = "server.sbspro.com",
                    mainBalance = 500000.0,
                    driveBalance = 100000.0,
                    bankBalance = 250000.0,
                    accountType = "Administrator",
                    operator = "Grameenphone",
                    isCurrentActive = false
                )
                userDao.insertUser(adminUser)

                seedInitialTxAndSms(primary.mobileNumber)
            } else if (list.none { it.mobileNumber == "01900000000" }) {
                val adminUser = UserAccountEntity(
                    mobileNumber = "01900000000",
                    name = "Central Server Admin",
                    shopName = "SBS Pro HQ & Server Operations",
                    pin = "7788",
                    domain = "server.sbspro.com",
                    mainBalance = 500000.0,
                    driveBalance = 100000.0,
                    bankBalance = 250000.0,
                    accountType = "Administrator",
                    operator = "Grameenphone",
                    isCurrentActive = false
                )
                userDao.insertUser(adminUser)
            }
        }
    }

    init {
        scope.launch {
            userDao.getActiveUser().collect { active ->
                _currentUser.value = active
                if (active != null) {
                    observeUserData(active.mobileNumber)
                } else {
                    _transactions.value = emptyList()
                    _smsLogs.value = emptyList()
                }
            }
        }

        // Global observation for Admin Panel
        scope.launch {
            transactionDao.getAllTransactions().collect { allList ->
                val mapped = allList.map { entity ->
                    Transaction(
                        id = entity.id,
                        type = entity.type,
                        recipient = entity.recipient,
                        operatorOrProvider = entity.operatorOrProvider,
                        amount = entity.amount,
                        commission = entity.commission,
                        status = try { TransactionStatus.valueOf(entity.status) } catch (e: Exception) { TransactionStatus.PENDING },
                        timestamp = entity.timestamp,
                        trxId = entity.trxId,
                        balanceAfter = entity.balanceAfter,
                        reference = entity.reference,
                        userMobile = entity.userMobile
                    )
                }
                _allSystemTransactions.value = mapped
                _pendingTransactions.value = mapped.filter { it.status == TransactionStatus.PENDING }
            }
        }

        // Realtime Database synchronization: live updates from remote devices/admin
        rtdbManager.onRemoteTransactionUpdated = { rtdbTx ->
            scope.launch {
                val existing = transactionDao.getTransactionById(rtdbTx.id)
                if (existing == null) {
                    val newEntity = TransactionEntity(
                        id = rtdbTx.id,
                        userMobile = rtdbTx.userMobile,
                        type = rtdbTx.type,
                        recipient = rtdbTx.recipient,
                        operatorOrProvider = rtdbTx.operatorOrProvider,
                        amount = rtdbTx.amount,
                        commission = rtdbTx.commission,
                        status = rtdbTx.status,
                        trxId = rtdbTx.trxId,
                        balanceAfter = rtdbTx.balanceAfter,
                        reference = rtdbTx.reference,
                        timestamp = rtdbTx.timestamp
                    )
                    transactionDao.insertTransaction(newEntity)
                } else if (existing.status != rtdbTx.status) {
                    transactionDao.updateTransactionStatus(rtdbTx.id, rtdbTx.status)
                }
            }
        }

        rtdbManager.onRemoteUserBalanceUpdated = { mobile, main, drive, bank ->
            scope.launch {
                val user = userDao.getUserByMobile(mobile)
                if (user != null) {
                    userDao.updateBalances(mobile, main, drive, bank)
                    if (_currentUser.value?.mobileNumber == mobile) {
                        _currentUser.value = user.copy(mainBalance = main, driveBalance = drive, bankBalance = bank)
                    }
                }
            }
        }
    }

    private fun observeUserData(mobile: String) {
        scope.launch {
            transactionDao.getTransactionsForUser(mobile).collect { entityList ->
                _transactions.value = entityList.map { entity ->
                    Transaction(
                        id = entity.id,
                        type = entity.type,
                        recipient = entity.recipient,
                        operatorOrProvider = entity.operatorOrProvider,
                        amount = entity.amount,
                        commission = entity.commission,
                        status = try { TransactionStatus.valueOf(entity.status) } catch (e: Exception) { TransactionStatus.SUCCESS },
                        timestamp = entity.timestamp,
                        trxId = entity.trxId,
                        balanceAfter = entity.balanceAfter,
                        reference = entity.reference,
                        userMobile = entity.userMobile
                    )
                }
            }
        }

        scope.launch {
            smsDao.getSmsForUser(mobile).collect { entityList ->
                _smsLogs.value = entityList.map { entity ->
                    SmsLog(
                        id = entity.id,
                        sender = entity.sender,
                        message = entity.message,
                        timestamp = entity.timestamp
                    )
                }
            }
        }
    }

    private suspend fun seedInitialTxAndSms(mobile: String) {
        val now = System.currentTimeMillis()
        val tx1 = TransactionEntity(
            id = "tx-1",
            userMobile = mobile,
            type = "Recharge",
            recipient = "01712-449102",
            operatorOrProvider = "GrameenPhone (Prepaid)",
            amount = 50.0,
            commission = 1.40,
            status = "SUCCESS",
            trxId = "GP-983141",
            balanceAfter = 24850.50,
            reference = "Prepaid Top-Up",
            timestamp = now - 15 * 60 * 1000
        )
        val tx2 = TransactionEntity(
            id = "tx-2",
            userMobile = mobile,
            type = "bKash",
            recipient = "01823-991204",
            operatorOrProvider = "bKash Cash In",
            amount = 1500.0,
            commission = 6.15,
            status = "SUCCESS",
            trxId = "BK-772109",
            balanceAfter = 24900.50,
            reference = "Cash In",
            timestamp = now - 45 * 60 * 1000
        )
        // Seed initial pending transaction for Admin approval
        val txPending = TransactionEntity(
            id = "tx-pending-1",
            userMobile = mobile,
            type = "bKash",
            recipient = "01911-554433",
            operatorOrProvider = "bKash Cash In",
            amount = 2000.0,
            commission = 8.20,
            status = "PENDING",
            trxId = "BK-PEND-3021",
            balanceAfter = 22850.50,
            reference = "Agent Banking - Awaiting Admin Approval",
            timestamp = now - 5 * 60 * 1000
        )
        transactionDao.insertTransaction(tx1)
        transactionDao.insertTransaction(tx2)
        transactionDao.insertTransaction(txPending)

        val sms1 = SmsEntity(
            id = "sms-1",
            userMobile = mobile,
            sender = "SBS Server",
            message = "Welcome to SBS Pro! Account registered with mobile $mobile. Domain: server.sbspro.com. Your initial balance is active.",
            timestamp = now - 60 * 60 * 1000
        )
        smsDao.insertSms(sms1)
    }

    suspend fun createAccount(
        mobileNumber: String,
        name: String,
        shopName: String,
        pin: String,
        domain: String = "server.sbspro.com",
        initialBalance: Double = 15000.0,
        accountType: String = "Reseller Agent"
    ): Result<UserAccountEntity> = withContext(Dispatchers.IO) {
        val cleanMobile = mobileNumber.replace(Regex("[^0-9]"), "")
        if (cleanMobile.length < 10) {
            return@withContext Result.failure(IllegalArgumentException("Mobile number must be at least 10 digits"))
        }
        val existing = userDao.getUserByMobile(cleanMobile)
        if (existing != null) {
            return@withContext Result.failure(IllegalStateException("An account with mobile $cleanMobile already exists! Please log in."))
        }

        userDao.clearActiveUsers()
        val detectedOp = when {
            cleanMobile.startsWith("017") || cleanMobile.startsWith("013") -> "Grameenphone"
            cleanMobile.startsWith("018") -> "Robi"
            cleanMobile.startsWith("019") || cleanMobile.startsWith("014") -> "Banglalink"
            cleanMobile.startsWith("015") -> "TeleTalk"
            cleanMobile.startsWith("016") -> "Airtel"
            else -> "Telecom"
        }

        val newUser = UserAccountEntity(
            mobileNumber = cleanMobile,
            name = name.ifBlank { "Agent $cleanMobile" },
            shopName = shopName.ifBlank { "SBS Digital Shop" },
            pin = pin,
            domain = domain.ifBlank { "server.sbspro.com" },
            mainBalance = initialBalance,
            driveBalance = 2500.0,
            bankBalance = 5000.0,
            accountType = accountType,
            operator = detectedOp,
            createdAt = System.currentTimeMillis(),
            isCurrentActive = true
        )
        userDao.insertUser(newUser)
        rtdbManager.syncUserAccount(newUser)

        val welcomeSms = SmsEntity(
            id = "sms-" + UUID.randomUUID().toString().take(6),
            userMobile = cleanMobile,
            sender = "SBS System",
            message = "Congratulations $name! Your SBS Pro account has been created for mobile $cleanMobile. Initial balance: ৳ ${"%,.2f".format(initialBalance)}.",
            timestamp = System.currentTimeMillis()
        )
        smsDao.insertSms(welcomeSms)

        _currentUser.value = newUser
        Result.success(newUser)
    }

    suspend fun loginWithMobile(
        mobileNumber: String,
        pin: String
    ): Result<UserAccountEntity> = withContext(Dispatchers.IO) {
        val cleanMobile = mobileNumber.replace(Regex("[^0-9]"), "")
        val user = userDao.getUserByMobile(cleanMobile)
        if (user == null) {
            return@withContext Result.failure(NoSuchElementException("No account found for mobile $cleanMobile. Please create an account."))
        }
        if (user.pin != pin) {
            return@withContext Result.failure(IllegalArgumentException("Incorrect security PIN. Please try again."))
        }

        userDao.clearActiveUsers()
        userDao.setActiveUser(cleanMobile)
        val refreshed = user.copy(isCurrentActive = true)
        _currentUser.value = refreshed
        Result.success(refreshed)
    }

    suspend fun switchUser(mobileNumber: String) = withContext(Dispatchers.IO) {
        userDao.clearActiveUsers()
        userDao.setActiveUser(mobileNumber)
        _currentUser.value = userDao.getUserByMobile(mobileNumber)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.clearActiveUsers()
        _currentUser.value = null
    }

    suspend fun updateDomain(newDomain: String) = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext
        val updated = current.copy(domain = newDomain)
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun changePin(oldPin: String, newPin: String): Boolean = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext false
        if (current.pin == oldPin) {
            userDao.updatePin(current.mobileNumber, newPin)
            _currentUser.value = current.copy(pin = newPin)

            val sms = SmsEntity(
                id = "sms-" + UUID.randomUUID().toString().take(6),
                userMobile = current.mobileNumber,
                sender = "SBS Security",
                message = "Your SBS Pro security PIN was updated. Keep it confidential.",
                timestamp = System.currentTimeMillis()
            )
            smsDao.insertSms(sms)
            true
        } else {
            false
        }
    }

    suspend fun executeRecharge(
        operatorCode: String,
        operatorName: String,
        number: String,
        type: RechargeNumberType,
        amount: Double,
        packageOffer: PackageOffer? = null
    ): Transaction = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val commission = if (packageOffer != null && packageOffer.cashback > 0) {
            packageOffer.cashback
        } else {
            amount * 0.028
        }

        // Deduct the requested amount from main balance (placed on hold pending admin approval)
        val newMain = (user.mainBalance - amount).coerceAtLeast(0.0)

        userDao.updateBalances(user.mobileNumber, newMain, user.driveBalance, user.bankBalance)
        _currentUser.value = user.copy(mainBalance = newMain)

        val trxId = "${operatorCode.take(2)}-" + Random.nextInt(100000, 999999)
        val txnEntity = TransactionEntity(
            id = "tx-" + UUID.randomUUID().toString().take(8),
            userMobile = user.mobileNumber,
            type = if (packageOffer != null) "Recharge (Drive)" else "Recharge",
            recipient = number,
            operatorOrProvider = "$operatorName (${type.label})",
            amount = amount,
            commission = commission,
            status = "PENDING",
            trxId = trxId,
            balanceAfter = newMain,
            reference = packageOffer?.title ?: "${type.label} Top-Up",
            timestamp = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(txnEntity)
        rtdbManager.pushTransaction(txnEntity, user.shopName)

        val sms = SmsEntity(
            id = "sms-" + UUID.randomUUID().toString().take(6),
            userMobile = user.mobileNumber,
            sender = operatorName,
            message = "Recharge request of TK $amount to $number submitted. TrxID: $trxId. Status: PENDING (Awaiting Admin Approval).",
            timestamp = System.currentTimeMillis()
        )
        smsDao.insertSms(sms)

        Transaction(
            id = txnEntity.id,
            type = txnEntity.type,
            recipient = txnEntity.recipient,
            operatorOrProvider = txnEntity.operatorOrProvider,
            amount = txnEntity.amount,
            commission = txnEntity.commission,
            status = TransactionStatus.PENDING,
            timestamp = txnEntity.timestamp,
            trxId = txnEntity.trxId,
            balanceAfter = txnEntity.balanceAfter,
            reference = txnEntity.reference,
            userMobile = txnEntity.userMobile,
            userShopName = user.shopName
        )
    }

    suspend fun executeMfs(
        mfsName: String,
        action: String,
        number: String,
        amount: Double
    ): Transaction = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val commissionRate = when (mfsName.lowercase()) {
            "bkash" -> 0.0041
            "rocket" -> 0.0045
            "nagad" -> 0.0040
            else -> 0.0040
        }
        val commission = amount * commissionRate
        val newMain = (user.mainBalance - amount).coerceAtLeast(0.0)

        userDao.updateBalances(user.mobileNumber, newMain, user.driveBalance, user.bankBalance)
        _currentUser.value = user.copy(mainBalance = newMain)

        val trxId = "${mfsName.take(2).uppercase()}-" + Random.nextInt(100000, 999999)
        val txnEntity = TransactionEntity(
            id = "tx-" + UUID.randomUUID().toString().take(8),
            userMobile = user.mobileNumber,
            type = mfsName,
            recipient = number,
            operatorOrProvider = "$mfsName $action",
            amount = amount,
            commission = commission,
            status = "PENDING",
            trxId = trxId,
            balanceAfter = newMain,
            reference = "$action for $number",
            timestamp = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(txnEntity)
        rtdbManager.pushTransaction(txnEntity, user.shopName)

        val sms = SmsEntity(
            id = "sms-" + UUID.randomUUID().toString().take(6),
            userMobile = user.mobileNumber,
            sender = mfsName,
            message = "$mfsName $action request of TK $amount to $number submitted. TrxID: $trxId. Status: PENDING (Awaiting Admin Approval).",
            timestamp = System.currentTimeMillis()
        )
        smsDao.insertSms(sms)

        Transaction(
            id = txnEntity.id,
            type = txnEntity.type,
            recipient = txnEntity.recipient,
            operatorOrProvider = txnEntity.operatorOrProvider,
            amount = txnEntity.amount,
            commission = txnEntity.commission,
            status = TransactionStatus.PENDING,
            timestamp = txnEntity.timestamp,
            trxId = txnEntity.trxId,
            balanceAfter = txnEntity.balanceAfter,
            reference = txnEntity.reference,
            userMobile = txnEntity.userMobile,
            userShopName = user.shopName
        )
    }

    suspend fun executeBillPay(
        providerName: String,
        accountNo: String,
        amount: Double,
        month: String
    ): Transaction = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val commission = 15.00
        val newMain = (user.mainBalance - amount).coerceAtLeast(0.0)

        userDao.updateBalances(user.mobileNumber, newMain, user.driveBalance, user.bankBalance)
        _currentUser.value = user.copy(mainBalance = newMain)

        val trxId = "BP-" + Random.nextInt(100000, 999999)
        val txnEntity = TransactionEntity(
            id = "tx-" + UUID.randomUUID().toString().take(8),
            userMobile = user.mobileNumber,
            type = "BillPay",
            recipient = accountNo,
            operatorOrProvider = providerName,
            amount = amount,
            commission = commission,
            status = "PENDING",
            trxId = trxId,
            balanceAfter = newMain,
            reference = "Bill Month: $month",
            timestamp = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(txnEntity)
        rtdbManager.pushTransaction(txnEntity, user.shopName)

        val sms = SmsEntity(
            id = "sms-" + UUID.randomUUID().toString().take(6),
            userMobile = user.mobileNumber,
            sender = "Utility BillPay",
            message = "Bill Payment request of TK $amount for Acct: $accountNo ($providerName) submitted. TrxID: $trxId. Status: PENDING (Awaiting Admin Approval).",
            timestamp = System.currentTimeMillis()
        )
        smsDao.insertSms(sms)

        Transaction(
            id = txnEntity.id,
            type = txnEntity.type,
            recipient = txnEntity.recipient,
            operatorOrProvider = txnEntity.operatorOrProvider,
            amount = txnEntity.amount,
            commission = txnEntity.commission,
            status = TransactionStatus.PENDING,
            timestamp = txnEntity.timestamp,
            trxId = txnEntity.trxId,
            balanceAfter = txnEntity.balanceAfter,
            reference = txnEntity.reference,
            userMobile = txnEntity.userMobile,
            userShopName = user.shopName
        )
    }

    // ==================== ADMIN PANEL ACTIONS ====================

    suspend fun approveTransaction(id: String): Result<Transaction> = withContext(Dispatchers.IO) {
        val txEntity = transactionDao.getTransactionById(id)
            ?: return@withContext Result.failure(NoSuchElementException("Transaction not found"))
        if (txEntity.status != "PENDING") {
            return@withContext Result.failure(IllegalStateException("Transaction is already ${txEntity.status}"))
        }

        transactionDao.updateTransactionStatus(id, "SUCCESS")
        rtdbManager.updateTransactionStatus(id, "SUCCESS")

        // Credit the earned commission (and drive cashback if applicable) upon approval
        val user = userDao.getUserByMobile(txEntity.userMobile)
        if (user != null) {
            val updatedMain = user.mainBalance + txEntity.commission
            val updatedDrive = if (txEntity.type.contains("Drive")) user.driveBalance + txEntity.commission else user.driveBalance
            userDao.updateBalances(user.mobileNumber, updatedMain, updatedDrive, user.bankBalance)
            val updatedUser = user.copy(mainBalance = updatedMain, driveBalance = updatedDrive)
            rtdbManager.syncUserAccount(updatedUser)
            if (_currentUser.value?.mobileNumber == user.mobileNumber) {
                _currentUser.value = updatedUser
            }
        }

        // SMS notification to the agent
        val sms = SmsEntity(
            id = "sms-" + UUID.randomUUID().toString().take(6),
            userMobile = txEntity.userMobile,
            sender = "Admin Approval",
            message = "APPROVED! Your ${txEntity.type} of TK ${txEntity.amount} to ${txEntity.recipient} has been approved by Admin. TrxID: ${txEntity.trxId}. Comm credited: TK ${"%.2f".format(txEntity.commission)}.",
            timestamp = System.currentTimeMillis()
        )
        smsDao.insertSms(sms)

        val updatedTx = txEntity.copy(status = "SUCCESS")
        val model = Transaction(
            id = updatedTx.id,
            type = updatedTx.type,
            recipient = updatedTx.recipient,
            operatorOrProvider = updatedTx.operatorOrProvider,
            amount = updatedTx.amount,
            commission = updatedTx.commission,
            status = TransactionStatus.SUCCESS,
            timestamp = updatedTx.timestamp,
            trxId = updatedTx.trxId,
            balanceAfter = updatedTx.balanceAfter,
            reference = updatedTx.reference,
            userMobile = updatedTx.userMobile,
            userShopName = user?.shopName ?: ""
        )
        Result.success(model)
    }

    suspend fun rejectTransaction(id: String, reason: String = "Rejected by Admin"): Result<Transaction> = withContext(Dispatchers.IO) {
        val txEntity = transactionDao.getTransactionById(id)
            ?: return@withContext Result.failure(NoSuchElementException("Transaction not found"))
        if (txEntity.status != "PENDING") {
            return@withContext Result.failure(IllegalStateException("Transaction is already ${txEntity.status}"))
        }

        transactionDao.updateTransactionStatus(id, "FAILED")
        rtdbManager.updateTransactionStatus(id, "FAILED")

        // Refund held amount back to user's main balance
        val user = userDao.getUserByMobile(txEntity.userMobile)
        if (user != null) {
            val refundedMain = user.mainBalance + txEntity.amount
            userDao.updateBalances(user.mobileNumber, refundedMain, user.driveBalance, user.bankBalance)
            val updatedUser = user.copy(mainBalance = refundedMain)
            rtdbManager.syncUserAccount(updatedUser)
            if (_currentUser.value?.mobileNumber == user.mobileNumber) {
                _currentUser.value = updatedUser
            }
        }

        // SMS notification to the agent
        val sms = SmsEntity(
            id = "sms-" + UUID.randomUUID().toString().take(6),
            userMobile = txEntity.userMobile,
            sender = "Admin Notice",
            message = "REJECTED by Admin: ${txEntity.type} of TK ${txEntity.amount} for ${txEntity.recipient} was rejected ($reason). TK ${txEntity.amount} refunded to your main balance.",
            timestamp = System.currentTimeMillis()
        )
        smsDao.insertSms(sms)

        val updatedTx = txEntity.copy(status = "FAILED")
        val model = Transaction(
            id = updatedTx.id,
            type = updatedTx.type,
            recipient = updatedTx.recipient,
            operatorOrProvider = updatedTx.operatorOrProvider,
            amount = updatedTx.amount,
            commission = updatedTx.commission,
            status = TransactionStatus.FAILED,
            timestamp = updatedTx.timestamp,
            trxId = updatedTx.trxId,
            balanceAfter = updatedTx.balanceAfter,
            reference = updatedTx.reference,
            userMobile = updatedTx.userMobile,
            userShopName = user?.shopName ?: ""
        )
        Result.success(model)
    }

    suspend fun approveAllPending(): Int = withContext(Dispatchers.IO) {
        val pendingList = _pendingTransactions.value
        var approvedCount = 0
        for (tx in pendingList) {
            val res = approveTransaction(tx.id)
            if (res.isSuccess) {
                approvedCount++
            }
        }
        approvedCount
    }

    suspend fun adminAddBalance(
        mobileNumber: String,
        amount: Double,
        balanceType: String, // "Main", "Drive", "Bank"
        remarks: String
    ): Result<UserAccountEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByMobile(mobileNumber)
            ?: return@withContext Result.failure(NoSuchElementException("Agent with mobile $mobileNumber not found"))

        val newMain = if (balanceType.equals("Main", ignoreCase = true)) user.mainBalance + amount else user.mainBalance
        val newDrive = if (balanceType.equals("Drive", ignoreCase = true)) user.driveBalance + amount else user.driveBalance
        val newBank = if (balanceType.equals("Bank", ignoreCase = true)) user.bankBalance + amount else user.bankBalance

        userDao.updateBalances(mobileNumber, newMain, newDrive, newBank)
        val updatedUser = user.copy(mainBalance = newMain, driveBalance = newDrive, bankBalance = newBank)
        rtdbManager.syncUserAccount(updatedUser)
        if (_currentUser.value?.mobileNumber == mobileNumber) {
            _currentUser.value = updatedUser
        }

        val note = if (remarks.isNotBlank()) " ($remarks)" else ""
        val sms = SmsEntity(
            id = "sms-" + UUID.randomUUID().toString().take(6),
            userMobile = mobileNumber,
            sender = "Admin Balance Transfer",
            message = "Admin credited TK ${"%,.2f".format(amount)} to your $balanceType Balance$note. New $balanceType: TK ${"%,.2f".format(if (balanceType == "Drive") newDrive else if (balanceType == "Bank") newBank else newMain)}.",
            timestamp = System.currentTimeMillis()
        )
        smsDao.insertSms(sms)

        Result.success(updatedUser)
    }

    fun testRtdbConnection(onComplete: (Boolean, String) -> Unit) {
        rtdbManager.testConnection(onComplete)
    }
}
