package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SbsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    private lateinit var repository: SbsRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = SbsRepository(context)
    }

    @Test
    fun testInitialCountriesAndOperators() {
        val countries = repository.countries
        assertTrue(countries.any { it.id == "bd" && it.name == "Bangladesh" })
        assertTrue(countries.any { it.id == "in" && it.name == "India" })

        val bdOperators = repository.operators["bd"]
        assertNotNull(bdOperators)
        assertTrue(bdOperators!!.any { it.code == "GRA" && it.name == "GrameenPhone" })
        assertTrue(bdOperators.any { it.code == "ROB" && it.name == "Robi" })
        assertTrue(bdOperators.any { it.code == "BAN" && it.name == "Banglalink" })
    }

    @Test
    fun testCreateAccountWithMobileOnly() = runBlocking {
        val result = repository.createAccount(
            mobileNumber = "01899112233",
            name = "Test Agent",
            shopName = "Test Shop",
            pin = "4321",
            initialBalance = 30000.0,
            accountType = "Master Reseller"
        )

        assertTrue(result.isSuccess)
        val user = result.getOrThrow()
        assertEquals("01899112233", user.mobileNumber)
        assertEquals("Test Agent", user.name)
        assertEquals(30000.0, user.mainBalance, 0.01)
        assertEquals("Robi", user.operator)

        // Login with mobile and PIN
        val loginResult = repository.loginWithMobile("01899112233", "4321")
        assertTrue(loginResult.isSuccess)

        // Wrong PIN should fail
        val wrongPinResult = repository.loginWithMobile("01899112233", "0000")
        assertTrue(wrongPinResult.isFailure)
    }

    @Test
    fun testTransactionApprovalFlow() = runBlocking {
        // Create account
        val userRes = repository.createAccount(
            mobileNumber = "01755667788",
            name = "Approval Agent",
            shopName = "Approval Shop",
            pin = "1122",
            initialBalance = 10000.0,
            accountType = "Master Reseller"
        )
        assertTrue(userRes.isSuccess)
        repository.loginWithMobile("01755667788", "1122")

        // Submit MFS transaction (Agent Banking)
        val txn = repository.executeMfs(
            mfsName = "bKash",
            action = "Cash In",
            number = "01999887766",
            amount = 1000.0
        )

        // Must be in PENDING state awaiting Admin approval
        assertEquals(com.example.model.TransactionStatus.PENDING, txn.status)
        assertEquals("01755667788", txn.userMobile)

        // Admin approves transaction
        val approveRes = repository.approveTransaction(txn.id)
        assertTrue(approveRes.isSuccess)
        val approvedTxn = approveRes.getOrThrow()
        assertEquals(com.example.model.TransactionStatus.SUCCESS, approvedTxn.status)
    }

    @Test
    fun testTransactionRejectionAndRefund() = runBlocking {
        val userRes = repository.createAccount(
            mobileNumber = "01611223344",
            name = "Refund Agent",
            shopName = "Refund Shop",
            pin = "5566",
            initialBalance = 5000.0,
            accountType = "Basic Reseller"
        )
        assertTrue(userRes.isSuccess)
        val initialUser = userRes.getOrThrow()
        repository.loginWithMobile("01611223344", "5566")

        // Submit recharge of 500 TK
        val txn = repository.executeRecharge(
            operatorCode = "AIR",
            operatorName = "Airtel",
            number = "01611000000",
            type = com.example.model.RechargeNumberType.PREPAID,
            amount = 500.0
        )
        assertEquals(com.example.model.TransactionStatus.PENDING, txn.status)

        // Admin rejects transaction
        val rejectRes = repository.rejectTransaction(txn.id, "Invalid recipient test")
        assertTrue(rejectRes.isSuccess)
        val rejectedTxn = rejectRes.getOrThrow()
        assertEquals(com.example.model.TransactionStatus.FAILED, rejectedTxn.status)

        // Check that user balance was refunded
        val refreshed = repository.currentUser.value
        assertNotNull(refreshed)
        assertEquals(initialUser.mainBalance, refreshed!!.mainBalance, 0.01)
    }

    @Test
    fun testAdminAddBalance() = runBlocking {
        val userRes = repository.createAccount(
            mobileNumber = "01599887766",
            name = "Credit Agent",
            shopName = "Credit Shop",
            pin = "9988",
            initialBalance = 2000.0,
            accountType = "Retailer"
        )
        assertTrue(userRes.isSuccess)

        val creditRes = repository.adminAddBalance(
            mobileNumber = "01599887766",
            amount = 3500.0,
            balanceType = "Main",
            remarks = "Test Bank Deposit"
        )
        assertTrue(creditRes.isSuccess)
        val creditedUser = creditRes.getOrThrow()
        assertEquals(5500.0, creditedUser.mainBalance, 0.01)
    }

    @Test
    fun testRegularUsersCannotSeeAdminControlPanel() = runBlocking {
        // Create standard agent user
        val agentRes = repository.createAccount(
            mobileNumber = "01788776655",
            name = "Regular Agent User",
            shopName = "Village Telecom",
            pin = "4455",
            initialBalance = 15000.0,
            accountType = "Reseller Agent"
        )
        assertTrue(agentRes.isSuccess)
        val agent = agentRes.getOrThrow()

        // Standard user MUST NOT be an admin
        assertFalse(agent.isAdmin)

        // Master Reseller or Retailer MUST NOT be an admin
        val retailerRes = repository.createAccount(
            mobileNumber = "01822334455",
            name = "Retailer User",
            shopName = "City Point",
            pin = "1122",
            initialBalance = 5000.0,
            accountType = "Retailer"
        )
        val retailer = retailerRes.getOrThrow()
        assertFalse(retailer.isAdmin)

        // Only dedicated Administrator accounts have isAdmin == true
        val seededAdminRes = repository.loginWithMobile("01900000000", "7788")
        assertTrue(seededAdminRes.isSuccess)
        val seededAdmin = seededAdminRes.getOrThrow()
        assertTrue(seededAdmin.isAdmin)

        // Creating another Administrator account also yields isAdmin == true
        val customAdminRes = repository.createAccount(
            mobileNumber = "01988887766",
            name = "Regional Super Admin",
            shopName = "SBS Pro Division 2",
            pin = "7788",
            initialBalance = 100000.0,
            accountType = "Administrator"
        )
        assertTrue(customAdminRes.isSuccess)
        val customAdmin = customAdminRes.getOrThrow()
        assertTrue(customAdmin.isAdmin)
    }
}
