package unipi.p17168.imagesician

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import unipi.p17168.imagesician.activities.BaseActivity
import unipi.p17168.imagesician.activities.SignInActivity
import unipi.p17168.imagesician.database.DBHelper
import unipi.p17168.imagesician.databinding.FragmentSettingsBinding
import unipi.p17168.imagesician.models.User
import unipi.p17168.imagesician.utils.Constants

class SettingsFragment : Fragment(){

    //~~~~~~~VARIABLES~~~~~~~

    //VAR
    private var _binding : FragmentSettingsBinding? = null
    private lateinit var modelUser: User

    //VAL
    private val binding get() = _binding!!
    private val contextSettingsFragment get() = this@SettingsFragment.requireContext()
    private val dbFirestore = FirebaseFirestore.getInstance()


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
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
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
                successProfileDetailsFromFirestore(user)
            }
    }

    private fun successProfileDetailsFromFirestore(user: User) {

        modelUser = user

        binding.apply {
            // Populate the user details in the input texts.
            textViewNameValue.text = modelUser.fullName
            textViewEmailValue.text = modelUser.email
            textViewDateRegisteredValue.text = Constants.DATE_FORMAT.format(modelUser.dateRegistered)
        }
    }
}
