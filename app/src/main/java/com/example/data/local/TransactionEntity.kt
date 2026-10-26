package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val userMobile: String,
    val type: String,
    val recipient: String,
    val operatorOrProvider: String,
    val amount: Double,
    val commission: Double,
    val status: String,
    val trxId: String,
    val balanceAfter: Double,
    val reference: String,
    val timestamp: Long
)
