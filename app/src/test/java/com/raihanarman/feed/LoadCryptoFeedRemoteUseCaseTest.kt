package com.raihanarman.feed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author Raihan Arman
 * @date 18/11/23
 */

class LoadCryptoFeedRemoteUseCase {
    fun load() {
        HttpClient.instance.get()
    }
}

open class HttpClient {
    companion object {
        var instance = HttpClient()
    }

    open fun get() {

    }
}

class HttpClientSpy: HttpClient() {
    var getCount = 0

    override fun get() {
        getCount += 1
    }
}

class LoadCryptoFeedRemoteUseCaseTest {

    @Test
    fun testInitDoesNotLoad() {
        val client = HttpClientSpy()
        HttpClient.instance = client
        val sut = LoadCryptoFeedRemoteUseCase()

        assertTrue(client.getCount == 0)
    }

    @Test
    fun testLoadRequestData() {
        // Given
        val client = HttpClientSpy()
        HttpClient.instance = client
        val sut = LoadCryptoFeedRemoteUseCase()

        // When
        sut.load()

        // Then
        assertEquals(1, client.getCount)
    }

}