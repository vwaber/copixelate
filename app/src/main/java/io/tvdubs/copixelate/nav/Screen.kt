package io.tvdubs.copixelate.nav

sealed class Screen(val route: String) {
    object Login: Screen(route = "login")
    object Registration: Screen(route = "registration")
    object Messages: Screen(route = "messages")
    object MessageThread: Screen(route = "message_thread")
}
