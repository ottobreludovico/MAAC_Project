package com.example.wikiwhere

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var emailSignUp: EditText
    private  lateinit var confirmEmailSignUp:EditText
    private  lateinit var passwordSignUp:EditText
    private  lateinit var confirmPasswordSignUp:EditText
    private lateinit var signUpButton: Button
    private lateinit var alreadyRegisteredButton: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var nameEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        var fAuth = FirebaseAuth.getInstance();

        if(fAuth.currentUser!=null) {
            startActivity(Intent(applicationContext, MainActivity::class.java));
            finish();
        }

        emailSignUp = findViewById<EditText>(R.id.emailSignUpEditText)
        confirmEmailSignUp = findViewById<EditText>(R.id.confirmEmailSignUpEditText)
        passwordSignUp = findViewById<EditText>(R.id.passwordSignUpEditText)
        confirmPasswordSignUp = findViewById<EditText>(R.id.confirmPasswordSignUpEditText)
        auth = FirebaseAuth.getInstance()
        signUpButton = findViewById<Button>(R.id.signUpButton)
        alreadyRegisteredButton = findViewById<TextView>(R.id.alreadyRegisteredButton)
        databaseReference = FirebaseDatabase.getInstance().getReference()
        nameEditText = findViewById<EditText>(R.id.nameSignUpEditText)


        signUpButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    val name: String = nameEditText.getText().toString().trim({ it <= ' ' })
                    val email: String = emailSignUp.getText().toString()
                    val confirmEmail: String = confirmEmailSignUp.getText().toString()
                    val pass: String = passwordSignUp.getText().toString()
                    val confirmPass: String = confirmPasswordSignUp.getText().toString()
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(applicationContext, "Enter your name", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(applicationContext, "Please enter your E-mail address", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (TextUtils.isEmpty(confirmEmail)) {
                        Toast.makeText(applicationContext, "Please confirm your E-mail address", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (email != confirmEmail) {
                        Toast.makeText(applicationContext, "Emails must be equals", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (TextUtils.isEmpty(pass)) {
                        Toast.makeText(applicationContext, "Please enter your Password", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (TextUtils.isEmpty(confirmPass)) {
                        Toast.makeText(applicationContext, "Please confirm your Password", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (pass.length == 0) {
                        Toast.makeText(applicationContext, "Please enter your Password", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (confirmPass.length == 0) {
                        Toast.makeText(applicationContext, "Please confirm your Password", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (pass.length < 8) {
                        Toast.makeText(applicationContext, "Password must be more than 8 digit", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (pass != confirmPass) {
                        Toast.makeText(applicationContext, "Passwords must be equals", Toast.LENGTH_LONG).show()
                        return
                    } else {
                        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this@SignupActivity, OnCompleteListener<AuthResult?> { task ->
                            if (!task.isSuccessful) {
                                try {
                                    throw task.exception!!
                                } // if user enters wrong password.
                                catch (weakPassword: FirebaseAuthWeakPasswordException) {
                                    Toast.makeText(this@SignupActivity, "Wrong password.", Toast.LENGTH_LONG).show()
                                } // if user enters wrong email.
                                catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(this@SignupActivity, "Wrong email.", Toast.LENGTH_LONG).show()
                                } catch (existEmail: FirebaseAuthUserCollisionException) {
                                    Toast.makeText(this@SignupActivity, "An account with this email already exists! If you don't remember your password, reset it.", Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    Log.d("SIGN UP", "onComplete: " + e.message)
                                }
                            } else {
                                val user = FirebaseAuth.getInstance().currentUser
                                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                                user!!.updateProfile(profileUpdates)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                //Success
                                            }
                                        }
                                Toast.makeText(applicationContext, "User information updated", Toast.LENGTH_LONG).show()
                                user.sendEmailVerification()
                                Toast.makeText(applicationContext, "Activate your account by clicking on the link sent at the email " + user.email, Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                                finish()
                            }
                        })
                    }
                }
            })

            alreadyRegisteredButton.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    navigateSignIn()
                }
            })
        }



        private fun navigateSignIn() {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }