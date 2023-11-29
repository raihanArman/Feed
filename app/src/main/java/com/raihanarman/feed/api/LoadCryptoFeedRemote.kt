package com.raihanarman.feed.api

import com.raihanarman.feed.domain.LoadCryptoFeedResult
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
    fun load(): Flow<LoadCryptoFeedResult> = flow {
        httpClient.get().collect { result  ->
            when(result) {
                is HttpClientResult.Failure -> {
                    when(result.exception) {
                        is ConnectivityException -> {
                            emit(LoadCryptoFeedResult.Failure(Connectivity()))
                        }
                        is InvalidDataException -> {
                            emit(LoadCryptoFeedResult.Failure(InvalidData()))
                        }
                        is BadRequestException -> {
                            emit(LoadCryptoFeedResult.Failure(BadRequest()))
                        }
                        is ServerErrorException -> {
                            emit(LoadCryptoFeedResult.Failure(ServerError()))
                        }
                    }
                }
            }
        }
    }
}

sealed class HttpClientResult {
    data class Failure(val exception: Exception): HttpClientResult()
}

interface HttpClient {
    fun get(): Flow<HttpClientResult>
}

class Connectivity: Exception()
class ConnectivityException: Exception()
class InvalidDataException: Exception()

class BadRequestException: Exception()

class ServerErrorException: Exception()
class InvalidData: Exception()

class BadRequest: Exception()

class ServerError: Exception()