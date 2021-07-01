package com.example.wikiwhere

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_m_l.*
import kotlinx.android.synthetic.main.activity_m_l.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MLFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MLFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var img: String
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String
    private lateinit var c: Context
    private var activity: Activity? = null
    val REQUEST_TAKE_PHOTO = 1
    private lateinit var vicinity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var photoFile : File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity=this.getActivity()

        /*var v:View
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
            v = inflater.inflate(R.layout.activity_m_l_land, container, false)
        }else {
            v = inflater.inflate(R.layout.activity_m_l, container, false)
        }*/
        var v = inflater.inflate(R.layout.activity_m_l, container, false)
        c=v.context
        v.buttonLoadPicture.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        v.button5.setOnClickListener {
            /*val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }*/
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val frag: Fragment = this
            /** Pass your fragment reference **/
            /** Pass your fragment reference  */
            frag.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE) // REQUEST_IMAGE_CAPTURE = 12345

        }


        v.button4.setOnClickListener { ml(imageUri!!,v.context) }

        return v
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            val packageManager: PackageManager? = activity?.packageManager
            takePictureIntent.resolveActivity(packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            c,
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK && requestCode === REQUEST_IMAGE_CAPTURE) {
                // Do something with imagePath
                val photo = data!!.extras!!["data"] as Bitmap?
                pickIma.setImageBitmap(photo)
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                /*var selectedImage: Uri? = getImageUri(c, photo!!)
                val realPath: String? = getRealPathFromURI(selectedImage)
                selectedImage = Uri.parse(realPath)
                imageUri = selectedImage*/
                imageUri = bitmapToFile(photo)
        }
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            pickIma.setImageURI(imageUri)
        }
    }

    private fun bitmapToFile(bitmap:Bitmap?): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(c)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return file.toUri()
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = requireActivity()!!.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    fun ml(uri: Uri, c: Context){
        val options = FirebaseVisionCloudDetectorOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
            .setMaxResults(15)
            .build()

        val image: FirebaseVisionImage?
        try {
            println(uri)
            image = FirebaseVisionImage.fromFilePath(c, uri);
            val detector = FirebaseVision.getInstance()
                .getVisionCloudLandmarkDetector(options)

            detector.detectInImage(image)
                .addOnSuccessListener {
                    // Task succeeded!
                    if (it.size == 0){
                        Toast.makeText(c,"Place not found :(" , Toast.LENGTH_SHORT).show();
                    }else{
                        var j=0
                        for (landmark in it) {
                            // Do something with landmark
                            if(j==0){
                                val landmarkName = landmark.landmark
                                println(landmarkName)
                                val lat = landmark.locations[0].latitude
                                val lon = landmark.locations[0].longitude
                                val countDownLatch = CountDownLatch(1)
                                val s2 = "${landmarkName.replace(" ", "%20")}"
                                call2(s2,countDownLatch)
                                countDownLatch.await();
                                val bundle = Bundle()
                                bundle.putString("place", landmarkName) // Put anything what you want
                                bundle.putString("description", vicinity)
                                bundle.putDouble("rate", 3.5)
                                bundle.putDouble("lat", lat)
                                bundle.putDouble("lon", lon)
                                bundle.putInt("c",0)
                                bundle.putString("image", img)
                                val fragment2 = InfoPlaceFragment()
                                fragment2.setArguments(bundle)
                                (c as FragmentActivity).supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragmentContainer, fragment2).addToBackStack(null).commit()
                                j++
                            }

                        }
                    }

                }
                .addOnFailureListener {
                    // Task failed with an exception
                    Toast.makeText(c,"Error !" , Toast.LENGTH_SHORT).show();
                }

        }catch (e: IOException) {
            e.printStackTrace();
        }

    }

    private fun call2(str : String,c: CountDownLatch){
        val client = OkHttpClient()

        val request2 = Request.Builder()
            .url("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="+str+"&inputtype=textquery&fields=photos,rating,formatted_address&key=AIzaSyBc-5WXDKKZhmwDkkQTBTc6Knsl9-k70OM")
            .build()

        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    if (response.code == 200) {
                        println(response)
                        val obj = JSONObject(response.body!!.string())
                        val aaa = obj.getJSONArray("candidates")
                        if(aaa.length()>=1){
                            val aa = aaa.getJSONObject(0)
                            vicinity=aa.getString("formatted_address")
                            if(aa.has("photos")){
                                val im = aa.getJSONArray("photos")
                                val ob = im.getJSONObject(0)
                                img = "https://maps.googleapis.com/maps/api/place/photo?maxheight=700&maxwidth=200&key=AIzaSyBc-5WXDKKZhmwDkkQTBTc6Knsl9-k70OM&photoreference=" + ob.getString("photo_reference")
                                c.countDown();
                            }else{
                                img = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png"
                                c.countDown();
                            }
                        }else{

                        }
                    }
                }
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MLFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MLFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}