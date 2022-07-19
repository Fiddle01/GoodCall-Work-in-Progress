package com.example.goodcall

class Group (val members: ArrayList<String>, val messages: ArrayList<String>, val name: String, val code: String) {
    //members - array list of strings that store the user ID of each member from FireBase.

    //name - The name of the group (nullable if the user decides to not name the group.

    //messages - each group will have messages sent in it chronologically in array list.
    constructor(): this(ArrayList<String>(), ArrayList<String>(), "New Group", "123")


}