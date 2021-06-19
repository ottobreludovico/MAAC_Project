package com.example.wikiwhere


import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    private val bottomNavigationView: BottomNavigationView? = null

    private val STORAGE_PERMISSION_CODE = 101
    private var bottomNavigationSelectedItem = 0
    private var navigationSelectedItem:Int = 0
    private lateinit var toolbar1: Toolbar
    private val scanButton: Button? = null
    private lateinit var dl: DrawerLayout
    private val titleToolbar: TextView? = null
    private lateinit var t: ActionBarDrawerToggle
    private val nv: NavigationView? = null
    private val firebaseAuth: FirebaseAuth? = null
    private val profilePic: ImageView? = null
    private val navTitle: TextView? = null
    private val context: Context? = null
    private var isConnected = false
    private var preferenceManager: SharedPreferences? = null

    private var homeFragment: HomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        toolbar1=toolbar
        toolbar1.setTitle("")
        setSupportActionBar(toolbar1)

        dl = findViewById(R.id.activity_home)


        // Permission
        if (ContextCompat.checkSelfPermission(this@HomeActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@HomeActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
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
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // No item selected on bottom navbar

        // No item selected on bottom navbar

        bottomNavigationView.setItemIconTintList(null);
        for (i in 0 until bottomNavigationView.getMenu().size()) {
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
                        true
                    }else{
                        navigationSelectedItem = R.id.nav_home
                        bottomNavigationSelectedItem = -1
                        loadFragment(HomeFragment())
                        dl.closeDrawers()
                        true
                    }
                }
                R.id.nav_profile -> {
                    if (item.itemId == navigationSelectedItem) {
                        bottomNavigationSelectedItem = -1
                        dl.closeDrawers()
                        true
                    }else{
                        navigationSelectedItem = R.id.nav_profile
                        bottomNavigationSelectedItem = -1
                        loadFragment(InfoProfileFragment())
                        dl.closeDrawers()
                        true
                    }

                }
                R.id.nav_settings -> {

                }
                R.id.nav_info -> {

                }
                R.id.nav_logout -> {
                    userLogout()
                    true
                }
            }
            false
        })

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            for (i in 0 until bottomNavigationView.menu.size()) {
                bottomNavigationView.menu.getItem(i).isCheckable = true
            }
            when (item.itemId) {
                R.id.navigation_house -> {

                }
                R.id.navigation_room -> {

                }
                R.id.navigation_wall -> {

                }
            }
            false
        })


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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@HomeActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@HomeActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
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