package com.example.wikiwhere

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.addAuthStateListener(authStateListener)
        val user = firebaseAuth.currentUser

    }

    var authStateListener =
        AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                val intent = Intent(this@MainActivity, SigninActivity::class.java)
                startActivity(intent)
                finish()
            } else { // (firebaseUser != null)
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent);
                finish()
            }
        }
}