package io.tvdubs.copixelate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.tvdubs.copixelate.R
import io.tvdubs.copixelate.data.InputValidity

@Composable
fun ValidatedTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    validity: InputValidity,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {

    var lastFocusedState by rememberSaveable { mutableStateOf(false) }

    var isError by rememberSaveable { mutableStateOf(false) }
    isError = isError && validity.isNotValid && value.isNotEmpty()

    Column {
        ImeActionTextInputField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            isError = isError,
            imeAction = imeAction,
            modifier = modifier.onFocusChanged { focusState ->
                isError = isError || (!focusState.isFocused && lastFocusedState)
                lastFocusedState = focusState.isFocused
            },
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,

            )

        if (isError) {

            val resourceId: Int = when (validity as InputValidity.Invalid) {
                InputValidity.Invalid.Email.Invalid -> R.string.invalid_email

                InputValidity.Invalid.DisplayName.TooShort -> R.string.invalid_display_name_too_short
                InputValidity.Invalid.DisplayName.TooLong -> R.string.invalid_display_name_too_long

                InputValidity.Invalid.Password.TooShort -> R.string.invalid_password_too_short
                InputValidity.Invalid.Password.TooLong -> R.string.invalid_password_too_long
                InputValidity.Invalid.Password.NoMatch -> R.string.invalid_password_no_match
            }

            if (resourceId != 0) {
                val errorText: String = stringResource(resourceId)

                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 0.dp)
                )
            }

        }// End "if (isError)"

    }// End Column

}// End ValidatedTextInputField
