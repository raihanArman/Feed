package com.raihanarman.feed.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * @author Raihan Arman
 * @date 19/11/23
 */
class LoadCryptoFeedRemoteUseCase(
    private val httpClient: HttpClient
) {
    fun load(): Flow<Exception> = flow {
        httpClient.get().collect {
             emit(Connectivity())
        }
    }
}

interface HttpClient {
    fun get(): Flow<Exception>
}

class Connectivity: Exception()