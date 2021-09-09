package unipi.p17168.imagesician.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.net.URL
import java.util.*

/**
 * A data model class with required fields.
 */
@Keep
@Parcelize
@IgnoreExtraProperties
data class UserReco(
    val userId: String = "",
    val type: String = "", // image, imageText
    @ServerTimestamp
    val dateAdded: Date = Date(),
    var imageId: String = "",
) : Parcelable