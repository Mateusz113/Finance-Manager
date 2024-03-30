package com.mateusz113.financemanager.data.repository

import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.data.mapper.toNewPaymentDetails
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.model.TestPaymentInformation
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FakePaymentRepositoryTest {
    private lateinit var repository: PaymentRepository

    @Before
    fun setUp() {
        repository = FakePaymentRepository()
        val paymentsToInsert = mutableListOf<TestPaymentInformation>()
        ('A'..'Z').forEachIndexed { index, char ->
            paymentsToInsert.add(
                TestPaymentInformation(
                    id = index.toString(),
                    title = char.toString(),
                    description = char.toString(),
                    amount = index.toDouble(),
                    date = LocalDate.now(),
                    category = Category.values().random(),
                    photoUrls = emptyList(),
                    photoUris = emptyList(),
                    deletedPhotos = emptyList()
                )
            )
        }
        paymentsToInsert.shuffle()
        runBlocking {
            paymentsToInsert.forEach { repository.addPayment(it.toNewPaymentDetails()) }
        }
    }

    @Test
    fun `Get payment listings from populated repository, returns list of payment listings`() =
        runTest {
            val paymentListings = repository.getPaymentListings().first().data
            assertThat(paymentListings).isNotEmpty()
        }

    @Test
    fun `Get payment listings from empty repository, returns empty list`() = runTest {
        repository.getPaymentListings().first().data?.forEach {
            repository.removePayment(it.id)
        }
        val paymentListings = repository.getPaymentListings().first().data
        assertThat(paymentListings).isEmpty()
    }

    @Test
    fun `Get payment listings with filter from populated repository, returns payments list matching filter`() =
        runTest {
            val filter = FilterSettings(
                query = "A"
            )
            val paymentListings = repository.getPaymentListingsWithFilter(filter).first().data
            paymentListings?.forEach {
                assertThat(
                    it.title.lowercase().contains(filter.query.lowercase())
                            && (filter.categories.isEmpty() ||
                            filter.categories.contains(it.category))
                            && (filter.minValue.isBlank() ||
                            filter.minValue.toDouble() <= it.amount)
                            && (filter.maxValue.isBlank() ||
                            filter.maxValue.toDouble() >= it.amount)
                            && filter.startDate <= it.date
                            && filter.endDate >= it.date
                ).isTrue()
            }
        }

    @Test
    fun `Get payment details from payment listing id, returns payment details with matching data`() =
        runTest {
            val paymentListings = repository.getPaymentListings().first().data
            paymentListings?.forEach { listing ->
                val details = repository.getPaymentDetails(listing.id).first().data
                details?.let {
                    assertThat(
                        listing.title == it.title &&
                                listing.amount == it.amount &&
                                listing.date == it.date &&
                                listing.category == it.category
                    ).isTrue()
                }
            }
        }

    @Test
    fun `Get payment details from not existent id, throws error`() =
        runTest {
            repository.getPaymentDetails("not existent id").collect {
                assertThat(it.message).contains("Payment with given ID not found")
            }
        }

    @Test
    fun `Add payment, new payment is in repository`() = runTest {
        val sizeBeforeAddition = repository.getPaymentListings().first().data!!.size
        repository.addPayment(
            NewPaymentDetails(
                title = "title",
                description = "desc",
                amount = 10.toDouble(),
                photoUrls = emptyList(),
                photoUris = emptyList(),
                deletedPhotos = emptyList(),
                date = LocalDate.now(),
                category = Category.Health
            )
        )
        val sizeAfterAddition = repository.getPaymentListings().first().data!!.size
        assertThat(sizeBeforeAddition).isLessThan(sizeAfterAddition)
    }

    @Test
    fun `Edit payment with given id, retrieves data after edition`() = runTest {
        val newTitle = "New title"
        val paymentListings = repository.getPaymentListings().first().data!!
        paymentListings.first().run {
            val details = repository.getPaymentDetails(this.id).first().data!!
            repository.editPayment(
                id = this.id,
                newPaymentDetails = NewPaymentDetails(
                    title = newTitle,
                    description = details.description,
                    amount = details.amount,
                    photoUris = emptyList(),
                    photoUrls = emptyList(),
                    deletedPhotos = emptyList(),
                    date = details.date,
                    category = details.category
                )
            )
            assertThat(repository.getPaymentDetails(this.id).first().data!!.title).contains(newTitle)
        }
    }

    @Test
    fun `Remove payments with given ids, throws error when accessing deleted payments`() = runTest {
        repository.getPaymentListings().first().data?.forEach {
            repository.removePayment(it.id)
            repository.getPaymentDetails(it.id).collect { resource ->
                assertThat(resource.message).contains("Payment with given ID not found")
            }
        }
    }
}
