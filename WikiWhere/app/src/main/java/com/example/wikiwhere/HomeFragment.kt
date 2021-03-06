package com.example.wikiwhere

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var images: ArrayList<String> = arrayListOf()
private var lats: ArrayList<Double> = arrayListOf()
private var longs: ArrayList<Double> = arrayListOf()
private var listItems: ArrayList<String> = arrayListOf()
private var listRatings: ArrayList<Float> = arrayListOf()
private var listVicinity: ArrayList<String> = arrayListOf()

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var listView : ListView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchText : String
    private var filterText = "&type=bar"
    private var l1 : Double = 41.54
    private var l2 : Double = 12.48
    private var condizione : Int = 0
    private var condizione2 : Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    fun getLocationFromAddress(context: Context?, ress: String): LatLng? {
        val coder = Geocoder(context)
        var p1: LatLng? = null
        try {
            // May throw an IOException
            val address = coder.getFromLocationName(ress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)
            return p1
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewR = inflater.inflate(R.layout.fragment_home, container, false)

        //get the spinner from the xml.
       viewR.imageButton.setOnClickListener {
            /*val list = geocoder.getFromLocationName(searchText, 1)
            if(list.size > 0) {
                l1 = list.get(0).getLatitude();
                println(l1)
                l2 = list.get(0).getLongitude();
                println(l2)
                val countDownLatch = CountDownLatch(1)
                call(l1,l2,countDownLatch)
                countDownLatch.await();
                val lAdapter = CustomListAdapter(viewR.context, listItems, images, listRatings, lats, longs)

                viewR.placeList.adapter = lAdapter
            }else{
                val countDownLatch = CountDownLatch(1)
                call(l1,l2,countDownLatch)
                countDownLatch.await();
                val lAdapter = CustomListAdapter(viewR.context, listItems, images, listRatings, lats, longs)

                viewR.placeList.adapter = lAdapter
            }*/

               searchText=viewR.searchT.text.toString()
               val c = getLocationFromAddress(context,searchText)
               if(c!=null){
                   val countDownLatch = CountDownLatch(1)
                   l1=c!!.latitude
                   l2=c!!.longitude
                   call(l1,l2,countDownLatch)
                   countDownLatch.await();
                   val lAdapter = CustomListAdapter(0,viewR.context, listItems, images, listRatings, lats, longs, listVicinity)

                   viewR.placeList.adapter = lAdapter
               }





        }

        viewR.imageButton2.setOnClickListener {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(viewR.context)

            if (ActivityCompat.checkSelfPermission(
                            viewR.context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            viewR.context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.


            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                if(location!=null){
                    println("aoooooooo")
                    l1=location.latitude
                    l2=location.longitude

                    val countDownLatch = CountDownLatch(1)
                    call(l1,l2,countDownLatch)
                    countDownLatch.await();
                    val lAdapter = CustomListAdapter(0,viewR.context, listItems, images, listRatings, lats, longs, listVicinity)

                    viewR.placeList.adapter = lAdapter
                }else{

                    val countDownLatch = CountDownLatch(1)
                    call(l1,l2,countDownLatch)
                    countDownLatch.await();
                    val lAdapter = CustomListAdapter(0,viewR.context, listItems, images, listRatings, lats, longs, listVicinity)

                    viewR.placeList.adapter = lAdapter
                }

            }




        }



        //get the spinner from the xml.
       /* val dropdown: Spinner = viewR.spinner2
//create a list of items for the spinner.
//create a list of items for the spinner.
        val items = arrayOf("accounting",
                "airport",
                "amusement_park",
                "aquarium",
                "art_gallery",
                "atm",
                "bakery",
                "bank",
                "bar",
                "beauty_salon",
                "bicycle_store",
                "book_store",
                "bowling_alley",
                "bus_station",
                "cafe",
                "campground",
                "car_dealer",
                "car_rental",
                "car_repair",
                "car_wash",
                "casino",
                "cemetery",
                "church",
                "city_hall",
                "clothing_store",
                "convenience_store",
                "courthouse",
                "dentist",
                "department_store",
                "doctor",
                "drugstore",
                "electrician",
                "electronics_store",
                "embassy",
                "fire_station",
                "florist",
                "funeral_home",
                "furniture_store",
                "gas_station",
                "gym",
                "hair_care",
                "hardware_store",
                "hindu_temple",
                "home_goods_store",
                "hospital",
                "insurance_agency",
                "jewelry_store",
                "laundry",
                "lawyer",
                "library",
                "light_rail_station",
                "liquor_store",
                "local_government_office",
                "locksmith",
                "lodging",
                "meal_delivery",
                "meal_takeaway",
                "mosque",
                "movie_rental",
                "movie_theater",
                "moving_company",
                "museum",
                "night_club",
                "painter",
                "park",
                "parking",
                "pet_store",
                "pharmacy",
                "physiotherapist",
                "plumber",
                "police",
                "post_office",
                "primary_school",
                "real_estate_agency",
                "restaurant",
                "roofing_contractor",
                "rv_park",
                "school",
                "secondary_school",
                "shoe_store",
                "shopping_mall",
                "spa",
                "stadium",
                "storage",
                "store",
                "subway_station",
                "supermarket",
                "synagogue",
                "taxi_stand",
                "tourist_attraction",
                "train_station",
                "transit_station",
                "travel_agency",
                "university",
                "veterinary_care",
                "zoo")
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(condizione==1) {
                    filterText="&type="+dropdown.getItemAtPosition(position)
                    val countDownLatch = CountDownLatch(1)
                    call(l1,l2,countDownLatch)
                    countDownLatch.await();
                    val lAdapter = CustomListAdapter(viewR.context, listItems, images, listRatings, lats, longs)
                    viewR.placeList.adapter = lAdapter
                }
                condizione=1
            }

        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(viewR.context, android.R.layout.simple_spinner_dropdown_item, items)
//set the spinners adapter to the previously created one.
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter)*/




        val lAdapter = CustomListAdapter(0, viewR.context, listItems, images, listRatings, lats, longs, listVicinity)
        viewR.placeList.adapter = lAdapter
        return viewR
    }



    private fun call(lat: Double, lon: Double,c:CountDownLatch){
        images = arrayListOf<String>()
        lats = arrayListOf<Double>()
        longs = arrayListOf<Double>()
        listItems = arrayListOf<String>()
        listRatings= arrayListOf<Float>()
        listVicinity= arrayListOf()
        val client = OkHttpClient()

        val request = Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lon+"&radius=5000&key=AIzaSyBc-5WXDKKZhmwDkkQTBTc6Knsl9-k70OM"+filterText)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    if (response.code == 200) {
                        val obj = JSONObject(response.body!!.string())
                        val jsonArray = obj.getJSONArray("results")

                        for (i in 0 until jsonArray.length()){
                            val jsonObject = jsonArray.getJSONObject(i)
                            listItems.add(jsonObject.getString("name"))
                            listVicinity.add(jsonObject.getString("vicinity"))
                            if(jsonObject.has("rating")){
                                listRatings.add((jsonObject.getDouble("rating")).toFloat())
                            }else{
                                listRatings.add(0.0f)
                            }
                            if(jsonObject.has("photos")){
                                val im = jsonObject.getJSONArray("photos")
                                val ob = im.getJSONObject(0)
                                images.add(ob.getString("photo_reference"))
                            }else{
                                images.add("")
                            }
                            val a=jsonObject.getJSONObject("geometry")
                            val loc=a.getJSONObject("location")
                            lats.add((loc.getDouble("lat")))
                            longs.add((loc.getDouble("lng")))
                        }
                        c.countDown();
                    }

                    //println(response.body!!.string())
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }





}