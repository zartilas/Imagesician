package unipi.p17168.imagesician.activities

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import unipi.p17168.imagesician.ImagesicianActivity
import unipi.p17168.imagesician.R
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * A base activity class is used to define the functions and members which we will use in all the activities.
 * It inherits the AppCompatActivity class so in other activity class we will replace the AppCompatActivity with BaseActivity.
 */
open class BaseActivity : AppCompatActivity() {

    // Create an executor that executes tasks in a background thread.
    private val backgroundExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    // A global variable for double back press feature.
    private var doubleBackToExitPressedOnce = false

    fun goToSignInActivity(context: Context?) {
        val intent = Intent(context, SignInActivity::class.java)
        startActivity(intent)
    }

    fun goToImagesicialActivity(context: Context) {
        val intent = Intent(context, ImagesicianActivity::class.java)
        startActivity(intent)
    }

    fun goToMainActivityNoAnimation(context: Context) {
        val intent = Intent(context, ImagesicianActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    /**
     * A function to implement the double back press feature to exit the app.
     */
    fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.txt_please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        backgroundExecutor.schedule({
            doubleBackToExitPressedOnce = false
        }, 2000, TimeUnit.MILLISECONDS)
    }
}