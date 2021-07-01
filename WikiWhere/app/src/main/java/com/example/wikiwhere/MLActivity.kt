package com.example.wikiwhere

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_m_l.*
import java.io.IOException


class MLActivity : AppCompatActivity() {
    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m_l)

        buttonLoadPicture.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        button4.setOnClickListener { ml(imageUri!!) }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            pickIma.setImageURI(imageUri)
        }
    }

    fun ml(uri: Uri){
        val options = FirebaseVisionCloudDetectorOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
            .setMaxResults(15)
            .build()

        val image: FirebaseVisionImage?
        try {
            image = FirebaseVisionImage.fromFilePath(this, uri);
            val detector = FirebaseVision.getInstance()
                .getVisionCloudLandmarkDetector(options)

            detector.detectInImage(image)
                .addOnSuccessListener {
                    // Task succeeded!
                    for (landmark in it) {
                        // Do something with landmark
                        val landmarkName = landmark.landmark
                        val lat = landmark.locations[0].latitude
                        val lon = landmark.locations[0].longitude
                        val bundle = Bundle()
                        bundle.putString("place", landmarkName) // Put anything what you want
                        bundle.putString("description", landmarkName)
                        bundle.putDouble("rate", 3.5)
                        bundle.putDouble("lat", lat)
                        bundle.putDouble("lon", lon)
                        bundle.putInt("c",0)
                        bundle.putString("image", "")
                        val fragment2 = InfoPlaceFragment()
                        fragment2.setArguments(bundle)
                        fragment2

                    }
                }
                .addOnFailureListener {
                    // Task failed with an exception

                }

        }catch (e: IOException) {
            e.printStackTrace();
        }

    }
}