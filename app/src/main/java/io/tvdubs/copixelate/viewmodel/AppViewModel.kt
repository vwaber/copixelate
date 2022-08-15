package io.tvdubs.copixelate.viewmodel

import androidx.lifecycle.ViewModel
import io.tvdubs.copixelate.ui.art.Drawing

class AppViewModel() : ViewModel() {
    val drawing: Drawing = Drawing()
}
