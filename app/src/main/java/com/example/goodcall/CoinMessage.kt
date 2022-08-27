package com.example.goodcall

import java.util.*
import kotlin.random.Random.Default.nextBoolean

class CoinMessage(override val author: String, override val timeSent: Date, var isHeads: Boolean) : Message() {
    override fun contentsToString(): String {
        return if (isHeads) "$author flipped a coin that landed heads!" else "$author flipped a coin that landed tails!"
    }
    //no-args constructor necessary for Firebase
    constructor(): this ("NONE", Date(), nextBoolean())


}