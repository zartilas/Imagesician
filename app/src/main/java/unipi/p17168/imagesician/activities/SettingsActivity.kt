package unipi.p17168.imagesician.activities

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_settings.*
import kotlinx.android.synthetic.main.toolbar_title_only.*
import unipi.p17168.imagesician.LanguageClass
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.database.DBHelper
import unipi.p17168.imagesician.databinding.ActivitySettingsBinding
import unipi.p17168.imagesician.models.User
import unipi.p17168.imagesician.utils.Constants
import unipi.p17168.imagesician.utils.Constants.LANGUAGE
import unipi.p17168.imagesician.utils.Constants.NIGHTMODE
import unipi.p17168.imagesician.utils.Constants.ENGLISH_LANG
import unipi.p17168.imagesician.utils.Constants.GERMAN_LANG
import unipi.p17168.imagesician.utils.Constants.GREEK_LANG
import android.content.Intent


class SettingsActivity : BaseActivity() {

    // ~~~~~~~VARIABLES~~~~~~~
    //var
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var modelUser: User
    private lateinit var sharePrefLagnuage: SharedPreferences

    //VAL
    private val dbFirestore = FirebaseFirestore.getInstance()
    var radioButtonID = 0

    // ~~~~~~~~~~~~~~~~~~~~~~~


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init(){
        sharePrefLagnuage = PreferenceManager.getDefaultSharedPreferences(this)
        sharePrefNightMode = PreferenceManager.getDefaultSharedPreferences(this)
        radioButtonID = binding.radioGroupLag.checkedRadioButtonId
        Log.e("Settings Fragment:","init()")
        setupClickListeners()
        loadProfileDetails()
        checkedChangeRadioButtonLag()
        setUpSetting()
        setupActionBar()
    }

    @SuppressLint("SetTextI18n")
    private fun setupClickListeners() {
        binding.apply {
            btnLogout.setOnClickListener{
                FirebaseAuth.getInstance().signOut()
               /* val intent = Intent(this@SettingsActivity, SignInActivity::class.java)
                startActivity(intent)*/
                goToSignInActivity(this@SettingsActivity)
            }

            switchNightMode.setOnCheckedChangeListener { _, _ ->
                if (switchNightMode.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    with(sharePrefNightMode.edit()) {
                        putBoolean(NIGHTMODE, true)
                        apply()
                    }
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    with(sharePrefNightMode.edit()) {
                        putBoolean(NIGHTMODE, false)
                        apply()
                    }
                }
            }

                radioButtonEnglish.setOnClickListener{
                    Log.e("Settings Activity","Radio Group En")
                    radioButtonGreek.isClickable = true
                    radioButtonGerman.isClickable = true
                    radioButtonEnglish.isClickable = false
                    reloadApp()
                }
                radioButtonGreek.setOnClickListener {
                    Log.e("Settings Activity", "Radio Group El")
                    radioButtonGreek.isClickable = false
                    radioButtonGerman.isClickable = true
                    radioButtonEnglish.isClickable = true
                    reloadApp()
                }
             radioButtonGerman.setOnClickListener {
                Log.e("Settings Activity", "Radio Group GE")
                radioButtonGreek.isClickable = true
                radioButtonGerman.isClickable = false
                radioButtonEnglish.isClickable = false
                reloadApp()
                }


        }
    }


    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            textViewLabel.text = getString(R.string.txt_settings)
            imgBtn_back.setOnClickListener {
                goToImagesicialActivity(this@SettingsActivity)
            }
        }
    }

    private fun setUpSetting(){
        when(sharePrefLagnuage.getString(LANGUAGE,"")){
            GREEK_LANG-> binding.radioGroupLag.check(R.id.radioButtonGreek)
            ENGLISH_LANG->  binding.radioGroupLag.check(R.id.radioButtonEnglish)
            GERMAN_LANG-> binding.radioGroupLag.check(R.id.radioButtonGerman)
        }

        switch_night_mode.isChecked = sharePrefNightMode.getBoolean(NIGHTMODE, false)
    }

    private fun loadProfileDetails() {
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // The document id to get the Fields of user.
            .document(DBHelper().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!
                profileDetails(user)
            }
    }

    private fun profileDetails(user: User) {

        modelUser = user

        binding.apply {
            // Populate the user details in the input texts.
            textViewNameValue.text = modelUser.fullName
            textViewEmailValue.text = modelUser.email
            textViewDateRegisteredValue.text = Constants.DATE_FORMAT.format(modelUser.dateRegistered)
        }
    }

    private fun checkedChangeRadioButtonLag(){

        binding.radioGroupLag.setOnCheckedChangeListener { _, checkedId -> // idemi gamato an doulefkei etsi
            when (checkedId) {
                R.id.radioButtonGreek->{
                    Log.e("Settings Activity", GREEK_LANG)
                    with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, GREEK_LANG)
                        apply()
                    }

                }
                R.id.radioButtonGerman -> {
                    Log.e("Settings Activity", GERMAN_LANG)
                    with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, GERMAN_LANG)
                        apply()
                    }
                }
                R.id.radioButtonEnglish -> {
                    Log.e("Settings Activity", ENGLISH_LANG)
                    with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, ENGLISH_LANG)
                        apply()
                    }
                }

            }
            LanguageClass()
        }
    }


    private fun reloadApp(){
        val i = baseContext.packageManager
            .getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }
}

