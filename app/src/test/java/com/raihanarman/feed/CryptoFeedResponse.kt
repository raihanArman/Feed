package com.raihanarman.feed

import com.raihanarman.feed.api.RemoteCoinInfo
import com.raihanarman.feed.api.RemoteCryptoFeedItem
import com.raihanarman.feed.api.RemoteDisplay
import com.raihanarman.feed.api.RemoteUsd

/**
 * @author Raihan Arman
 * @date 01/12/23
 */
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