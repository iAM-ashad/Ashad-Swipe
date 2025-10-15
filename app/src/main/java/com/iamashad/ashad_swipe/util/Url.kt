package com.iamashad.ashad_swipe.util

/**
 * Normalizes an image URL returned by the API.
 *
 * - Returns `null` if blank or invalid.
 * - Keeps full URLs as-is.
 * - Prepends [base] if the path is relative.
 *
 * Example:
 *  - "image.png" → "https://app.getswipe.in/image.png"
 *  - "/images/pic.jpg" → "https://app.getswipe.in/images/pic.jpg"
 *  - "https://cdn..." → stays unchanged
 */
fun normalizeImageUrl(
    raw: String?,
    base: String = "https://app.getswipe.in"
): String? {
    val trimmed = raw?.trim().orEmpty()
    if (trimmed.isEmpty()) return null

    return when {
        trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) -> trimmed

        trimmed.startsWith("/") ->
            base.removeSuffix("/") + trimmed

        else ->
            base.removeSuffix("/") + "/" + trimmed
    }
}
