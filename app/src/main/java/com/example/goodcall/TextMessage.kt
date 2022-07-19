package com.example.goodcall

import java.util.*

class TextMessage(override val author: String, override val timeSent: Date, val text: String) : Message(){
    override fun contentsToString(): String {
        return text
    }

    constructor(): this ("None", Date(), "None")


}