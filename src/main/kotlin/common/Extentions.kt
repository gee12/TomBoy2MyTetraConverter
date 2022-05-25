package common

import java.text.SimpleDateFormat
import java.util.*

fun Date.dateToString(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun String.stringToDate(pattern: String): Date? {
    if (this.isEmpty()) {
        return null
    }
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
