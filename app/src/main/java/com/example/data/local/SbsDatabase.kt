package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserAccountEntity::class,
        TransactionEntity::class,
        SmsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SbsDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun smsDao(): SmsDao

    companion object {
        @Volatile
        private var INSTANCE: SbsDatabase? = null

        fun getDatabase(context: Context): SbsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SbsDatabase::class.java,
                    "sbs_pro_local.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
