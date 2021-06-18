package com.example.wikiwhere

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_info_profile.*
import kotlinx.android.synthetic.main.fragment_info_profile.view.*

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
    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private val nameProfileTextView: TextView? = null
    private var phoneNumberProfileTextView:TextView? = null
    private var emailView:TextView? = null
    private lateinit var viewOfLayout: View

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
        viewOfLayout = inflater.inflate(R.layout.fragment_info_profile, container, false)
        // Inflate the layout for this fragment
        //editNameProfileButton.setOnClickListener(View.OnClickListener { editName() })

        //editPhoneNumberProfileButton.setOnClickListener(View.OnClickListener { editPhoneNumber() })

        user=FirebaseAuth.getInstance().currentUser

        if (user == null) {
            startActivity(Intent(activity, SigninActivity::class.java))
            //activity.finish()
        }


        //nameProfileTextView.setText(user.getDisplayName())
        viewOfLayout.emailProfileTextView.setText(user?.email)

        /* databaseReference.addValueEventListener(object : ValueEventListener() {
             fun onDataChange(dataSnapshot: DataSnapshot) {
                 val userProfile: Userinformation =
                     dataSnapshot.getValue(Userinformation::class.java)
                 nameProfileTextView.setText(user.getDisplayName())
                 phoneNumberProfileTextView.setText(userProfile.getUserPhoneNumber())
                 emailProfileTextView.setText(user.getEmail())
             }

             fun onCancelled(databaseError: DatabaseError) {
                 Toast.makeText(context, databaseError.getCode(), Toast.LENGTH_SHORT).show()
             }
         })*/

        return viewOfLayout
    }

    private fun editName() {
        /*val inflater = layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.layout_custom_dialog_edit_name, null)
        val setUserName = alertLayout.findViewById<EditText>(R.id.setUserName)
        MaterialAlertDialogBuilder(context!!)
            .setTitle("Edit name")
            .setMessage("Insert the new name")
            .setView(alertLayout) // this is set the view from XML inside AlertDialog
            .setCancelable(false) // disallow cancel of AlertDialog on click of back button and outside touch
            .setPositiveButton(
                "Save"
            ) { dialog, which ->
                val newName = setUserName.text.toString()
                val profileUpdates =
                    UserProfileChangeRequest.Builder().setDisplayName(newName).build()
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            nameProfileTextView!!.text = user!!.displayName
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
            .show()*/
    }

    private fun editPhoneNumber() {
        /*val inflater = layoutInflater
        val alertLayout: View =
            inflater.inflate(R.layout.layout_custom_dialog_edit_phone_number, null)
        val setUserPhoneNumber =
            alertLayout.findViewById<EditText>(R.id.setUserPhoneNumber)
        MaterialAlertDialogBuilder(context!!)
            .setTitle("Edit phone number")
            .setMessage("Insert the new phone number")
            .setView(alertLayout) // this is set the view from XML inside AlertDialog
            .setCancelable(false) // disallow cancel of AlertDialog on click of back button and outside touch
            .setPositiveButton(
                "Save"
            ) { dialog, which ->
                val phoneNumber = setUserPhoneNumber.text.toString()
                val userinformation = Userinformation(phoneNumber)
                val user = firebaseAuth!!.currentUser
                databaseReference.child(user.uid).setValue(userinformation)
                setUserPhoneNumber.onEditorAction(EditorInfo.IME_ACTION_DONE)
            }
            .setNegativeButton("Cancel", null)
            .show()*/
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