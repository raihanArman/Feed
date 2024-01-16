package com.raihanarman.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raihanarman.feed.api.BadRequest
import com.raihanarman.feed.api.Connectivity
import com.raihanarman.feed.api.InvalidData
import com.raihanarman.feed.api.NotFound
import com.raihanarman.feed.domain.CryptoFeed
import com.raihanarman.feed.domain.LoadCryptoFeedResult
import com.raihanarman.feed.domain.LoadCryptoFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @author Raihan Arman
 * @date 16/01/24
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
                                    is BadRequest -> "Permintaan gagal"
                                    is NotFound -> "Tidak ditemukan"
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