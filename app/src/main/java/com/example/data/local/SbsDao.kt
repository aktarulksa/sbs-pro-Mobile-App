package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE mobileNumber = :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): UserAccountEntity?

    @Query("SELECT * FROM users WHERE isCurrentActive = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("UPDATE users SET isCurrentActive = 0")
    suspend fun clearActiveUsers()

    @Query("UPDATE users SET isCurrentActive = 1 WHERE mobileNumber = :mobile")
    suspend fun setActiveUser(mobile: String)

    @Query("UPDATE users SET pin = :newPin WHERE mobileNumber = :mobile")
    suspend fun updatePin(mobile: String, newPin: String)

    @Query("UPDATE users SET mainBalance = :main, driveBalance = :drive, bankBalance = :bank WHERE mobileNumber = :mobile")
    suspend fun updateBalances(mobile: String, main: Double, drive: Double, bank: Double)

    @Query("UPDATE users SET mainBalance = mainBalance + :amount WHERE mobileNumber = :mobile")
    suspend fun addMainBalance(mobile: String, amount: Double)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM user_transactions WHERE userMobile = :mobile ORDER BY timestamp DESC")
    fun getTransactionsForUser(mobile: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM user_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM user_transactions WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM user_transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("UPDATE user_transactions SET status = :status WHERE id = :id")
    suspend fun updateTransactionStatus(id: String, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntity)
}

@Dao
interface SmsDao {
    @Query("SELECT * FROM user_sms WHERE userMobile = :mobile ORDER BY timestamp DESC")
    fun getSmsForUser(mobile: String): Flow<List<SmsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSms(sms: SmsEntity)
}
