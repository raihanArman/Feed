package com.raihanarman.feed.api

import kotlinx.coroutines.flow.Flow

/**
 * @author Raihan Arman
 * @date 30/11/23
 */
interface CryptoFeedService {
    suspend fun get(): RemoteRootCryptoFeed
}