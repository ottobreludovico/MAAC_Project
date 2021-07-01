package com.example.wikiwhere

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var emailResetPassword: EditText
    private lateinit var resetButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var signInButton: TextView
    private lateinit var signUpButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        emailResetPassword = findViewById(R.id.emailResetPasswordEditText)
        resetButton = findViewById(R.id.resetPasswordButton)
        auth = FirebaseAuth.getInstance()
        signInButton = findViewById(R.id.signInRPButton)
        signUpButton = findViewById(R.id.signUpRPButton)
        resetButton.setOnClickListener(View.OnClickListener {
            val email = emailResetPassword.getText().toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(application, "Enter your mail address", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            auth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ResetPasswordActivity, "We send you an e-mail", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        })
        signInButton.setOnClickListener(View.OnClickListener { navigateSignIn() })
        signUpButton.setOnClickListener(View.OnClickListener { navigateSignUp() })
    }

    private fun navigateSignIn() {
        val intent = Intent(this, SigninActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateSignUp() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish()
    }
}
