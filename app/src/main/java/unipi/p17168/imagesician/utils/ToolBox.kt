package unipi.p17168.imagesician.utils

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.activities.SignInActivity
import java.util.concurrent.TimeUnit

class ToolBox {

    // A global variable for double back press feature.
    private var doubleBackToExitPressedOnce = false

    //Permissions
     fun setupPermissions(context:Context,activity: Activity,permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            return
        }
    }
        //if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
        //PackageManager.PERMISSION_GRANTED) {
        //ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
        //50); }

    //Network Check
    fun isNetworkAvailbale(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetwork

        return  networkInfo!=null
    }

    //Snack Bar
    fun showSnackBar(context: View,colorBackgroud:Int,colorText:Int,textShow:String,actionText:String,timeShow:Int):Snackbar{

     val snackBar = Snackbar.make(context,textShow,timeShow)

        snackBar.view.setBackgroundColor(colorBackgroud)
        snackBar.setActionTextColor(colorText)
        snackBar.setTextColor(colorText)
        snackBar.setAction(actionText){}

        return snackBar
     }

    //copy Text
    fun copyText(context: Context,text:String){

        if(!text.length.equals(null)){
            val clipboard = getSystemService(
                context,
                ClipboardManager::class.java
            ) as ClipboardManager // Gets a handle to the clipboard service.
            val clip: ClipData = ClipData.newPlainText("text", text) // Creates a new text clip to put on the clipboard
            clipboard.setPrimaryClip(clip)  // Set the clipboard's primary clip.
        }
    }

    // Dialogs
    fun showNotFoundDialog(context: Context) : Dialog {

        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_not_found)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnAlertOk: MaterialButton = dialog.findViewById(R.id.btn_AlertTextWarning_Dismiss)
        btnAlertOk.setOnClickListener { dialog.dismiss()
        }
        return dialog
    }

    fun showWrongDialog(context: Context) : Dialog {

        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_wrong)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnAlertOk: MaterialButton = dialog.findViewById(R.id.btn_AlertTextWarning_Dismiss)
        btnAlertOk.setOnClickListener { dialog.dismiss()
        }

        return dialog
    }



}