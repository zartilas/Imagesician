package unipi.p17168.imagesician

import android.app.Application
import android.util.Log
import unipi.p17168.imagesician.activities.BaseActivity
import unipi.p17168.imagesician.utils.Constants.LANGUAGE
import java.util.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences


class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        setLang()
    }


    private fun setLang(){
        var change = ""
        val sharedPreferences = getDefaultSharedPreferences(this)
        when(sharedPreferences.getString(LANGUAGE, "")){
           "Greek"-> change = "el"
           "English"-> change = "en"
           "German"-> change = "ge"
       }

        BaseActivity.dLocale = Locale(change) //set any locale you want here
        /*BaseActivity().goToImagesicialActivity(this)*/
        Log.e("ApplicationClass", change)

    }
}
