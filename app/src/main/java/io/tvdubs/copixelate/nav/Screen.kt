package io.tvdubs.copixelate.nav

sealed class Screen(val route: String) {
    object Art: Screen(route = "art")
    object Login: Screen(route = "login")
    object Registration: Screen(route = "registration")
    object Messages: Screen(route = "messages")
    object MessageThread: Screen(route = "message_thread")
    object Contacts: Screen(route = "contacts")
}
