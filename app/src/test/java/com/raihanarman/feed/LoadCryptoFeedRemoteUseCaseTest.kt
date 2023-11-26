package com.raihanarman.feed

import com.raihanarman.feed.api.Connectivity
import com.raihanarman.feed.api.HttpClient
import com.raihanarman.feed.api.LoadCryptoFeedRemoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
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

    @Test
    fun testLoadDeliversErrorOnClientError() = runBlocking {
        val (sut, client) = makeSut()

        client.error = Exception("Test")

        var captureException: Exception?= null
        sut.load().collect { error ->
            captureException = error
        }

        assertEquals(Connectivity::class.java, captureException?.javaClass)
    }

    private fun makeSut(): Pair<LoadCryptoFeedRemoteUseCase, HttpClientSpy> {
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(client)
        return Pair(sut, client)
    }

    private class HttpClientSpy : HttpClient {
        var getCount = 0
        var error: Exception ?= null

        override fun get(): Flow<Exception> = flow {
            if (error != null) {
                emit(error ?: java.lang.Exception())
            }


            getCount += 1
        }
    }

}