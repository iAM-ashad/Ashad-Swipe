package com.iamashad.ashad_swipe.util

/**
 * Cleans user input for numeric fields.
 * - Strips minus signs
 * - Allows only digits and a single decimal point
 * - Converts a lone "." into "0."
 */
fun sanitizeDecimal(input: String): String {
    val noMinus = input.replace("-", "") // block negatives
    var dotSeen = false

    val cleaned = buildString {
        noMinus.forEach { ch ->
            when {
                ch.isDigit() -> append(ch)
                ch == '.' && !dotSeen -> {
                    append('.')
                    dotSeen = true
                }
            }
        }
    }

    // Fix edge case: user types only "."
    return if (cleaned == ".") "0." else cleaned
}

/**
 * Returns true if the string is a valid non-negative decimal number.
 * Safe for empty strings and invalid input.
 */
fun isNonNegativeDecimal(s: String): Boolean {
    val v = s.toDoubleOrNull() ?: return false
    return v >= 0.0
}
