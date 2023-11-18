package com.raihanarman.feed.domain

import kotlinx.coroutines.flow.Flow
import java.lang.Exception

/**
 * @author Raihan Arman
 * @date 18/11/23
 */

/**
 * Boundaries interface
 *
 * Separate module-module
 */

sealed class LoadCryptoFeedResult {
    data class Success(val cryptoFeed: List<CryptoFeed>): LoadCryptoFeedResult()
    data class Error(val exception: Exception): LoadCryptoFeedResult()
}

interface LoadCryptoFeedUseCase {
    fun load(): Flow<LoadCryptoFeedResult>
}