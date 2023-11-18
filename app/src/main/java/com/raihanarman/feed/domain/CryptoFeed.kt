package com.raihanarman.feed.domain

/**
 * @author Raihan Arman
 * @date 18/11/23
 */
data class CryptoFeed(
    val coinInfo: CoinInfo,
    val raw: Raw
)

data class Raw(
    val usd: Usd
)

data class Usd(
    val price: Double,
    val changePctDay: Long
)

data class CoinInfo(
    val id: String,
    val name: String,
    val fullName: String
)
