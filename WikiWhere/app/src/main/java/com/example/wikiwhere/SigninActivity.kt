package com.example.wikiwhere

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager : CallbackManager
    private lateinit var emailSignIn: EditText
    private  lateinit var passwordSignIn:EditText
    private lateinit var databaseReference: DatabaseReference
    private  lateinit var resetPasswordButton: TextView
    private val mGoogleSignInClient: GoogleSignInClient? = null
    private val mCallbackManager: CallbackManager? = null
    private lateinit var rememberMe: CheckBox
    private lateinit var preferenceManager: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val RC_SIGN_IN = 9001
    private val STORAGE_PERMISSION_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        //supportActionBar!!.setTitle("Login")
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        emailSignIn = findViewById<EditText>(R.id.emailSignInEditText)
        passwordSignIn = findViewById<EditText>(R.id.passwordSignInEditText)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)
        rememberMe = findViewById<CheckBox>(R.id.rememberMeCheckBox)

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        editor = preferenceManager.edit()

        var fAuth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        notRegisteredButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(applicationContext, SignupActivity::class.java))
            }
        })

        databaseReference = FirebaseDatabase.getInstance().reference

        //Get Firebase auth instance

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()


        callbackManager = CallbackManager.Factory.create()

        facebookSignInButton.setPermissions("email", "public_profile")
        facebookSignInButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)

            }

            override fun onCancel() {
                Toast.makeText(applicationContext,"Login cancelled", Toast.LENGTH_SHORT).show();
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(applicationContext,"Error ! " + exception.message , Toast.LENGTH_SHORT).show();
            }
        })




        signInButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //Your code here
                val email = emailSignIn.getText().toString().trim { it <= ' ' }
                val password = passwordSignIn.getText().toString().trim { it <= ' ' }

                if (TextUtils.isEmpty(email)) {
                    emailSignIn.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordSignIn.setError("Password is required");
                    return;
                }

                if (password.length < 6) {
                    passwordSignIn.setError("Password must be >= 6 characters");
                    return;
                }

                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener() { task ->
                    if (!task.isSuccessful) {
                        // there was an error
                        try {
                            throw task.exception!!
                        } // if user enters wrong email.
                        catch (invalidEmail: FirebaseAuthInvalidUserException) {
                            Toast.makeText(applicationContext, "Wrong email.", Toast.LENGTH_SHORT).show()
                        } // if user enters wrong password.
                        catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(applicationContext, "Wrong password.", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.d("SIGN IN", "onComplete: " + e.message)
                        }
                    } else {
                        val user = auth.currentUser
                        //Check if user exist yet  in rails and if not it creates him
                        val intent = Intent(this@SigninActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }


            }
        })


        googleSignInButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                signIn()
            }
        })

        resetPasswordButton.setOnClickListener(View.OnClickListener { navigateResetPassword() })

        rememberMe.setChecked(preferenceManager.getBoolean("RememberMe", false))
        rememberMe.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                editor.putBoolean("RememberMe", true)
                editor.apply()
            } else {
                editor.putBoolean("RememberMe", false)
                editor.apply()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
                Toast.makeText(applicationContext,"Successful" , Toast.LENGTH_SHORT).show();
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(applicationContext,"Error ! " + e.message , Toast.LENGTH_SHORT).show();
            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@SigninActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@SigninActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(user.uid)) {
                            // run some code
                        } else {

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
                val intent = Intent(this@SigninActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@SigninActivity, "Authentication with Facebook failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChild(user.uid)) {
                                // run some code
                            } else {

                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                    val intent = Intent(this@SigninActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Authentication with Google failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun updateUI(user: FirebaseUser?) {

    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    private fun navigateSignUp() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun navigateResetPassword() {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

}