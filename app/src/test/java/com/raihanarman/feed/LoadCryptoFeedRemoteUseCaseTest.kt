package com.raihanarman.feed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author Raihan Arman
 * @date 18/11/23
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

class HttpClientSpy : HttpClient {
    var getCount = 0

    override fun get() {
        getCount += 1
    }
}

class LoadCryptoFeedRemoteUseCaseTest {

    @Test
    fun testInitDoesNotLoad() {
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(client)

        assertTrue(client.getCount == 0)
    }

    @Test
    fun testLoadRequestData() {
        // Given
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(client)

        // When
        sut.load()

        // Then
        assertEquals(1, client.getCount)
    }

}