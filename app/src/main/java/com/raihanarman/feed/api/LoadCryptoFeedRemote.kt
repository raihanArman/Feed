package com.raihanarman.feed.api

/**
 * @author Raihan Arman
 * @date 19/11/23
 */
class LoadCryptoFeedRemoteUseCase(
    private val httpClient: HttpClient
) {
    fun load() {
        httpClient.get()
    }
}

interface HttpClient {
    fun get()
}