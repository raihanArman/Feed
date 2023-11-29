package com.raihanarman.feed

import app.cash.turbine.test
import com.raihanarman.feed.api.BadRequest
import com.raihanarman.feed.api.BadRequestException
import com.raihanarman.feed.api.Connectivity
import com.raihanarman.feed.api.ConnectivityException
import com.raihanarman.feed.api.HttpClient
import com.raihanarman.feed.api.HttpClientResult
import com.raihanarman.feed.api.InvalidData
import com.raihanarman.feed.api.InvalidDataException
import com.raihanarman.feed.api.LoadCryptoFeedRemoteUseCase
import com.raihanarman.feed.api.NotFound
import com.raihanarman.feed.api.NotFoundException
import com.raihanarman.feed.api.RemoteCoinInfo
import com.raihanarman.feed.api.RemoteCryptoFeedItem
import com.raihanarman.feed.api.RemoteDisplay
import com.raihanarman.feed.api.RemoteRootCryptoFeed
import com.raihanarman.feed.api.RemoteUsd
import com.raihanarman.feed.api.ServerError
import com.raihanarman.feed.api.ServerErrorException
import com.raihanarman.feed.domain.CoinInfo
import com.raihanarman.feed.domain.CryptoFeed
import com.raihanarman.feed.domain.LoadCryptoFeedResult
import com.raihanarman.feed.domain.Raw
import com.raihanarman.feed.domain.Usd
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * @author Raihan Arman
 * @date 18/11/23
 */

class LoadCryptoFeedRemoteUseCaseTest {

    private val client = spyk<HttpClient>()
    private lateinit var sut: LoadCryptoFeedRemoteUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        sut = LoadCryptoFeedRemoteUseCase(client)
    }

    @Test
    fun testInitDoesNotLoad() {
        verify(exactly = 0) {
            client.get()
        }

        confirmVerified(client)
    }

    @Test
    fun testLoadRequestData() = runBlocking {
        // Given
        every {
            client.get()
        } returns flowOf()

        // When
        sut.load().test {
            awaitComplete()
        }

        // Then
        verify(exactly = 1) {
            client.get()
        }

        confirmVerified(client)
    }
//
    @Test
    fun testLoadRequestDataTwice() = runBlocking {
        // Given
        every {
            client.get()
        } returns flowOf()

        // When
        sut.load().test {
            awaitComplete()
        }

        sut.load().test {
            awaitComplete()
        }

        verify(exactly = 2) {
            client.get()
        }

        confirmVerified(client)
    }



    @Test
    fun testLoadDeliversErrorOnClientError() = runBlocking {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult =  HttpClientResult.Failure(ConnectivityException()),
            expectedResult = Connectivity(),
            exactly = 1,
            confirmVerified = client
        )

    }

    @Test
    fun testDeliversInvalidDataError() = runBlocking {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(InvalidDataException()),
            expectedResult = InvalidData(),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testDeliversBadRequestError() = runBlocking {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(BadRequestException()),
            expectedResult = BadRequest(),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testDeliversServerError() = runBlocking {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(ServerErrorException()),
            expectedResult = ServerError(),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testLoadDeliversNotFoundErrorOnClientError() {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(NotFoundException()),
            expectedResult = NotFound(),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testDeliversItemsOn200HttpResponseWithResponse() = runBlocking {
        val cryptoFeedResponse = listOf(
            RemoteCryptoFeedItem(
                RemoteCoinInfo(
                    "1",
                    "BTC",
                    "Bitcoin",
                    "imageUrl",
                ),
                RemoteDisplay(
                    RemoteUsd(
                        1.0,
                        1F,
                    ),
                ),
            ),
            RemoteCryptoFeedItem(
                RemoteCoinInfo(
                    "2",
                    "BTC 2",
                    "Bitcoin 2",
                    "imageUrl"
                ),
                RemoteDisplay(
                    RemoteUsd(
                        2.0,
                        2F,
                    ),
                ),
            ),
        )
        val cryptoFeed = listOf(
            CryptoFeed(
                CoinInfo(
                    "1",
                    "BTC",
                    "Bitcoin",
                    "imageUrl"
                ),
                Raw(
                    Usd(
                        1.0,
                        1F,
                    ),
                ),
            ),
            CryptoFeed(
                CoinInfo(
                    "2",
                    "BTC 2",
                    "Bitcoin 2",
                    "imageUrl"
                ),
                Raw(
                    Usd(
                        2.0,
                        2F,
                    ),
                ),
            ),
        )
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Success(RemoteRootCryptoFeed(
                data = cryptoFeedResponse
            )),
            expectedResult = LoadCryptoFeedResult.Success(cryptoFeed),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testDeliversNoItemsOn200HttpResponseWithResponse() {
        val cryptoFeedResponse = listOf<RemoteCryptoFeedItem>()
        val cryptoFeed = listOf<CryptoFeed>()
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Success(RemoteRootCryptoFeed(
                data = cryptoFeedResponse
            )),
            expectedResult = LoadCryptoFeedResult.Success(cryptoFeed),
            exactly = 1,
            confirmVerified = client
        )
    }

    private fun expect(
        client: HttpClient,
        sut: LoadCryptoFeedRemoteUseCase,
        receivedHttpClientResult:  HttpClientResult,
        expectedResult: Any,
        exactly: Int = -1,
        confirmVerified: HttpClient
    ) = runBlocking {
        every {
            client.get()
        } returns flowOf(receivedHttpClientResult)

        sut.load().test {
            when(val receivedResult = awaitItem()) {
                is LoadCryptoFeedResult.Failure -> {
                    assertEquals(expectedResult::class.java, receivedResult.exception::class.java)
                }

                is LoadCryptoFeedResult.Success -> {
                    assertEquals(
                        expectedResult,
                        receivedResult
                    )
                }
            }
            awaitComplete()
        }

        verify(exactly = exactly) {
            client.get()
        }

        confirmVerified(confirmVerified)
    }

}