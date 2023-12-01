package com.raihanarman.feed

import androidx.lifecycle.ViewModel
import com.raihanarman.feed.domain.CryptoFeed
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
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
        useCase.load()
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
}