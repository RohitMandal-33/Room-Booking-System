package com.swifttechnology.bookingsystem.core.extensions

import android.util.Patterns

/** Returns true if this string is a syntactically valid email address. */
fun String.isValidEmail(): Boolean =
    isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

/** Returns true if this string meets minimum password requirements. */
fun String.isValidPassword(): Boolean =
    length >= 8 && any { it.isDigit() } && any { it.isUpperCase() }

/** Trims and returns null if blank, otherwise returns trimmed string. */
fun String.nullIfBlank(): String? = trim().ifBlank { null }

/** Capitalizes the first character of each word. */
fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercaseChar() }
    }
