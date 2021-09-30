package unipi.p17168.imagesician

import android.app.Application
import android.content.Context
import android.util.Log
import unipi.p17168.imagesician.activities.BaseActivity
import unipi.p17168.imagesician.utils.Constants.LANGUAGE
import java.util.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import unipi.p17168.imagesician.utils.Constants.EL
import unipi.p17168.imagesician.utils.Constants.EN
import unipi.p17168.imagesician.utils.Constants.ENGLISH_LANG
import unipi.p17168.imagesician.utils.Constants.GE
import unipi.p17168.imagesician.utils.Constants.GERMAN_LANG
import unipi.p17168.imagesician.utils.Constants.GREEK_LANG


class LanguageClass : Application() {

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        setLang(baseContext)
    }

     fun setLang(context: Context){
        val sharedPreferences = getDefaultSharedPreferences(context)
        Log.e("LANGUAGE CLASS", sharedPreferences.getString(LANGUAGE, "").toString())
        when(sharedPreferences.getString(LANGUAGE, "")){
           GREEK_LANG->    BaseActivity.dLocale = Locale(EL) //set locale
           ENGLISH_LANG->  BaseActivity.dLocale = Locale(EN)
           GERMAN_LANG ->  BaseActivity.dLocale = Locale(GE)
       }

    }
}
