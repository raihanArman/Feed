package com.raihanarman.feed

import app.cash.turbine.test
import com.raihanarman.feed.api.BadRequest
import com.raihanarman.feed.api.BadRequestException
import com.raihanarman.feed.api.Connectivity
import com.raihanarman.feed.api.ConnectivityException
import com.raihanarman.feed.api.HttpClient
import com.raihanarman.feed.api.InvalidData
import com.raihanarman.feed.api.InvalidDataException
import com.raihanarman.feed.api.LoadCryptoFeedRemoteUseCase
import com.raihanarman.feed.api.ServerError
import com.raihanarman.feed.api.ServerErrorException
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
        every {
            client.get()
        } returns flowOf(ConnectivityException())

        sut.load().test {
            assertEquals(Connectivity::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        verify(exactly = 1) {
            client.get()
        }

        confirmVerified(client)

    }

    @Test
    fun testDeliversInvalidDataError() = runBlocking {
        every {
            client.get()
        } returns flowOf(InvalidDataException())

        sut.load().test {
            assertEquals(InvalidData::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        verify(exactly = 1) {
            client.get()
        }

        confirmVerified(client)

    }

    @Test
    fun testDeliversBadRequestError() = runBlocking {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = BadRequestException(),
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
            receivedHttpClientResult = ServerErrorException(),
            expectedResult = ServerError(),
            exactly = 1,
            confirmVerified = client
        )
    }

    private fun expect(
        client: HttpClient,
        sut: LoadCryptoFeedRemoteUseCase,
        receivedHttpClientResult: Exception,
        expectedResult: Any,
        exactly: Int = -1,
        confirmVerified: HttpClient
    ) = runBlocking {
        every {
            client.get()
        } returns flowOf(receivedHttpClientResult)

        sut.load().test {
            assertEquals(expectedResult::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        verify(exactly = exactly) {
            client.get()
        }

        confirmVerified(confirmVerified)
    }

}