package io.tvdubs.copixelate.navigation

// Class for setting up screen objects with a route string.
sealed class Screen(val route: String) {
    object Login: Screen(route = "login")
    object Registration: Screen(route = "registration")
    object Messages: Screen(route = "messages")
    object MessageThread: Screen(route = "message_thread")
}
