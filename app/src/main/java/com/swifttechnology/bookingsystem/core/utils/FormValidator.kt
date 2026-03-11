package com.swifttechnology.bookingsystem.core.utils

import com.swifttechnology.bookingsystem.core.extensions.isValidEmail
import com.swifttechnology.bookingsystem.core.extensions.isValidPassword
import com.swifttechnology.bookingsystem.core.utils.Constants.MIN_PASSWORD_LENGTH

/**
 * Stateless form validation utility.
 * Returns a [ValidationResult] with an optional error message.
 * Mirrors iOS FormValidator.
 */
object FormValidator {

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()

        val isValid: Boolean get() = this is Valid
        val errorMessage: String? get() = (this as? Invalid)?.message
    }

    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) return ValidationResult.Invalid("Email is required.")
        if (!email.isValidEmail()) return ValidationResult.Invalid("Enter a valid email address.")
        return ValidationResult.Valid
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) return ValidationResult.Invalid("Password is required.")
        if (password.length < MIN_PASSWORD_LENGTH)
            return ValidationResult.Invalid("Password must be at least $MIN_PASSWORD_LENGTH characters.")
        if (!password.any { it.isDigit() })
            return ValidationResult.Invalid("Password must contain at least one number.")
        if (!password.any { it.isUpperCase() })
            return ValidationResult.Invalid("Password must contain at least one uppercase letter.")
        return ValidationResult.Valid
    }

    fun validateRequired(value: String, fieldName: String): ValidationResult {
        if (value.isBlank()) return ValidationResult.Invalid("$fieldName is required.")
        return ValidationResult.Valid
    }

    fun validateConfirmPassword(password: String, confirm: String): ValidationResult {
        if (confirm.isBlank()) return ValidationResult.Invalid("Please confirm your password.")
        if (password != confirm) return ValidationResult.Invalid("Passwords do not match.")
        return ValidationResult.Valid
    }
}
