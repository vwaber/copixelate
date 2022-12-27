package io.tvdubs.copixelate.data

import android.util.Patterns

object InputValidation {

    fun checkEmail(value: String): InputValidity =
        when (Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            true -> InputValidity.Valid
            false -> InputValidity.Email.Invalid
        }

    fun checkDisplayName(value: String): InputValidity =
        when (value.length > 1) {
            true -> InputValidity.Valid
            false -> InputValidity.DisplayName.TooLong
        }

    fun checkPassword(value: String) =
        // Firebase requires a min of 6
        if (value.length < 6) InputValidity.Password.TooShort
        // Short max for testing, change to 32 or 64
        else if (value.length > 12) InputValidity.Password.TooLong
        else InputValidity.Valid

    fun checkPasswordMatch(p1: String, p2: String) =
        when (p1 == p2) {
            true -> InputValidity.Valid
            false -> InputValidity.Password.NoMatch
        }

}

sealed class InputValidity {

    object Valid : InputValidity()

    val isValid
        get() = when (this) {
            is Valid -> true
            else -> false
        }

    val isNotValid
        get() = !isValid

    sealed class Email : InputValidity() {
        object Invalid : Email()
    }

    sealed class DisplayName : InputValidity() {
        object TooShort : DisplayName()
        object TooLong : DisplayName()
    }

    sealed class Password : InputValidity() {
        object TooShort : Password()
        object TooLong : Password()
        object NoMatch : Password()
    }

}
