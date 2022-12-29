package io.tvdubs.copixelate.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import io.tvdubs.copixelate.data.InputValidity

private enum class Visibility {
    VISIBLE, HIDDEN;

    fun toggle() = when (this) {
        VISIBLE -> HIDDEN
        HIDDEN -> VISIBLE
    }
}

@Composable
fun SecretTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    validity: InputValidity,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
) {

    var visibility: Visibility by remember { mutableStateOf(Visibility.HIDDEN) }

    ValidatedTextInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        validity = validity,
        imeAction = imeAction,
        visualTransformation = when (visibility) {
            Visibility.VISIBLE -> VisualTransformation.None
            Visibility.HIDDEN -> PasswordVisualTransformation()
        },
        trailingIcon = {
            val image = when (visibility) {
                Visibility.VISIBLE -> Icons.Filled.VisibilityOff
                Visibility.HIDDEN -> Icons.Filled.Visibility
            }
            val contentDescription = when (visibility) {
                Visibility.VISIBLE -> "Hide password"
                Visibility.HIDDEN -> "Show password"
            }
            IconButton(onClick = { visibility = visibility.toggle() }) {
                Icon(image, contentDescription)
            }
        },
        modifier = modifier
    )

}// End SecretTextInputField
