package com.example.wikiwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar!!.setTitle("Register")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        var registerR = loginBtn;
        var emailR = email;
        var nameR = name;
        var surnameR = surname;
        var psw = password;

        var fAuth = FirebaseAuth.getInstance();

        if(fAuth.currentUser!=null) {
            startActivity(Intent(applicationContext, MainActivity::class.java));
            finish();
        }

        registerR.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //Your code here
                var emailT: String = emailR.text.toString().trim();
                var pswT: String = psw.text.toString().trim();

                if (TextUtils.isEmpty(emailT)) {
                    emailR.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pswT)) {
                    psw.setError("Password is required");
                    return;
                }

                if (pswT.length < 6) {
                    psw.setError("Password must be >= 6 characters");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(emailT, pswT).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "User created", Toast.LENGTH_SHORT).show();
                        startActivity(Intent(applicationContext, MainActivity::class.java));
                    } else {
                        Toast.makeText(applicationContext,"Error ! " + task.exception , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        })
    }
}