package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccountEntity(
    @PrimaryKey
    val mobileNumber: String,
    val name: String,
    val shopName: String,
    val pin: String,
    val domain: String = "server.sbspro.com",
    val mainBalance: Double = 25000.0,
    val driveBalance: Double = 5000.0,
    val bankBalance: Double = 10000.0,
    val accountType: String = "Reseller Agent",
    val operator: String = "Grameenphone",
    val createdAt: Long = System.currentTimeMillis(),
    val isCurrentActive: Boolean = false
) {
    @get:androidx.room.Ignore
    val isAdmin: Boolean
        get() = accountType.equals("Administrator", ignoreCase = true) ||
                accountType.equals("Admin", ignoreCase = true) ||
                accountType.equals("Super Admin", ignoreCase = true) ||
                mobileNumber == "01900000000"
}
