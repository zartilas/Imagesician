package unipi.p17168.imagesician

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStateManagerControl
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_settings.*
import unipi.p17168.imagesician.activities.BaseActivity
import unipi.p17168.imagesician.utils.ToolBox






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
    var refresh : Intent? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        init()
        return binding.root

    }

    private fun init(){

        sharePrefLagnuage = getDefaultSharedPreferences(contextSettingsFragment)
        radioButtonID = binding.radioGroupLag.checkedRadioButtonId
        Log.e("Settings Fragment, Change:","INIT()")
        setupClickListeners()
        loadProfileDetails()
        checkedChangeRadioButtonLag()
        setUpSettings()
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


    private fun setUpSettings(){
        when(sharePrefLagnuage.getString(LANGUAGE,"")){
          "Greek"-> binding.radioGroupLag.check(R.id.radioButtonGreek)
          "English"->  binding.radioGroupLag.check(R.id.radioButtonEnglish)
          "German"-> binding.radioGroupLag.check(R.id.radioButtonGerman)
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
                    Log.e("Settings Fragment", "GREEK SUCCESS")
                    with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, "Greek")
                        apply()
                    }

                }
                R.id.radioButtonGerman -> {
                    Log.e("Settings Fragment", "GERMAN SUCCESS")
                    with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, "German")
                        apply()
                    }
                }
                R.id.radioButtonEnglish -> {
                    Log.e("Settings Fragment", "ENGLISH SUCCESS")
                    with(sharePrefLagnuage.edit()) {
                        putString(LANGUAGE, "English")
                        apply()

                    }
                }
            }
            ApplicationClass()
            reloadFragment()

        }
    }

    fun reloadFragment(){
        // Reload current fragment
        var frg: Fragment?
        frg =  getChildFragmentManager().findFragmentByTag("fragment_settings")
        val ft: FragmentTransaction =  getChildFragmentManager().beginTransaction()
        if (frg != null) {
            ft.detach(frg)
        }
        if (frg != null) {
            ft.attach(frg)
        }
        ft.commit()
    }


}



