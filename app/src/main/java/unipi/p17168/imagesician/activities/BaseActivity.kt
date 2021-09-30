package unipi.p17168.imagesician.activities


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import unipi.p17168.imagesician.LanguageClass
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.utils.Constants.EN
import unipi.p17168.imagesician.utils.Constants.NIGHTMODE
import java.util.*
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

    // This is a progress dialog instance which we will initialize later on.
    private lateinit var mProgressDialog: Dialog

    lateinit var sharePrefNightMode: SharedPreferences


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

    fun goToSettingsActivity(context: Context){
        val intent = Intent(context, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun goToSplashActivity(context: Context){
        val intent = Intent(context, SplashActivity::class.java)
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

    /**
     * This function is used to show the progress dialog with the title and message to user.
     */
    fun showProgressDialog() {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    //ABOUT LANGUAGE
    companion object {
         var dLocale: Locale? = null
        // var dLocale: Locale? = Locale(EN)
    }

    init {
        updateConfig(this)

    }

    fun updateConfig(wrapper: ContextThemeWrapper) {
        if(dLocale==Locale(""))//If dLocale is null return
            return
        Locale.setDefault(dLocale)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }

    fun setUpSettings(context: Context){
        sharePrefNightMode = PreferenceManager.getDefaultSharedPreferences(context)
        when(sharePrefNightMode.getBoolean(NIGHTMODE, false)){
            true-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


}

