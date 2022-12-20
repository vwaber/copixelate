package vwaber.copixelate.data

import vwaber.copixelate.core.AppDataSource
import java.util.logging.Level

object AppRepo : AppDataSource {

    override fun login() {
        java.util.logging.Logger.getLogger("AppRepo").log(Level.INFO, "login")
    }

}
