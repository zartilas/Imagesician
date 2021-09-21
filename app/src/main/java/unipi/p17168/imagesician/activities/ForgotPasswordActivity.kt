package unipi.p17168.imagesician.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.databinding.ActivityForgotPasswordBinding
import unipi.p17168.imagesician.utils.ToolBox

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        setupUI()
        setUpClickListeners()
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
        }
    }

    private fun setUpClickListeners() {
        binding.apply {
            btnSend.setOnClickListener{ resetPassword() }
        }
    }


    private fun resetPassword() {
        binding.apply {
            if (validateFields()) {
                // Show the progress dialog.
                showProgressDialog()

                // This piece of code is used to send the reset password link to the user's email id if the user is registered.
                FirebaseAuth.getInstance().sendPasswordResetEmail(inputTxtEmail.text.toString())
                    .addOnCompleteListener { task ->

                        // Hide the progress dialog
                        hideProgressDialog()

                        if (task.isSuccessful) {
                            ToolBox().showSnackBar(binding.root,
                                ContextCompat.getColor(this@ForgotPasswordActivity,R.color.colorSuccessBackgroundSnackbar),
                                ContextCompat.getColor(this@ForgotPasswordActivity,R.color.colorStrings),
                                getString(R.string.txt_password_reset_mail_sent),
                                getString(R.string.txt_all_right),
                                Snackbar.ANIMATION_MODE_SLIDE).show()
                            finish()
                        } else {
                            ToolBox().showSnackBar(binding.root,
                                ContextCompat.getColor(this@ForgotPasswordActivity,R.color.colorErrorBackgroundSnackbar),
                                ContextCompat.getColor(this@ForgotPasswordActivity,R.color.colorStrings),
                                getString(R.string.txt_Something_went_wrong),
                                getString(R.string.txt_all_right),
                                Snackbar.ANIMATION_MODE_SLIDE).show()
                        }
                    }
            }
            else
                btnSend.startAnimation(
                    AnimationUtils.loadAnimation(this@ForgotPasswordActivity,
                    R.anim.anim_shake))
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(inputTxtEmail.text.toString().trim { it <= ' ' }) -> {
                    ToolBox().showSnackBar(binding.root,
                        ContextCompat.getColor(this@ForgotPasswordActivity,R.color.colorErrorBackgroundSnackbar),
                        ContextCompat.getColor(this@ForgotPasswordActivity,R.color.colorStrings),
                        getString(R.string.txt_error_empty_email),
                        getString(R.string.txt_all_right),
                        Snackbar.ANIMATION_MODE_SLIDE).show()

                    inputTxtLayoutEmail.requestFocus()
                    inputTxtLayoutEmail.error = getString(R.string.txt_error_empty_email)
                    false
                }
                else -> true
            }
        }
    }
}