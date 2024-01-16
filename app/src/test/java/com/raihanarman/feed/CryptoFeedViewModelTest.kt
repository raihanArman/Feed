package com.raihanarman.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.raihanarman.feed.api.BadRequest
import com.raihanarman.feed.api.Connectivity
import com.raihanarman.feed.api.InvalidData
import com.raihanarman.feed.api.NotFound
import com.raihanarman.feed.domain.CryptoFeed
import com.raihanarman.feed.domain.LoadCryptoFeedResult
import com.raihanarman.feed.domain.LoadCryptoFeedUseCase
import com.raihanarman.feed.presentation.CryptoFeedViewModel
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * @author Raihan Arman
 * @date 01/12/23
 */

class CryptoFeedViewModelTest {
    private val useCase = spyk<LoadCryptoFeedUseCase>()
    private lateinit var sut: CryptoFeedViewModel

    @Before
    fun setUp() {
       MockKAnnotations.init(this, relaxed = true)

       sut = CryptoFeedViewModel(useCase = useCase)
       Dispatchers.setMain(UnconfinedTestDispatcher())
    }


    @Test
    fun testInitInitialState() {
        val uiState = sut.uiState.value

        assertFalse(uiState.isLoading)
        assertTrue(uiState.cryptoFeed.isEmpty())
        assert(uiState.failed.isEmpty())
    }

    @Test
    fun testInitDoesNotLoad() {
        verify(exactly = 0) {
            useCase.load()
        }

        confirmVerified(useCase)
    }

    @Test
    fun testLoadRequestsData() = runBlocking {
        every {
            useCase.load()
        } returns flowOf()

        sut.load()

        verify(exactly = 1) {
            useCase.load()
        }

        confirmVerified(useCase)
    }

    @Test
    fun testLoadTwiceRequestsDataTwice() = runBlocking {
        every {
            useCase.load()
        } returns flowOf()

        sut.load()
        sut.load()

        verify(exactly = 2) {
            useCase.load()
        }

        confirmVerified(useCase)
    }

    @Test
    fun testLoadIsLoadingState() = runBlocking {
        every {
            useCase.load()
        } returns flowOf()

        sut.load()

         sut.uiState.take(1).test {
            val receivedResult = awaitItem()
            assertEquals(true, receivedResult.isLoading)
            awaitComplete()
        }

        verify(exactly = 1) {
            useCase.load()
        }

        confirmVerified(useCase)
    }

    @Test
    fun testLoadFailedConnectivityShowsConnectivityError() = runBlocking {
        expect(
            result = LoadCryptoFeedResult.Failure(Connectivity()),
            sut = sut,
            expectedLoadingResult = false,
            "Tidak ada internet"
        )
    }

    @Test
    fun testLoadFailedInvalidDataErrorShowsError() = runBlocking {
        expect(
            result = LoadCryptoFeedResult.Failure(InvalidData()),
            sut = sut,
            expectedLoadingResult = false,
            "Terjadi kesalahan"
        )
    }

    @Test
    fun testLoadFailedBadRequestErrorShowsError() = runBlocking {
        expect(
            result = LoadCryptoFeedResult.Failure(BadRequest ()),
            sut = sut,
            expectedLoadingResult = false,
            "Permintaan gagal"
        )
    }

    @Test
    fun testLoadFailedNotFoundErrorShowsError() = runBlocking {
        expect(
            result = LoadCryptoFeedResult.Failure(NotFound()),
            sut = sut,
            expectedLoadingResult = false,
            "Tidak ditemukan"
        )
    }

    @Test
    fun testLoacSuccessShowsCryptoFeed() = runBlocking {
        expect(
            result = LoadCryptoFeedResult.Success(cryptoFeed),
            sut = sut,
            expectedLoadingResult = false,
            expectedFailedResult = ""
        )
    }


    private fun expect(
        result: LoadCryptoFeedResult,
        sut: CryptoFeedViewModel,
        expectedLoadingResult: Boolean,
        expectedFailedResult: String,
    ) = runBlocking  {
        every {
            useCase.load()
        } returns flowOf(result)

        sut.load()

        sut.uiState.take(1).test {
            val receiverResult = awaitItem()
            if (receiverResult.failed.isEmpty()) {
                assertEquals(expectedLoadingResult, receiverResult.isLoading)
                assertEquals(cryptoFeed, receiverResult.cryptoFeed)
                assertEquals(expectedFailedResult, receiverResult.failed)
            } else {
                assertEquals(expectedLoadingResult, receiverResult.isLoading)
                assertEquals(expectedFailedResult, receiverResult.failed)
            }

            awaitComplete()
        }

        verify(exactly = 1) {
            useCase.load()
        }

        confirmVerified(useCase)
    }
}