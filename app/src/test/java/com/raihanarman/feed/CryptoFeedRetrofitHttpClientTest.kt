package com.raihanarman.feed

import app.cash.turbine.test
import com.raihanarman.feed.api.BadRequestException
import com.raihanarman.feed.api.ConnectivityException
import com.raihanarman.feed.api.CryptoFeedRetrofitHttpClient
import com.raihanarman.feed.api.CryptoFeedService
import com.raihanarman.feed.api.HttpClientResult
import com.raihanarman.feed.api.InternalServerErrorException
import com.raihanarman.feed.api.InvalidDataException
import com.raihanarman.feed.api.NotFoundException
import com.raihanarman.feed.api.RemoteCryptoFeedItem
import com.raihanarman.feed.api.RemoteRootCryptoFeed
import com.raihanarman.feed.api.UnexpectedException
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
    fun testGetFailsOnConnectivityError() {
        expect(
            sut = sut,
            expectedResult = ConnectivityException()
        )
    }

    @Test
    fun testGetFailsOn400HttpResponse() {
        expect(
            withStatusCode = 400,
            sut = sut,
            expectedResult = BadRequestException()
        )
    }

    @Test
    fun testGetFailsOn404HttpResponse() {
        expect(
            withStatusCode = 404,
            sut = sut,
            expectedResult = NotFoundException()
        )
    }

    @Test
    fun testGetFailsOn422HttpResponse() {
        expect(
            withStatusCode = 422,
            sut = sut,
            expectedResult = InvalidDataException()
        )
    }

    @Test
    fun testGetFailsOn500HttpResponse()  {
        expect(
            withStatusCode = 500,
            sut = sut,
            expectedResult = InternalServerErrorException()
        )
    }

    @Test
    fun testGetFailsOnUnexpectedException() {
        expect(
            sut = sut,
            expectedResult = UnexpectedException()
        )
    }

    private fun expect(
        withStatusCode: Int? = null,
        sut: CryptoFeedRetrofitHttpClient,
        expectedResult: Any,
    ) = runBlocking {
        when {
            withStatusCode != null -> {
                val response = Response.error<RemoteRootCryptoFeed>(withStatusCode, ResponseBody.create(null, ""))

                coEvery {
                    service.get()
                } throws HttpException(response)
            }

            expectedResult is ConnectivityException -> {
                coEvery {
                    service.get()
                } throws  IOException()
            }

            else -> {
                coEvery {
                    service.get()
                } throws Exception()
            }
        }

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(expectedResult::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            service.get()
        }
    }

}