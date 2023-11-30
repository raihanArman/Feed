package com.raihanarman.feed.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

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
            when(e) {
                is IOException -> {
                    emit(HttpClientResult.Failure(ConnectivityException()))
                }

                is HttpException -> {
                    when(e.code()) {
                         400 -> {
                             emit(HttpClientResult.Failure(BadRequestException()))
                         }
                    }
                }
            }
        }
    }
}