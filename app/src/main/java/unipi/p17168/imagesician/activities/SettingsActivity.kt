package unipi.p17168.imagesician.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.toolbar_settings.*
import kotlinx.android.synthetic.main.toolbar_title_only.*
import unipi.p17168.imagesician.LanguageClass
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.database.DBHelper
import unipi.p17168.imagesician.databinding.ActivitySettingsBinding
import unipi.p17168.imagesician.models.User
import unipi.p17168.imagesician.utils.Constants
import unipi.p17168.imagesician.utils.Constants.ENGLISH_LAG
import unipi.p17168.imagesician.utils.Constants.GERMAN_LAG
import unipi.p17168.imagesician.utils.Constants.GREEK_LAG

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
        radioButtonID = binding.radioGroupLag.checkedRadioButtonId
        Log.e("Settings Fragment, Change:","init()")
        setupClickListeners()
        loadProfileDetails()
        checkedChangeRadioButtonLag()
        setUpSettings()
        setupActionBar()
    }

    @SuppressLint("SetTextI18n")
    private fun setupClickListeners() {
        binding.apply {
            btnLogout.setOnClickListener{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@SettingsActivity, SignInActivity::class.java)
                startActivity(intent)
            }

            switchNightMode.setOnCheckedChangeListener { _, _ ->
                if (switchNightMode.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
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

    private fun setUpSettings(){
        when(sharePrefLagnuage.getString(Constants.LANGUAGE,"")){
            GREEK_LAG-> binding.radioGroupLag.check(R.id.radioButtonGreek)
            ENGLISH_LAG->  binding.radioGroupLag.check(R.id.radioButtonEnglish)
            GERMAN_LAG-> binding.radioGroupLag.check(R.id.radioButtonGerman)
        }
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

        binding.radioGroupLag.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonGreek->{
                    Log.e("Settings Fragment", GREEK_LAG)
                    with(sharePrefLagnuage.edit()) {
                        putString(Constants.LANGUAGE, GREEK_LAG)
                        apply()
                    }
                }
                R.id.radioButtonGerman -> {
                    Log.e("Settings Fragment", GERMAN_LAG)
                    with(sharePrefLagnuage.edit()) {
                        putString(Constants.LANGUAGE, GERMAN_LAG)
                        apply()
                    }
                }
                R.id.radioButtonEnglish -> {
                    Log.e("Settings Fragment", ENGLISH_LAG)
                    with(sharePrefLagnuage.edit()) {
                        putString(Constants.LANGUAGE, ENGLISH_LAG)
                        apply()

                    }
                }
            }
            LanguageClass()
        }
    }
}