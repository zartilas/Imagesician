package unipi.p17168.imagesician.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Xml
import android.view.Window
import com.google.android.material.button.MaterialButton
import unipi.p17168.imagesician.R


class Dialogs {

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