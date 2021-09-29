package unipi.p17168.imagesician

import android.app.Application
import android.util.Log
import unipi.p17168.imagesician.activities.BaseActivity
import unipi.p17168.imagesician.utils.Constants.LANGUAGE
import java.util.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import unipi.p17168.imagesician.utils.Constants.EL
import unipi.p17168.imagesician.utils.Constants.EN
import unipi.p17168.imagesician.utils.Constants.ENGLISH_LAG
import unipi.p17168.imagesician.utils.Constants.GE
import unipi.p17168.imagesician.utils.Constants.GERMAN_LAG
import unipi.p17168.imagesician.utils.Constants.GREEK_LAG


class LanguageClass : Application() {

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        setLang()
    }

    private fun setLang(){
        val sharedPreferences = getDefaultSharedPreferences(this)
        when(sharedPreferences.getString(LANGUAGE, "")){
           GREEK_LAG->    BaseActivity.dLocale = Locale(EL) //set locale
           ENGLISH_LAG->  BaseActivity.dLocale = Locale(EN)
           GERMAN_LAG ->  BaseActivity.dLocale = Locale(GE)
       }
    }
}
