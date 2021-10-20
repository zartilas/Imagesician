package unipi.p17168.imagesician.activities


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.databinding.ActivitySignInBinding
import com.google.android.material.snackbar.Snackbar
import unipi.p17168.imagesician.database.DBHelper
import unipi.p17168.imagesician.utils.Constants
import unipi.p17168.imagesician.utils.ToolBox
import java.util.jar.Manifest


class SignInActivity : BaseActivity() {

    // ~~~~~~~VARIABLES~~~~~~~
    //var
    private lateinit var binding: ActivitySignInBinding

    // ~~~~~~~~~~~~~~~~~~~~~~~

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_REG_USERS_SNACKBAR)) {
            binding.inputTxtEmail.setText(intent.getStringExtra(Constants.EXTRA_USER_EMAIL).toString())
        }
        ToolBox().setupPermissions(applicationContext,this@SignInActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE,100)
        setupUI()
        setupClickListeners()

    }



    private fun setupUI() {
        binding.apply {
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

    private fun setupClickListeners() {
        binding.apply {
                txtViewSignUp.setOnClickListener {
                    // Launch the sign up screen when the user clicks on the sign up text.
                    val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                    startActivity(intent)
                }
                txtViewForgotPassword.setOnClickListener {
                    // Launch the forgot password screen when the user clicks on the forgot password text.
                    val intent = Intent(this@SignInActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                btnSignIn.setOnClickListener {
                    ToolBox().hideSoftKeyboard(this@SignInActivity, it)
                    signInUser()}
        }
    }
    private fun signInUser() {
        if (validateFields()) {
            // Show the progress dialog.
            showProgressDialog()
            Log.e("Sign","Ok")
            binding.apply {
                // Get the text from editText and trim the space
                val email = inputTxtEmail.text.toString().trim { it <= ' ' }
                val password = inputTxtPassword.text.toString().trim { it <= ' ' }

                // Log-In using FirebaseAuth
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        Log.e("FirebaseAuth","Ok")
                        FirebaseAuth.getInstance()
                        if (task.isSuccessful) {
                            DBHelper().getUserDetails(this@SignInActivity)

                        } else {

                            // Hide the progress dialog
                            hideProgressDialog()

                            ToolBox().showSnackBar(binding.root,
                                ContextCompat.getColor(this@SignInActivity,R.color.colorErrorBackgroundSnackbar),
                                ContextCompat.getColor(this@SignInActivity,R.color.colorStrings),
                                getString(R.string.txt_Something_went_wrong),
                                getString(R.string.txt_all_right),
                                Snackbar.ANIMATION_MODE_SLIDE).show()
                        }
                    }
            }
        }
        else
            binding.btnSignIn.startAnimation(AnimationUtils.loadAnimation(this@SignInActivity, R.anim.anim_shake))
    }

    /**
     * A function to notify user that logged in success and get the user details from the FireStore database after authentication.
     */
    fun userLoggedInSuccess() {

        // Hide the progress dialog.
        hideProgressDialog()

        finish()
        // Redirect the user to Dashboard Screen after log in.
        goToImagesicialActivity(this@SignInActivity)

    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(inputTxtEmail.text.toString().trim { it <= ' ' }) -> {
                    ToolBox().showSnackBar(binding.root,
                        ContextCompat.getColor(this@SignInActivity,R.color.colorErrorBackgroundSnackbar),
                        ContextCompat.getColor(this@SignInActivity,R.color.colorStrings),
                        "Please enter your email.",
                        "OK",
                        Snackbar.ANIMATION_MODE_SLIDE).show()
                    false
                }

                TextUtils.isEmpty(inputTxtPassword.text.toString().trim { it <= ' ' }) -> {
                    ToolBox().showSnackBar(binding.root,
                        ContextCompat.getColor(this@SignInActivity,R.color.colorErrorBackgroundSnackbar),
                        ContextCompat.getColor(this@SignInActivity,R.color.colorStrings),
                        "Please enter your password.",
                        "OK",
                        Snackbar.ANIMATION_MODE_SLIDE).show()
                    false
                }

                else -> true
            }
        }
    }

    fun View.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackbar.show()
        }
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}
