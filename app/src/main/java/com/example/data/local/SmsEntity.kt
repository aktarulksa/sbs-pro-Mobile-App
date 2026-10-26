package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_sms")
data class SmsEntity(
    @PrimaryKey
    val id: String,
    val userMobile: String,
    val sender: String,
    val message: String,
    val timestamp: Long
)
