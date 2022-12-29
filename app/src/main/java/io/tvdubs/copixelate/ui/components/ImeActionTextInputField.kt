package io.tvdubs.copixelate.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImeActionTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    isError: Boolean,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = when (imeAction) {
            ImeAction.Next -> {
                KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
            }
            ImeAction.Done -> {
                KeyboardActions(onDone = { focusManager.clearFocus() })
            }
            else -> KeyboardActions.Default
        },
        modifier = modifier,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
    )

}// End KeyboardActionTextInputField
