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
        HttpClient.instance.getCount = 1
    }
}

class HttpClient private constructor(){
    companion object {
        val instance = HttpClient()
    }
    var getCount = 0
}

class LoadCryptoFeedRemoteUseCaseTest {

    @Test
    fun testInitDoesNotLoad() {
        val client = HttpClient.instance
        val sut = LoadCryptoFeedRemoteUseCase()

        assertTrue(client.getCount == 0)
    }

    @Test
    fun testLoadRequestData() {
        // Given
        val client = HttpClient.instance
        val sut = LoadCryptoFeedRemoteUseCase()

        // When
        sut.load()

        // Then
        assertEquals(1, client.getCount)
    }

}