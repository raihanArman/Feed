package com.raihanarman.feed

import app.cash.turbine.test
import com.raihanarman.feed.api.BadRequestException
import com.raihanarman.feed.api.ConnectivityException
import com.raihanarman.feed.api.CryptoFeedRetrofitHttpClient
import com.raihanarman.feed.api.CryptoFeedService
import com.raihanarman.feed.api.HttpClientResult
import com.raihanarman.feed.api.NotFoundException
import com.raihanarman.feed.api.RemoteCryptoFeedItem
import com.raihanarman.feed.api.RemoteRootCryptoFeed
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
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

    @Test
    fun testGetFailsOn400HttpResponse() = runBlocking {
        val response = Response.error<RemoteRootCryptoFeed>(400, ResponseBody.create(null, ""))

        coEvery {
            service.get()
        } throws HttpException(response)

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(BadRequestException()::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            service.get()
        }
    }

    @Test
    fun testGetFailsOn404HttpResponse() = runBlocking {
        val response = Response.error<RemoteRootCryptoFeed>(404, ResponseBody.create(null, ""))

        coEvery {
            service.get()
        } throws HttpException(response)

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(NotFoundException ()::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            service.get()
        }
    }

}