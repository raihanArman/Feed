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

sealed class CryptoFeedResult {
    data class Success(val cryptoFeed: List<CryptoFeed>): CryptoFeedResult()
    data class Error(val exception: Exception): CryptoFeedResult()
}

interface CryptoFeedUseCase {
    fun load(): Flow<CryptoFeedResult>
}