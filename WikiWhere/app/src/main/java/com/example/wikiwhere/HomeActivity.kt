package com.example.wikiwhere


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import java.io.IOException
import java.lang.Exception


class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private val STORAGE_PERMISSION_CODE = 101
    private var bottomNavigationSelectedItem = 0
    private var navigationSelectedItem:Int = 0
    private lateinit var toolbar1: Toolbar
    private val scanButton: Button? = null
    private lateinit var dl: DrawerLayout
    private val titleToolbar: TextView? = null
    private lateinit var t: ActionBarDrawerToggle
    private val nv: NavigationView? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageReference: StorageReference? = null
    private var profilePic: ImageView? = null
    private var navTitle: TextView? = null
    private lateinit var context: Context
    private var isConnected = false
    private var preferenceManager: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        toolbar1=toolbar
        toolbar1.setTitle("")
        setSupportActionBar(toolbar1)

        dl = findViewById(R.id.activity_home)
        context=this

        // Permission
        if (ContextCompat.checkSelfPermission(this@HomeActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@HomeActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }


        if (ContextCompat.checkSelfPermission(this@HomeActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }


        // Check internet connection
        //checkInternetConnection()


        t = object : ActionBarDrawerToggle(this, activity_home, toolbar, R.string.Open, R.string.Close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                toolbar_title.setText("Wikiwhere")
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                toolbar_title.setText("WikiWhere")
                invalidateOptionsMenu()
            }
        }

        t.isDrawerIndicatorEnabled = true
        dl.addDrawerListener(t)
        t.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)

        val nv = findViewById<NavigationView>(R.id.navigation)
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // No item selected on bottom navbar

        // No item selected on bottom navbar

        bottomNavigationView.setItemIconTintList(null);
        for (i in 0 until bottomNavigationView.getMenu().size()) {
            if(i==0){
                bottomNavigationView.getMenu().getItem(i).setCheckable(true)
            }
            bottomNavigationView.getMenu().getItem(i).setCheckable(false)
        }

        nv.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val id = item.itemId
            for (i in 0 until bottomNavigationView.getMenu().size()) {
                bottomNavigationView.getMenu().getItem(i).setCheckable(false)
            }
            when (id) {
                R.id.nav_home -> {
                    if (item.itemId == navigationSelectedItem) {
                        bottomNavigationSelectedItem = -1
                        dl.closeDrawers()
                        return@OnNavigationItemSelectedListener true
                    }else{
                        navigationSelectedItem = R.id.nav_home
                        bottomNavigationSelectedItem = -1
                        openFragment(HomeFragment())
                        dl.closeDrawers()
                        return@OnNavigationItemSelectedListener true
                    }
                }
                R.id.nav_profile -> {
                    if (item.itemId == navigationSelectedItem) {
                        bottomNavigationSelectedItem = -1
                        dl.closeDrawers()
                        return@OnNavigationItemSelectedListener true
                    }else{
                        navigationSelectedItem = R.id.nav_profile
                        bottomNavigationSelectedItem = -1
                        openFragment(InfoProfileFragment())
                        dl.closeDrawers()
                        return@OnNavigationItemSelectedListener true
                    }

                }
                R.id.nav_settings -> {
                    if (item.itemId == navigationSelectedItem) {
                        bottomNavigationSelectedItem = -1
                        dl.closeDrawers()
                        return@OnNavigationItemSelectedListener true
                    }else{
                        navigationSelectedItem = R.id.nav_settings
                        bottomNavigationSelectedItem = -1
                        openFragment(SettingsFragment())
                        dl.closeDrawers()
                        return@OnNavigationItemSelectedListener true
                    }
                }
                R.id.nav_info -> {
                    dl.closeDrawers()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("App information")
                    builder.setMessage("This app is created by Ludovico Ottobre and Gianmarco Evangelista.\nIt was developed for Mobile Application and Cloud Computing course, La Sapienza Università di Roma")
                    builder.setCancelable(false) // disallow cancel of AlertDialog on click of back button and outside touch
                    builder.setPositiveButton("Ok") { dialog, which -> }
                    builder.create().show()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_logout -> {
                    userLogout()
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        })

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            for (i in 0 until bottomNavigationView.menu.size()) {
                bottomNavigationView.menu.getItem(i).isCheckable = true
            }
            when (item.itemId) {
                R.id.navigation_house -> {
                    if (item.itemId == bottomNavigationSelectedItem) false
                    navigationSelectedItem = -1
                    bottomNavigationSelectedItem = R.id.navigation_house
                    openFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener  true
                }
                R.id.navigation_room -> {
                    if (item.itemId == bottomNavigationSelectedItem) false
                    navigationSelectedItem = -1
                    bottomNavigationSelectedItem = R.id.navigation_room
                    openFragment(ItineraryFragment())
                    return@OnNavigationItemSelectedListener  true
                }
                R.id.navigation_wall -> {
                    if (item.itemId == bottomNavigationSelectedItem) false
                    navigationSelectedItem = -1
                    bottomNavigationSelectedItem = R.id.navigation_wall
                    openFragment(SavedPlacesFragment())
                    return@OnNavigationItemSelectedListener  true
                }
                R.id.photo_menu -> {
                    if (item.itemId == bottomNavigationSelectedItem) false
                    navigationSelectedItem = -1
                    bottomNavigationSelectedItem = R.id.photo_menu
                    openFragment(MLFragment())
                    return@OnNavigationItemSelectedListener  true
                }
            }
            false
        })

        profilePic = nv.getHeaderView(0).findViewById(R.id.nav_profile_pic)
        navTitle = nv.getHeaderView(0).findViewById(R.id.nav_header_textView)

        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        val user = firebaseAuth!!.currentUser

        if (getUserProvider(user) == "GOOGLE") {
            val imageUri =
                Uri.parse(user.photoUrl.toString().replace("s96-c", "s400-c"))
            Picasso.get().load(imageUri).fit().centerInside().into(profilePic)
        } else if (getUserProvider(user) == "FACEBOOK") {
            val imageUri =
                Uri.parse(user.photoUrl.toString() + "?height=500")
            Picasso.get().load(imageUri).fit().centerInside().into(profilePic)
        } else {
            // Get the image stored on Firebase via "User id/Images/Profile Pic.jpg".
            storageReference!!.child(user.uid).child("Images")
                .child("Profile Pic")
                .downloadUrl
                .addOnSuccessListener { uri ->
                    Picasso.get().load(uri).fit().centerInside().into(profilePic) }
        }

        if (getUserProvider(user!!) == "FIREBASE" && !user.isEmailVerified) {
            AlertDialog.Builder(context)
                .setTitle("Email verification")
                .setMessage("Your account is not activated since your email address is not verified. Activate your account clicking on the activation link sent at " + user.email + ". If you didn't receive the email click on 'Send' button to send another email.")
                .setCancelable(false) // disallow cancel of AlertDialog on click of back button and outside touch
                .setPositiveButton(
                    "Send"
                ) { dialog, which ->
                    user.sendEmailVerification()
                    Toast.makeText(
                        applicationContext,
                        "Email verification sent!",
                        Toast.LENGTH_SHORT
                    ).show()
                    userLogout()
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, which -> userLogout() }
                .show()
        }

        navTitle!!.setText(user.getDisplayName())

        //loading the default fragment
        bottomNavigationSelectedItem = -1
        navigationSelectedItem = R.id.nav_home
        loadFragment(HomeFragment())
    }


    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        println(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@HomeActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@HomeActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                if ((ContextCompat.checkSelfPermission(this@HomeActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this@HomeActivity, "GPS Permission Granted", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "GPS Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun openFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, null)
        fragmentTransaction.addToBackStack(null) //this will add it to back stack
        fragmentTransaction.commit()
    }

    private fun getUserProvider(user: FirebaseUser): String? {
        val infos = user.providerData
        var provider = "FIREBASE"
        for (ui in infos) {
            if (ui.providerId == GoogleAuthProvider.PROVIDER_ID) provider = "GOOGLE" else if (ui.providerId == FacebookAuthProvider.PROVIDER_ID) provider = "FACEBOOK"
        }
        return provider
    }

    private fun userLogout() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun checkInternetConnection() {
        if (!isConnected) {
            MaterialAlertDialogBuilder(this)
                    .setTitle("You are disconnected!")
                    .setMessage("Please activate internet connection")
                    .setCancelable(false) // disallow cancel of AlertDialog on click of back button and outside touch
                    .setNegativeButton("Cancel") { dialog, which -> checkInternetConnection() }
                    .show()
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }



}