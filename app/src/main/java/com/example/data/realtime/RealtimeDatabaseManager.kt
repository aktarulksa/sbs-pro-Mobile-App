package com.example.data.realtime

import android.content.Context
import android.util.Log
import com.example.data.local.TransactionEntity
import com.example.data.local.UserAccountEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Realtime Database data model for transactions.
 * Default values required for Firebase deserialization.
 */
data class RtdbTransaction(
    var id: String = "",
    var userMobile: String = "",
    var type: String = "",
    var recipient: String = "",
    var operatorOrProvider: String = "",
    var amount: Double = 0.0,
    var commission: Double = 0.0,
    var status: String = "PENDING",
    var trxId: String = "",
    var balanceAfter: Double = 0.0,
    var reference: String = "",
    var timestamp: Long = 0L,
    var userShopName: String = ""
)

data class RtdbUserAccount(
    var mobileNumber: String = "",
    var name: String = "",
    var shopName: String = "",
    var domain: String = "server.sbspro.com",
    var mainBalance: Double = 0.0,
    var driveBalance: Double = 0.0,
    var bankBalance: Double = 0.0,
    var accountType: String = "Reseller Agent",
    var operator: String = "",
    var lastUpdated: Long = 0L
)

class RealtimeDatabaseManager(private val context: Context) {

    companion object {
        const val RTDB_URL = "https://gen-lang-client-0892038385-default-rtdb.firebaseio.com/"
        private const val TAG = "RealtimeDbManager"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _connectionStatus = MutableStateFlow("Initializing Realtime Database...")
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()

    private val _lastSyncedTxId = MutableStateFlow<String?>(null)
    val lastSyncedTxId: StateFlow<String?> = _lastSyncedTxId.asStateFlow()

    private var database: FirebaseDatabase? = null
    private var transactionsRef: DatabaseReference? = null
    private var pendingRef: DatabaseReference? = null
    private var usersRef: DatabaseReference? = null

    // Callback when remote transaction changes (e.g., admin approves from another device)
    var onRemoteTransactionUpdated: ((RtdbTransaction) -> Unit)? = null
    var onRemoteUserBalanceUpdated: ((mobile: String, main: Double, drive: Double, bank: Double) -> Unit)? = null

    init {
        initFirebaseRtdb()
    }

    private fun initFirebaseRtdb() {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(context)
                } catch (e: Exception) {
                    Log.w(TAG, "FirebaseApp.initializeApp failed: ${e.message}")
                }
            }

            val dbInstance = FirebaseDatabase.getInstance(RTDB_URL)
            try {
                dbInstance.setPersistenceEnabled(true)
            } catch (e: Exception) {
                // Persistence can only be set once per process run
            }

            database = dbInstance
            transactionsRef = dbInstance.getReference("transactions")
            pendingRef = dbInstance.getReference("pending_transactions")
            usersRef = dbInstance.getReference("users")

            // Listen to connected status
            val connectedRef = dbInstance.getReference(".info/connected")
            connectedRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    _isConnected.value = connected
                    _connectionStatus.value = if (connected) {
                        "🟢 Connected to Realtime Database"
                    } else {
                        "🟡 Offline Cache Active (Syncs when connected)"
                    }
                    Log.d(TAG, "RTDB Connection state: $connected")
                }

                override fun onCancelled(error: DatabaseError) {
                    _isConnected.value = false
                    _connectionStatus.value = "🔴 Connection Error: ${error.message}"
                }
            })

            listenToRemoteTransactions()
            listenToRemoteUsers()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Realtime Database: ${e.message}", e)
            _isConnected.value = false
            _connectionStatus.value = "⚠️ RTDB Standby (${e.localizedMessage ?: "Offline"})"
        }
    }

    private fun listenToRemoteTransactions() {
        val ref = transactionsRef ?: return
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val rtdbTx = snapshot.getValue(RtdbTransaction::class.java) ?: return
                onRemoteTransactionUpdated?.invoke(rtdbTx)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val rtdbTx = snapshot.getValue(RtdbTransaction::class.java) ?: return
                onRemoteTransactionUpdated?.invoke(rtdbTx)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Transactions listener cancelled: ${error.message}")
            }
        })
    }

    private fun listenToRemoteUsers() {
        val ref = usersRef ?: return
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(RtdbUserAccount::class.java) ?: return
                onRemoteUserBalanceUpdated?.invoke(user.mobileNumber, user.mainBalance, user.driveBalance, user.bankBalance)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(RtdbUserAccount::class.java) ?: return
                onRemoteUserBalanceUpdated?.invoke(user.mobileNumber, user.mainBalance, user.driveBalance, user.bankBalance)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Users listener cancelled: ${error.message}")
            }
        })
    }

    /**
     * Upload or update a transaction in the Realtime Database.
     */
    fun pushTransaction(entity: TransactionEntity, userShopName: String = "") {
        scope.launch {
            try {
                val tx = RtdbTransaction(
                    id = entity.id,
                    userMobile = entity.userMobile,
                    type = entity.type,
                    recipient = entity.recipient,
                    operatorOrProvider = entity.operatorOrProvider,
                    amount = entity.amount,
                    commission = entity.commission,
                    status = entity.status,
                    trxId = entity.trxId,
                    balanceAfter = entity.balanceAfter,
                    reference = entity.reference,
                    timestamp = entity.timestamp,
                    userShopName = userShopName
                )
                transactionsRef?.child(entity.id)?.setValue(tx)
                if (entity.status == "PENDING") {
                    pendingRef?.child(entity.id)?.setValue(tx)
                } else {
                    pendingRef?.child(entity.id)?.removeValue()
                }
                _lastSyncedTxId.value = entity.id
                Log.d(TAG, "Pushed transaction ${entity.id} to Realtime Database")
            } catch (e: Exception) {
                Log.e(TAG, "Failed pushing transaction to RTDB: ${e.message}")
            }
        }
    }

    /**
     * Update transaction approval status in the Realtime Database.
     */
    fun updateTransactionStatus(txId: String, newStatus: String) {
        scope.launch {
            try {
                transactionsRef?.child(txId)?.child("status")?.setValue(newStatus)
                if (newStatus == "PENDING") {
                    transactionsRef?.child(txId)?.get()?.addOnSuccessListener { snap ->
                        val rtdbTx = snap.getValue(RtdbTransaction::class.java)
                        if (rtdbTx != null) {
                            pendingRef?.child(txId)?.setValue(rtdbTx)
                        }
                    }
                } else {
                    pendingRef?.child(txId)?.removeValue()
                }
                Log.d(TAG, "Updated transaction $txId status to $newStatus in RTDB")
            } catch (e: Exception) {
                Log.e(TAG, "Failed updating transaction status in RTDB: ${e.message}")
            }
        }
    }

    /**
     * Sync user account and balance updates to Realtime Database.
     */
    fun syncUserAccount(user: UserAccountEntity) {
        scope.launch {
            try {
                val rtdbUser = RtdbUserAccount(
                    mobileNumber = user.mobileNumber,
                    name = user.name,
                    shopName = user.shopName,
                    domain = user.domain,
                    mainBalance = user.mainBalance,
                    driveBalance = user.driveBalance,
                    bankBalance = user.bankBalance,
                    accountType = user.accountType,
                    operator = user.operator,
                    lastUpdated = System.currentTimeMillis()
                )
                usersRef?.child(user.mobileNumber)?.setValue(rtdbUser)
                Log.d(TAG, "Synced user ${user.mobileNumber} to RTDB")
            } catch (e: Exception) {
                Log.e(TAG, "Failed syncing user to RTDB: ${e.message}")
            }
        }
    }

    /**
     * Trigger manual sync test ping to write metadata into RTDB.
     */
    fun testConnection(onComplete: (Boolean, String) -> Unit) {
        val db = database
        if (db == null) {
            onComplete(false, "Realtime Database is not initialized")
            return
        }
        val pingRef = db.getReference("system_health/last_ping")
        val timestamp = System.currentTimeMillis()
        pingRef.setValue(mapOf(
            "timestamp" to timestamp,
            "status" to "OK",
            "app" to "SBS Pro",
            "url" to RTDB_URL
        )).addOnSuccessListener {
            onComplete(true, "Successfully connected to Realtime Database!")
        }.addOnFailureListener { e ->
            onComplete(false, "Failed: ${e.message}")
        }
    }
}
