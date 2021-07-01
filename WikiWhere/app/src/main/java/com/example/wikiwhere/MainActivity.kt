package com.example.wikiwhere

import android.content.Intent
import android.net.sip.SipErrorCode.TIME_OUT
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.postDelayed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var indicatorProgressBar: IndicatorProgressBar
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferenceManager.edit()
        var theme = 0
        theme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) preferenceManager.getInt("Theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) else preferenceManager.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(theme)
        setContentView(R.layout.activity_main)


        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.addAuthStateListener(authStateListener)
        val user = firebaseAuth.currentUser
        indicatorProgressBar = findViewById(R.id.indicatorProgressBarId)
        //indicatorProgressBar.indicatorPositions = listOf(0.13F, 0.34F, 0.57F, 0.85F, 0.92F)

    }


    var authStateListener =
        AuthStateListener { firebaseAuth ->
            /*indicatorProgressBar.setOnClickListener {
                if(indicatorProgressBar.progress >= 1F){
                    updateCurrentTime()
                }
            }*/
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                val intent = Intent(this@MainActivity, SigninActivity::class.java)
                startActivity(intent)
                finish()
            } else { // (firebaseUser != null)
                /*val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent);
                finish()*/
                scope.launch {
                    while (indicatorProgressBar.progress <= 1F){
                        delay(33)
                        runOnUiThread{
                            indicatorProgressBar.progress += 0.015F
                        }
                    }
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent);
                    finish()
                }

            }
        }

    private fun updateCurrentTime() {
        scope.launch {
            while (indicatorProgressBar.progress <= 1F){
                delay(33)
                runOnUiThread{
                    indicatorProgressBar.progress += 0.015F
                }
            }

        }
    }




}