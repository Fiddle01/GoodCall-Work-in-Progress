package com.example.goodcall

import java.util.*

abstract class Message {
    //Author - Display name
    abstract val author: String

    //Time sent
    abstract val timeSent: Date

    //Contents to string
    abstract fun contentsToString(): String
}