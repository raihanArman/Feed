package com.raihanarman.feed

import com.raihanarman.feed.domain.CoinInfo
import com.raihanarman.feed.domain.CryptoFeed
import com.raihanarman.feed.domain.Raw
import com.raihanarman.feed.domain.Usd

/**
 * @author Raihan Arman
 * @date 16/01/24
 */
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