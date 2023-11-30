package com.raihanarman.feed.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author Raihan Arman
 * @date 30/11/23
 */
class CryptoFeedRetrofitHttpClient(
    private val service: CryptoFeedService
) {
    fun get(): Flow<HttpClientResult> = flow {
        try {
            service.get()
        } catch (e: Exception) {
            emit(HttpClientResult.Failure(ConnectivityException()))
        }
    }
}