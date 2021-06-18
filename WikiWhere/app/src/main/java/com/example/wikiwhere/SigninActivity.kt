package com.example.wikiwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager : CallbackManager

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

        var emailL = email
        var pswL = password
        var btn = loginBtn
        var reg = registerLink

        var fAuth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        reg.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(applicationContext, SigninActivity::class.java))
            }
        })


        callbackManager = CallbackManager.Factory.create()

        login_button.setPermissions("email", "public_profile")
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
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




        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //Your code here
                var emailLL: String = emailL.text.toString().trim();
                var pswLL: String = pswL.text.toString().trim();

                if (TextUtils.isEmpty(emailLL)) {
                    emailL.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pswLL)) {
                    pswL.setError("Password is required");
                    return;
                }

                if (pswLL.length < 6) {
                    pswL.setError("Password must be >= 6 characters");
                    return;
                }

                fAuth.signInWithEmailAndPassword(emailLL,pswLL).addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Login Successfull", Toast.LENGTH_SHORT).show();
                        startActivity(Intent(applicationContext, MainActivity::class.java));
                    } else {
                        Toast.makeText(applicationContext,"Error ! " + task.exception , Toast.LENGTH_SHORT).show();
                    }
                }


            }
        })


        googleLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                signIn()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
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
                Toast.makeText(applicationContext,"Eok ! " , Toast.LENGTH_SHORT).show();
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(applicationContext,"Error ! " + e.message , Toast.LENGTH_SHORT).show();
            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                    startActivity(Intent(applicationContext, HomeActivity::class.java));
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    /*Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)*/
                    Toast.makeText(applicationContext, "Login Successfull", Toast.LENGTH_SHORT).show();
                    startActivity(Intent(applicationContext, HomeActivity::class.java));
                } else {
                    // If sign in fails, display a message to the user.
                    /*Log.w(TAG, "signInWithCredential:failure", task.exception)
                     updateUI(null)*/
                    Toast.makeText(applicationContext,"Error ! " + task.exception , Toast.LENGTH_SHORT).show();
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

}