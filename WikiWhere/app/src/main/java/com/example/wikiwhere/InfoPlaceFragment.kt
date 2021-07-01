package com.example.wikiwhere

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.createupdate3.view.*
import kotlinx.android.synthetic.main.fragment_info_place.*
import kotlinx.android.synthetic.main.fragment_info_place.view.*
import org.json.JSONArray
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoPlaceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoPlaceFragment : Fragment() , OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var loc : LatLng
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var mMap: MapView? = null

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
        // Inflate the layout for this fragment
        var v:View

        v = inflater.inflate(R.layout.fragment_info_place, container, false)



        mMap = v.findViewById(R.id.mapView) as MapView
        mMap?.onCreate(savedInstanceState)
        mMap?.getMapAsync(this)

        val bundle = this.arguments
        val s = bundle!!.getString("place")
        lat = bundle!!.getDouble("lat")
        lon = bundle!!.getDouble("lon")
        var rate = bundle!!.getDouble("rate")
        var ima = bundle!!.getString("image")
        var des = bundle!!.getString("description")

        loc= LatLng(lat,lon)
        v.placeTitle.setText(s)
        v.placeDescription.setText(des)

        var repository: SavedPlaceRepository= SavedPlaceRepository(v.context,PlaceAdapter(v.context))
        var repository2 : ItineraryRepository = ItineraryRepository(v.context,ItineraryAdapter(v.context))



        v.button2.setOnClickListener {
            repository.Create(
                title=v.placeTitle.text.toString(),
                rate=rate,
                description=v.placeDescription.text.toString(),
                lat = lat,
                lon = lon,
                ima = ima!!,
                note="")
        }

        v.button3.setOnClickListener {
            var iti = ""
            val builder = AlertDialog.Builder(v.context)
            val dialogView = LayoutInflater.from(v.context).inflate(R.layout.createupdate3,null)

            builder.setTitle("Select an Itinerary")
            builder.setView(dialogView)
            val dropdown: Spinner = dialogView.spinner3
//create a list of items for the spinner.
//create a list of items for the spinner.
            var ret = mutableListOf<String>()
            val stringRequest = JsonArrayRequest(
                    Request.Method.GET, "http://10.0.2.2:5000/itineraries/", null,
                    { r ->
                        val reply = JSONArray(r.toString())

                        var movieJSON: JSONObject

                        for (i in 0..reply.length()-1) {
                            movieJSON = JSONObject(reply[i].toString())
                            ret.add(movieJSON.getString("name"))
                        }
                        var ret1=ret.toTypedArray()
                        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                iti = dropdown.getItemAtPosition(position).toString()
                            }

                        }
                        val adapter: ArrayAdapter<String> = ArrayAdapter(v.context, android.R.layout.simple_spinner_dropdown_item, ret1)
//set the spinners adapter to the previously created one.
//set the spinners adapter to the previously created one.
                        dropdown.adapter = adapter
                    },
                    { error: VolleyError? ->
                        Log.e(
                                "ROR",
                                "/get request ERROR!: $error"
                        )
                    })
            // Add the request to the RequestQueue.
            Log.i("ROR", "...sending a request")
            var queue = Volley.newRequestQueue(v.context)
            queue.add(stringRequest)
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.

            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
                repository2.addIti(
                        ids=iti,
                        title=v.placeTitle.text.toString(),
                        rate=rate,
                        description=v.placeTitle.text.toString(),
                        lat = lat,
                        lon = lon,
                        ima = ima!!)
            })
            builder.create().show()

        }

        return v
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mMap?.onSaveInstanceState(outState)
    }


    override fun onResume() {
        super.onResume()
        mMap?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMap?.onPause()
    }

    override fun onStart() {
        super.onStart()
        mMap?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMap?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMap?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMap?.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val zoomLevel = 16.0f //This goes up to 21
        googleMap.addMarker(MarkerOptions().position(LatLng(lat,lon)).title(lat.toString()+","+lon.toString()))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat,lon), zoomLevel))

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InfoPlaceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoPlaceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}