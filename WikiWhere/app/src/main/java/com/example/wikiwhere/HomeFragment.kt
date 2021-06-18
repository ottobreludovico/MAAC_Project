package com.example.wikiwhere

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
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
        val viewR = inflater.inflate(R.layout.fragment_home, container, false)

        val loc = Location("dummyprovider")
        loc.setLatitude(41.89);
        loc.setLongitude(12.49);
        val prova = Array<String>(20, { i -> "" })
        val images: ArrayList<String> = arrayListOf()
        var resultWeb = false;
        val client = OkHttpClient()
        val listItems: ArrayList<String> = arrayListOf()
        val request = Request.Builder()
            .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${loc.latitude},${loc.longitude}&radius=5000&type=museum&key=AIzaSyBc-5WXDKKZhmwDkkQTBTc6Knsl9-k70OM")
            .build()

        val countDownLatch = CountDownLatch(1)
// Coroutines not supported directly, use the basic Callback way:
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    if (response.code == 200) {
                        resultWeb = true;

                        val obj = JSONObject(response.body!!.string())
                        val jsonArray = obj.getJSONArray("results")

                        for (i in 0 until jsonArray.length()){
                            val jsonObject = jsonArray.getJSONObject(i)
                            listItems.add(jsonObject.getString("name"))
                            prova[i]=jsonObject.getString("name")
                            val im = jsonObject.getJSONArray("photos")
                            val ob = im.getJSONObject(0)
                            images.add(ob.getString("photo_reference"))
                        }
                        countDownLatch.countDown();
                    }

                    println(listItems)
                    //println(response.body!!.string())
                }
            }
        })
        countDownLatch.await();
        //val lAdapter = ListAdapter(viewR.context, listItems, listItems, null, R.drawable.ic_launcher_foreground)
        //val lAdapter = ArrayAdapter<String>(viewR.context, android.R.layout.simple_list_item_1, prova)
        val lAdapter = CustomListAdapter(viewR.context, listItems, images)

        viewR.placeList.adapter = lAdapter
        return viewR
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