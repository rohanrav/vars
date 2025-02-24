import org.apache.commons.validator.routines.UrlValidator

fun isValidUrl(input: String): Boolean {
    val schemes = arrayOf("http", "https")
    val urlValidator = UrlValidator(schemes)

    return urlValidator.isValid(input)
}