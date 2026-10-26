package com.example.model

enum class ServiceType(val displayName: String, val iconResName: String) {
    RECHARGE("Recharg", "globe"),
    BKASH("bKash", "mfs_bkash"),
    ROCKET("Rocket", "mfs_rocket"),
    NAGAD("Nagad", "mfs_nagad"),
    BILL_PAY_ELECTRIC("Bill Pay - Elect", "bill_electric"),
    BILL_PAY_GAS("Bill Pay - Gas/Water", "bill_gas")
}

data class Country(
    val id: String,
    val name: String,
    val flagId: String,
    val phonePrefix: String,
    val isDth: Boolean = false
)

data class Operator(
    val id: String,
    val code: String, // e.g. "GRA", "ROB", "BAN", "TEL", "AIR"
    val name: String,
    val countryId: String,
    val prefix: List<String> = emptyList(),
    val isDth: Boolean = false
)

enum class RechargeNumberType(val label: String) {
    PREPAID("Prepaid"),
    POSTPAID("Postpaid"),
    SKITTO("Skitto")
}

data class PackageOffer(
    val id: String,
    val operatorCode: String,
    val title: String,
    val dataVolume: String,
    val minutes: String,
    val validity: String,
    val price: Double,
    val cashback: Double = 0.0,
    val category: String = "Internet" // Internet, Minute, Combo, Drive
)

enum class TransactionStatus {
    SUCCESS,
    PENDING,
    FAILED
}

data class Transaction(
    val id: String,
    val type: String, // "Recharge", "bKash", "Rocket", "BillPay"
    val recipient: String,
    val operatorOrProvider: String,
    val amount: Double,
    val commission: Double,
    val status: TransactionStatus,
    val timestamp: Long,
    val trxId: String,
    val balanceAfter: Double,
    val reference: String = "",
    val userMobile: String = "",
    val userShopName: String = ""
)

data class SmsLog(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val isIncoming: Boolean = true
)

data class AgentProfile(
    val agentId: String = "AG-880194",
    val name: String = "Aktarul Telecom",
    val phone: String = "+880 1712-345678",
    val domain: String = "server.sbspro.com",
    val mainBalance: Double = 24850.50,
    val driveBalance: Double = 8420.00,
    val bankBalance: Double = 15200.00,
    val currentPin: String = "1234"
)

enum class ReportCategory {
    HISTORY,
    SMS,
    PREPAID,
    BILLPAY,
    RECEIVE,
    PAYMENT,
    RECENT,
    PROFIT_LOSS,
    DAILY_REPORT,
    DAYS_30,
    THIS_MONTH,
    LAST_MONTH,
    THREE_MONTH,
    YEARLY,
    CUSTOM_RANGE,
    RESELLER
}
