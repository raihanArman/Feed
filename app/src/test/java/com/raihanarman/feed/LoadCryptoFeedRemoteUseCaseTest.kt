package com.raihanarman.feed

import com.raihanarman.feed.api.HttpClient
import com.raihanarman.feed.api.LoadCryptoFeedRemoteUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author Raihan Arman
 * @date 18/11/23
 */

class LoadCryptoFeedRemoteUseCaseTest {

    @Test
    fun testInitDoesNotLoad() {
        val (_, client) = makeSut()

        assertTrue(client.getCount == 0)
    }

    @Test
    fun testLoadRequestData() {
        // Given
        val (sut, client) = makeSut()

        // When
        sut.load()

        // Then
        assertEquals(1, client.getCount)
    }

    @Test
    fun testLoadRequestDataTwice() {
        // Given
        val (sut, client) = makeSut()

        // When
        sut.load()
        sut.load()

        // Then
        assertEquals(2, client.getCount)
    }

    private fun makeSut(): Pair<LoadCryptoFeedRemoteUseCase, HttpClientSpy> {
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(client)
        return Pair(sut, client)
    }

    private class HttpClientSpy : HttpClient {
        var getCount = 0

        override fun get() {
            getCount += 1
        }
    }

}