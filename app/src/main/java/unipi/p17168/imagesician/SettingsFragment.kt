package unipi.p17168.imagesician

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import unipi.p17168.imagesician.activities.BaseActivity
import unipi.p17168.imagesician.databinding.FragmentSettingsBinding
import unipi.p17168.imagesician.models.User


import unipi.p17168.imagesician.utils.Constants
import javax.inject.Inject


class SettingsFragment : Fragment(){

    //~~~~~~~VARIABLES~~~~~~~

    //VAR
    private var _binding : FragmentSettingsBinding? = null

    private lateinit var modelUser: User
  /*  private val modelUser: User by lazy{
        User()
    }*/

    /*  val user = document.toObject(User::class.java)!!*/



    //VAL
    private val binding get() = _binding!!
    private val contextSettingsFragment get() = this@SettingsFragment.requireContext()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init(){
        setupClickListeners()

    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            BaseActivity().goToSignInActivity(contextSettingsFragment)
        }
    }



    fun successProfileDetailsFromFirestore(user: User) {

        // Hide the progress dialog
       // hideProgressDialog()
        Log.d("SettingsFragment","Input Success")
        modelUser = user
        val myname= modelUser.fullName
        Log.d("SettingsFragment", "myname")

        binding.apply {

            /*   textViewNameValue.text = "Hi Boss"*/
            // Populate the user details in the input texts.
            textViewName.text = modelUser.fullName
            textViewNameValue.text = modelUser.fullName
            textViewEmailValue.text = modelUser.email
            textViewDateRegisteredValue.text = Constants.DATE_FORMAT.format(modelUser.dateRegistered)
        }

    }


}