package unipi.p17168.imagesician.database

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import unipi.p17168.imagesician.activities.SignInActivity
import unipi.p17168.imagesician.activities.SignUpActivity
import unipi.p17168.imagesician.utils.Constants
import unipi.p17168.imagesician.models.User
import unipi.p17168.imagesician.models.UserReco

class DBHelper {

    // Access a Cloud Firestore instance.
    private val dbFirestore = FirebaseFirestore.getInstance()


    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }


    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerUser(activity: SignUpActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.userId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                //activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.d(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                when (activity) {
                    // When activity is the sign in one
                    is SignInActivity -> {
                        val sharedPreferences =
                            activity.getSharedPreferences(
                                Constants.SHARED_PREFERENCES_PREFIX,
                                Context.MODE_PRIVATE
                            )

                        // Create an instance of the editor which is help us to edit the SharedPreference.
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString(
                            Constants.LOGGED_IN_EMAIL,
                            user.email
                        )
                        editor.apply()
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
               /* when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ProfileDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }*/

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

     fun saveUserImage(image: Uri, isTextImage : Boolean){

        Log.e("ImageFragmend","The id: ${DBHelper().getCurrentUserID()}")

        val folder : StorageReference = FirebaseStorage.getInstance().reference.child(DBHelper().getCurrentUserID())
        val imageName = folder.child("image" + image.lastPathSegment)

        val metadata = if(isTextImage){
            storageMetadata { setCustomMetadata("isText", true.toString()) }
        }else{
            storageMetadata { setCustomMetadata("isText", false.toString()) }
        }

         imageName.putFile(image,metadata).addOnSuccessListener{
                Log.e("ImageFragment","Save Image")
         }

     }

    /**
     * A function to get the user logs list from cloud firestore.
     *
     * @param activity The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getUserLogEntries(activity: Activity, sortBy: String) {
        // The collection name for User Logs
        dbFirestore.collection(Constants.COLLECTION_USER_LOGS)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .orderBy(sortBy, Query.Direction.ASCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.d("User Logs List", document.documents.toString())

                // Here we have created a new instance for user logs ArrayList.
                val userLogsList: ArrayList<UserReco> = ArrayList()

                // A for loop as per the list of documents to convert them into user logs ArrayList.
                for (i in document.documents) {

                    val userLog = i.toObject(UserLog::class.java)
                    userLog!!.logId = i.id

                    userLogsList.add(userLog)
                }
                when (activity) {
                    is UserLogsListActivity -> {
                        activity.successUserLogsFromFireStore(userLogsList)
                    }
                    else -> {}
                }
            }
            .addOnFailureListener { e ->
              /*  when (activity) {
                    is UserLogsListActivity -> {
                        activity.hideLogs()
                    }
                    else -> {}
                }*/
                Log.e("Get User Logs List", "Error while getting user logs list.", e)
            }
    }


}