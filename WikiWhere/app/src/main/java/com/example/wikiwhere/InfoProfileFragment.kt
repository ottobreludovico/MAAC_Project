package com.example.wikiwhere

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_info_profile.*
import kotlinx.android.synthetic.main.fragment_info_profile.view.*
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var emailView:TextView? = null
    private val PICK_IMAGE = 123
    private val tabLayout: TabLayout? = null
    private val viewPager: ViewPager? = null
    private val profilePicImageView: ImageView? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private var imagePath: Uri? = null
    private lateinit var changeEmailButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var deleteUserButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_info_profile, container, false)
        // Inflate the layout for this fragment
        view.editNameProfileButton.setOnClickListener(View.OnClickListener { editName() })




        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        user = firebaseAuth.currentUser!!
        databaseReference = firebaseDatabase.getReference(user.uid)
        storageReference = FirebaseStorage.getInstance().reference

        if (user == null) {
            startActivity(Intent(activity, SigninActivity::class.java))
            activity?.finish()
        }

        changeEmailButton = view.findViewById(R.id.changeEmailButton)
        changePasswordButton = view.findViewById(R.id.changePasswordButton)

        changeEmailButton.setOnClickListener { changeEmail() }

        changePasswordButton.setOnClickListener { changePassword() }

        val provider = getUserProvider(user)
        if (provider !== "FIREBASE") {
            changeEmailButton.visibility = View.GONE
            changePasswordButton.visibility = View.GONE
        }


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                view.nameProfileTextView.setText(user.displayName?.toString())
                emailProfileTextView.setText(user.email)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, databaseError.code, Toast.LENGTH_SHORT).show()
            }
        })


        if (getUserProvider(user) == "FIREBASE")
            view.imageProfileImageView.setOnClickListener(View.OnClickListener {
            val profileIntent = Intent()
            profileIntent.type = "image/*"
            profileIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(profileIntent, "Select Image."),
                PICK_IMAGE
            )
        })


        if (getUserProvider(user) == "GOOGLE") {
            val imageUri =
                Uri.parse(user.photoUrl.toString().replace("s96-c", "s400-c"))
            Picasso.get().load(imageUri).fit().centerInside().into(view.imageProfileImageView)
        } else if (getUserProvider(user) == "FACEBOOK") {
            val imageUri =
                Uri.parse(user.photoUrl.toString() + "?height=500")
            Picasso.get().load(imageUri).fit().centerInside().into(view.imageProfileImageView)
        } else {
            // Get the image stored on Firebase via "User id/Images/Profile Pic.jpg".
            storageReference!!.child(user!!.uid).child("Images").child("Profile Pic")
                .getDownloadUrl()
                .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                    Picasso.get().load(uri).fit().centerInside().into(view.imageProfileImageView)
                })
        }

        view.nameProfileTextView.setText(user.displayName?.toString())
        view.emailProfileTextView.setText(user.email)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imagePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    (activity as HomeActivity).contentResolver, imagePath
                )
                imageProfileImageView.setImageBitmap(bitmap)
                if (imagePath != null) sendUserImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendUserImage() {
        // Get "User UID" from Firebase > Authentification > Users.
        val imageReference: StorageReference =
            storageReference!!.child(firebaseAuth.uid!!).child("Images")
                .child("Profile Pic") //User id/Images/Profile Pic.jpg
        val uploadTask: UploadTask = imageReference.putFile(imagePath!!)
        uploadTask.addOnFailureListener(OnFailureListener {
            Toast.makeText(context, "Error: Uploading profile picture", Toast.LENGTH_SHORT)
                .show()
        }).addOnSuccessListener(OnSuccessListener<Any?> {
            Toast.makeText(
                context,
                "Profile picture uploaded",
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    private fun getUserProvider(user: FirebaseUser): String? {
        val infos = user.providerData
        var provider = "FIREBASE"
        for (ui in infos) {
            if (ui.providerId == GoogleAuthProvider.PROVIDER_ID) provider =
                "GOOGLE" else if (ui.providerId == FacebookAuthProvider.PROVIDER_ID) provider =
                "FACEBOOK"
        }
        return provider
    }

    private fun editName() {
        val inflater = layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.layout_custom_dialog_edit_name, null)
        val setUserName = alertLayout.findViewById<EditText>(R.id.setUserName)
        MaterialAlertDialogBuilder(alertLayout.context)
            .setTitle("Edit name")
            .setMessage("Insert the new name")
            .setView(alertLayout) // this is set the view from XML inside AlertDialog
            .setCancelable(false) // disallow cancel of AlertDialog on click of back button and outside touch
            .setPositiveButton(
                "Save"
            ) { dialog, which ->
                val newName = setUserName.text.toString()
                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            nameProfileTextView.setText(user!!.displayName)
                            Toast.makeText(
                                context,
                                "Name updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Error while updating name.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                setUserName.onEditorAction(EditorInfo.IME_ACTION_DONE)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun changeEmail() {
        val inflater = layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.layout_custom_dialog_edit_email, null)
        val setUserEmail = alertLayout.findViewById<EditText>(R.id.setUserEmail)
        val confirmUserEmail = alertLayout.findViewById<EditText>(R.id.confirmUserEmail)
        MaterialAlertDialogBuilder(alertLayout.context)
                .setTitle("Edit email")
                .setMessage("Insert the new email")
                .setView(alertLayout) // this is set the view from XML inside AlertDialog
                .setCancelable(false) // disallow cancel of AlertDialog on click of back button and outside touch
                .setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
                    val newEmail = setUserEmail.text.toString()
                    val confirmEmail = confirmUserEmail.text.toString()
                    if (TextUtils.isEmpty(newEmail)) {
                        Toast.makeText(context, "Please enter your E-mail address", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    if (TextUtils.isEmpty(confirmEmail)) {
                        Toast.makeText(context, "Please confirm your E-mail address", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    if (newEmail != confirmEmail) {
                        Toast.makeText(context, "Emails must be equals!", Toast.LENGTH_SHORT).show()
                        return@OnClickListener
                    } else {
                        user.updateEmail(newEmail)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Email updated!", Toast.LENGTH_SHORT).show()
                                    } else Toast.makeText(context, "Error while updating email", Toast.LENGTH_SHORT).show()
                                }
                    }
                    setUserEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
                })
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun changePassword() {
        val inflater = layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.layout_custom_dialog_edit_password, null)
        val setUserPassword = alertLayout.findViewById<EditText>(R.id.setUserPassword)
        val confirmUserPassword = alertLayout.findViewById<EditText>(R.id.confirmUserPassword)
        MaterialAlertDialogBuilder(alertLayout.context)
                .setTitle("Change password")
                .setMessage("Insert the new password")
                .setView(alertLayout) // this is set the view from XML inside AlertDialog
                .setCancelable(false) // disallow cancel of AlertDialog on click of back button and outside touch
                .setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
                    val newPassword = setUserPassword.text.toString()
                    val confirmPassword = confirmUserPassword.text.toString()
                    if (TextUtils.isEmpty(newPassword)) {
                        Toast.makeText(context, "Please enter your Password", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    if (TextUtils.isEmpty(confirmPassword)) {
                        Toast.makeText(context, "Please confirm your Password", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    if (newPassword.length == 0) {
                        Toast.makeText(context, "Please enter your Password", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    if (confirmPassword.length == 0) {
                        Toast.makeText(context, "Please confirm your Password", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    if (newPassword.length < 8) {
                        Toast.makeText(context, "Password must be more than 8 digit", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    if (newPassword != confirmPassword) {
                        Toast.makeText(context, "Passwords must be equals", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    } else {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Passwords updated!", Toast.LENGTH_LONG).show()
                                    } else Toast.makeText(context, "Error while updating password! Try to logout and login again", Toast.LENGTH_SHORT).show()
                                }
                    }
                    setUserPassword.onEditorAction(EditorInfo.IME_ACTION_DONE)
                })
                .setNegativeButton("Cancel", null)
                .show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InfoProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}