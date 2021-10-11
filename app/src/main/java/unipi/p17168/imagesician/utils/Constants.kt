package unipi.p17168.imagesician.utils

import java.text.SimpleDateFormat
import java.util.*

object Constants {

    const val NIGHTMODE: String = "nightMode"

    // General Constants
    const val SHARED_PREFERENCES_PREFIX: String = "ImagesicianPrefs"
    const val LOGGED_IN_EMAIL: String = "logged_in_email"
    const val SPLASH_SCREEN_DELAY: Long = 1500

    // Intent Extras
    const val EXTRA_USER_EMAIL: String = "extraUserEmail"
    const val EXTRA_REG_USERS_SNACKBAR: String = "extraShowRegisteredUserSnackbar"

    // Firebase Constants
    // This is used for the collection name for USERS.
    const val COLLECTION_USERS: String = "users"

    val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)

    //LANGUAGE
    const val LANGUAGE : String = "language"
    const val ENGLISH_LANG : String =  "English"
    const val GREEK_LANG : String =  "Greek"
    const val GERMAN_LANG : String =  "German"
    const val EL : String = "el"
    const val EN : String = "en"
    const val GE : String = "de"

}