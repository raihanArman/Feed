package com.raihanarman.feed

import app.cash.turbine.test
import com.raihanarman.feed.api.Connectivity
import com.raihanarman.feed.api.ConnectivityException
import com.raihanarman.feed.api.HttpClient
import com.raihanarman.feed.api.InvalidData
import com.raihanarman.feed.api.InvalidDataException
import com.raihanarman.feed.api.LoadCryptoFeedRemoteUseCase
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
}