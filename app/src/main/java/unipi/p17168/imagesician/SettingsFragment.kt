package unipi.p17168.imagesician

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import unipi.p17168.imagesician.activities.SignInActivity
import unipi.p17168.imagesician.database.DBHelper
import unipi.p17168.imagesician.databinding.FragmentSettingsBinding
import unipi.p17168.imagesician.models.User
import unipi.p17168.imagesician.utils.Constants
import unipi.p17168.imagesician.utils.Constants.DLOCALE
import unipi.p17168.imagesician.utils.Constants.LANGUAGE
import java.util.*
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_settings.*
import unipi.p17168.imagesician.activities.BaseActivity

class SettingsFragment : Fragment(){

    //~~~~~~~VARIABLES~~~~~~~

    //VAR
    private var _binding : FragmentSettingsBinding? = null
    private lateinit var modelUser: User
    private lateinit var sharePrefLagnuage: SharedPreferences
    private lateinit var lag : String

    //VAL
    private val binding get() = _binding!!
    private val contextSettingsFragment get() = this@SettingsFragment.requireContext()
    private val dbFirestore = FirebaseFirestore.getInstance()
    var radioButtonID = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        init()
        return binding.root

    }

    private fun init(){
        sharePrefLagnuage = getDefaultSharedPreferences(contextSettingsFragment)
        lag = sharePrefLagnuage.getString(LANGUAGE,"En")!!
        radioButtonID = binding.radioGroupLag.checkedRadioButtonId
        Log.e("Settings Fragment, Change:","INIT()")
        setupClickListeners()
        loadProfileDetails()
        updateConfig(BaseActivity.getView as ContextThemeWrapper) //TODO FIX THIS
        checkedChangeRadioButtonLag()
    }

    @SuppressLint("SetTextI18n")
    private fun setupClickListeners() {
        binding.apply {
            btnLogout.setOnClickListener{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(context, SignInActivity::class.java)
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

    /*private fun changeLag(){
        var change = ""
        when (lag) {
            "German" -> change = "Ge"
            "English" -> change = "En"
            "Greek" -> change = "Gr"
        }
        Log.e("Settings Fragment, Change:",change)

    }*/

/*    fun updateConfig(wrapper: ContextThemeWrapper) {
        if(DLOCALE==Locale("") ) // Do nothing if dLocale is null
            return
        Locale.setDefault(DLOCALE)
        val configuration = Configuration()
        configuration.setLocale(DLOCALE)
        wrapper.applyOverrideConfiguration(configuration)
    }*/


    private fun checkedChangeRadioButtonLag(){
        var change = ""
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contextSettingsFragment)
        val language = sharedPreferences.getString("language", "bak")

      //  val yourRadioGroup = findViewById(R.id.radioGroupLag) as RadioGroup
        binding.radioGroupLag.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonGreek->{
                   /* with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, "Greek")
                        apply()

                    }*/
                    Log.e("Settings Fragment", "GREEK SUCCESS")
                    change = "el"

                }
                R.id.radioButtonGerman -> {
                  /*  with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, "German")
                        apply()

                    }*/
                    Log.e("Settings Fragment", "GERMAN SUCCESS")
                    change = "ge"
                }
                R.id.radioButtonEnglish -> {
                    /*with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, "English")
                        apply()


                    }*/
                    Log.e("Settings Fragment", "ENGLISH SUCCESS")
                    change = "en"

                }
            }
            Constants.DLOCALE = Locale(change) //set any locale you want here
            Log.e("SettingsFragment",change)
        }
    }

    private fun updateConfig(wrapper: ContextThemeWrapper) {
        if(DLOCALE == Locale("") ) {
            // Do nothing if dLocale is null
            Log.e("FragmentSettings", "Nothing in localez")
            return
        }

        // Locale.setDefault(DLOCALE!!)
        val configuration = Configuration()
        configuration.setLocale(DLOCALE)
        wrapper.applyOverrideConfiguration(configuration)
    }

}





