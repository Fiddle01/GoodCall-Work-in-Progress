package com.example.goodcall

import java.util.*

class RandomMemberMessage(override val author: String, override val timeSent: Date, val chosenMember: String) : Message() {
    override fun contentsToString(): String {
        return "$chosenMember was chosen!"
    }
    constructor(): this ("NONE", Date(), "NONE")
}