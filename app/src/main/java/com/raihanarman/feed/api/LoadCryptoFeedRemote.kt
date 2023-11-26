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
        httpClient.get().collect { error ->
            when(error) {
                is ConnectivityException -> {
                    emit(Connectivity())
                }
                is InvalidDataException -> {
                    emit(InvalidData())
                }
                is BadRequestException -> {
                    emit(BadRequest())
                }
                is ServerErrorException -> {
                    emit(ServerError())
                }
            }
        }
    }
}

sealed class HttpClientResult {
    object Success: HttpClientResult()
    object Failure: HttpClientResult()
}

interface HttpClient {
    fun get(): Flow<Exception>
}

class Connectivity: Exception()
class ConnectivityException: Exception()
class InvalidDataException: Exception()

class BadRequestException: Exception()

class ServerErrorException: Exception()
class InvalidData: Exception()

class BadRequest: Exception()

class ServerError: Exception()