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
    data class Failure(val exception: Exception): LoadCryptoFeedResult()
}

interface LoadCryptoFeedUseCase {
    fun load(): Flow<LoadCryptoFeedResult>
}