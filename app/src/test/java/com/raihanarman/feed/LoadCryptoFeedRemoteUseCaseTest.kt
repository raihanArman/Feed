package com.raihanarman.feed

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author Raihan Arman
 * @date 18/11/23
 */

class LoadCryptoFeedRemoteUseCase {

}

class HttpClient {
    var getCount = 0
}

class LoadCryptoFeedRemoteUseCaseTest {

    @Test
    fun testInitDoesNotLoad() {
        val client = HttpClient()
        val sut = LoadCryptoFeedRemoteUseCase()

        assertTrue(client.getCount == 0)
    }

}