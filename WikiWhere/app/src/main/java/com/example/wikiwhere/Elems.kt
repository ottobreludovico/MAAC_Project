package com.example.wikiwhere

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_elems.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.savedplacelist.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.CountDownLatch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Elems.newInstance] factory method to
 * create an instance of this fragment.
 */
class Elems : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var images: ArrayList<String> = arrayListOf()
    var lats: ArrayList<Double> = arrayListOf()
    var longs: ArrayList<Double> = arrayListOf()
    var listItems: ArrayList<String> = arrayListOf()
    var listRatings: ArrayList<Float> = arrayListOf()
    var vicinity: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun call(url: String, c: CountDownLatch){
        images= arrayListOf()
        lats = arrayListOf()
        longs = arrayListOf()
        listItems= arrayListOf()
        listRatings = arrayListOf()
        vicinity = arrayListOf()

        val client = OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    if (response.code == 200) {
                        val obj = JSONArray(response.body!!.string())

                        for (i in 0 until obj.length()){
                            val jsonObject = obj.getJSONObject(i)
                            listItems.add(jsonObject.getString("title"))
                            listRatings.add((jsonObject.getDouble("rate")).toFloat())
                            images.add(jsonObject.getString("ima"))
                            lats.add((jsonObject.getDouble("lat")))
                            longs.add((jsonObject.getDouble("lon")))
                            vicinity.add(jsonObject.getString("description"))
                        }
                        c.countDown();
                    }
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_elems, container, false)
        // Inflate the layout for this fragment

        //prendere url da bundle
        val bundle = this.arguments
        val id : String = bundle!!.getString("id")!!

        val countDownLatch = CountDownLatch(1)
        val url ="http://10.0.2.2:5000/itineraries/"+id
        call(url, countDownLatch)
        countDownLatch.await();

        val lAdapter = CustomListAdapter(1,v.context, listItems, images, listRatings, lats, longs, vicinity)

        v.list3.adapter = lAdapter

        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Elems.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                Elems().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}