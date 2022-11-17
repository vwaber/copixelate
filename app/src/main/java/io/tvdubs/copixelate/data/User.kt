package io.tvdubs.copixelate.data

data class User(
    val uid: String? = null,
    val email: String? = null,
    val contacts: MutableList<String>? = null,
    val artBoards: MutableList<String>? = null,
    val profilePicture: String? = null
)
