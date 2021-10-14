package unipi.p17168.imagesician.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.database.DBHelper
import unipi.p17168.imagesician.databinding.ActivitySignUpBinding
import unipi.p17168.imagesician.models.User
import unipi.p17168.imagesician.utils.ToolBox

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
         val view = binding.root
        setContentView(view)

        init()
    }

    private fun init(){
        setupUI()
        setupClickListeners()
    }


    private fun setupClickListeners() {
        binding.apply {
            txtViewSignIn.setOnClickListener { onBackPressed() }
            btnSignUp.setOnClickListener{
                registerUser()
                ToolBox().hideSoftKeyboard(this@SignUpActivity, it)}
        }

    }

    private fun setupUI() {
        binding.apply {
            inputTxtName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    inputTxtLayoutName.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            inputTxtEmail.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    inputTxtLayoutEmail.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            inputTxtPassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    inputTxtLayoutPassword.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateRegisterDetails(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(inputTxtName.text.toString().trim { it <= ' ' }) -> {
                    ToolBox().showSnackBar(binding.root,
                        ContextCompat.getColor(this@SignUpActivity,R.color.colorErrorBackgroundSnackbar),
                        ContextCompat.getColor(this@SignUpActivity,R.color.colorStrings),
                        "Please enter your username.",
                        "OK",
                        Snackbar.ANIMATION_MODE_SLIDE).show()
                    false
                }

                TextUtils.isEmpty(inputTxtEmail.text.toString().trim { it <= ' ' }) -> {
                    ToolBox().showSnackBar(binding.root,
                        ContextCompat.getColor(this@SignUpActivity,R.color.colorErrorBackgroundSnackbar),
                        ContextCompat.getColor(this@SignUpActivity,R.color.colorStrings),
                        "Please enter your email.",
                        "OK",
                        Snackbar.ANIMATION_MODE_SLIDE).show()
                    false
                }

                TextUtils.isEmpty(inputTxtPassword.text.toString().trim { it <= ' ' }) -> {
                    ToolBox().showSnackBar(binding.root,
                        ContextCompat.getColor(this@SignUpActivity,R.color.colorErrorBackgroundSnackbar),
                        ContextCompat.getColor(this@SignUpActivity,R.color.colorStrings),
                        "Please enter your password.",
                        "OK",
                        Snackbar.ANIMATION_MODE_SLIDE).show()
                    false
                }

                else -> true
            }
        }
    }
    /**
     * A function to register the user with email and password using FirebaseAuth.
     */
    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {
            //ToolBox().hideSoftKeyboard(this,)
            // Show the progress dialog.
            showProgressDialog()

            binding.apply  {
                val email: String = inputTxtEmail.text.toString().trim { it <= ' ' }
                val password: String = inputTxtPassword.text.toString().trim { it <= ' ' }

                // Create an instance and create a register a user with email and password.
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result.user!!

                            // Instance of User data model class.
                            val user = User(
                                firebaseUser.uid,
                                inputTxtName.text.toString().trim { it <= ' ' },
                                inputTxtEmail.text.toString().trim { it <= ' ' }
                            )


                            // Pass the required values in the constructor.
                            DBHelper().registerUser(this@SignUpActivity, user)
                        } else {

                            // Hide the progress dialog
                            //hideProgressDialog()

                            // If the registering is not successful then show the error message.
                            ToolBox().showSnackBar(binding.root,
                                ContextCompat.getColor(this@SignUpActivity,R.color.colorErrorBackgroundSnackbar),
                                ContextCompat.getColor(this@SignUpActivity,R.color.colorStrings),
                                "Sorry, try later.",
                                "OK",
                                Snackbar.ANIMATION_MODE_SLIDE).show()
                        }
                    }
            }
        }
        else
            binding.btnSignUp.startAnimation(AnimationUtils.loadAnimation(this@SignUpActivity, R.anim.anim_shake))
    }

    /**
     * A function to notify the success result of Firestore entry when the user is registered successfully.
     */
    fun userRegistrationSuccess() {

        //Hide the progress dialog
        hideProgressDialog()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Register Screen
        finish()
        goToSignInActivity(this@SignUpActivity)
    }
}