package unipi.p17168.imagesician

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
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

class SettingsFragment : Fragment(){

    //~~~~~~~VARIABLES~~~~~~~

    //VAR
    private var _binding : FragmentSettingsBinding? = null
    private lateinit var modelUser: User

    //VAL
    private val binding get() = _binding!!
    private val contextSettingsFragment get() = this@SettingsFragment.requireContext()
    private val dbFirestore = FirebaseFirestore.getInstance()
    private val sharePrefLagnuage = PreferenceManager.getDefaultSharedPreferences(contextSettingsFragment)
    private var lag = sharePrefLagnuage.getString(LANGUAGE, "En")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        init()
        return binding.root

    }

    private fun init(){
        setupClickListeners()
        loadProfileDetails()
        onRadioButtonClicked(binding.radioGroupLag)
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

    private fun changeLag(){
        var change = ""
        when (lag) {
            "German" -> change = "Ge"
            "English" -> change = "En"
            "Greek" -> change = "Gr"
        }
        DLOCALE = Locale(change) //set any locale you want here
    }

    private fun onRadioButtonClicked(view: View) {

        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioButtonGreek ->
                    if (checked) {
                        with (sharePrefLagnuage.edit()) {
                            putString(LANGUAGE,"Greek")
                            apply()
                        }
                        changeLag()
                    }
                R.id.radioButtonEnglish ->
                    if (checked) {
                        with (sharePrefLagnuage.edit()) {
                            putString(LANGUAGE,"English")
                            apply()
                        }
                        changeLag()
                    }
                R.id.radioButtonGerman ->
                    if (checked) {
                        with (sharePrefLagnuage.edit()) {
                            putString(LANGUAGE,"German")
                            apply()
                        }
                        changeLag()
                    }
            }
        }
    }
}

