package com.raihanarman.feed

import app.cash.turbine.test
import com.raihanarman.feed.api.ConnectivityException
import com.raihanarman.feed.api.CryptoFeedRetrofitHttpClient
import com.raihanarman.feed.api.CryptoFeedService
import com.raihanarman.feed.api.HttpClientResult
import com.raihanarman.feed.api.RemoteCryptoFeedItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * @author Raihan Arman
 * @date 30/11/23
 */

class CryptoFeedRetrofitHttpClientTest {
    private val service = mockk<CryptoFeedService>()
    private lateinit var sut: CryptoFeedRetrofitHttpClient

    @Before
    fun setUp() {
        sut = CryptoFeedRetrofitHttpClient(service = service)
    }

    @Test
    fun testGetFailsOnConnectivityError() = runBlocking {
        coEvery {
            service.get()
        } throws IOException()

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(ConnectivityException()::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            service.get()
        }
    }

}