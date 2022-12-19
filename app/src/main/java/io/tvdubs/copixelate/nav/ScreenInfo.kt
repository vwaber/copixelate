package io.tvdubs.copixelate.nav

sealed class ScreenInfo(val route: String) {
    object Art: ScreenInfo(route = "art")
    object Login: ScreenInfo(route = "login")
    object Registration: ScreenInfo(route = "registration")
    object Messages: ScreenInfo(route = "messages")
    object MessageThread: ScreenInfo(route = "message_thread")
}
