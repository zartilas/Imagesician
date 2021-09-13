package unipi.p17168.imagesician.utils

import java.text.SimpleDateFormat
import java.util.*

object Constants {

    // General Constants
    const val SHARED_PREFERENCES_PREFIX: String = "ImagesicianPrefs"
    const val LOGGED_IN_EMAIL: String = "logged_in_email"

    // Intent Extras
    const val EXTRA_USER_EMAIL: String = "extraUserEmail"
    const val EXTRA_REG_USERS_SNACKBAR: String = "extraShowRegisteredUserSnackbar"

    // Firebase Constants
    // This is used for the collection name for USERS.
    const val COLLECTION_USERS: String = "users"

    val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
}