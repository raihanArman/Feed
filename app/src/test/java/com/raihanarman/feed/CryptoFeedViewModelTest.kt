package com.raihanarman.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.raihanarman.feed.api.Connectivity
import com.raihanarman.feed.api.InvalidData
import com.raihanarman.feed.domain.CryptoFeed
import com.raihanarman.feed.domain.LoadCryptoFeedResult
import com.raihanarman.feed.domain.LoadCryptoFeedUseCase
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

data class UiState(
    val isLoading: Boolean = false,
    val cryptoFeed: List<CryptoFeed> = emptyList(),
    val failed: String = ""
)

class CryptoFeedViewModel(
    private val useCase: LoadCryptoFeedUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            useCase.load().collect { result ->
                _uiState.update {
                    when(result) {
                        is LoadCryptoFeedResult.Success -> TODO()
                        is LoadCryptoFeedResult.Failure -> {
                            it.copy(
                                isLoading = false,
                                failed = when(result.exception) {
                                    is Connectivity -> "Tidak ada internet"
                                    is InvalidData -> "Terjadi kesalahan"
                                    else -> {
                                        ""
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

}
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
            val receivedResult = awaitItem()
            assertEquals(expectedLoadingResult, receivedResult.isLoading)
            assertEquals(expectedFailedResult, receivedResult.failed)
            awaitComplete()
        }

        verify(exactly = 1) {
            useCase.load()
        }

        confirmVerified(useCase)
    }
}