package com.example.wikiwhere

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class ItineraryRepository (c: Context?, adapter: ItineraryAdapter) : ItineraryInterface {

    private val queue = Volley.newRequestQueue(c)
    // private var url = "https://shrouded-shore-31030.herokuapp.com/movies"

    //  private var url = "https://pure-beyond-97319.herokuapp.com/movies"

    private var url = "http://10.0.2.2:5000/itineraries/"

    private var adp = adapter

    private var data: MutableMap<Int, Itinerary> = mutableMapOf()

    //curl -H "Content-Type: application/json" -X POST  -d '{"movie": {"title":"Matrix"}}' https://shrouded-shore-31030.herokuapp.com/movies
    override fun CreateI(name: String, date: String, image: String): Boolean {

        var req = HashMap<String,String>()
        req["name"]=name
        req["date"]=date.toString()
        req["image"]=image.toString()


        var movie = Itinerary()

        movie.name=name
        movie.date=date
        movie.image=image


        //Log.i("ROR.3",req["year"])
        Log.i("info", "Create: "+ JSONObject(req.toMap()))

        val stringRequest = JsonObjectRequest(
                Request.Method.POST, url, JSONObject(req.toMap()),
                { r ->
                    val key = JSONObject(r.toString()).getInt("id")
                    movie.id = key
                    data.set(key, movie)
                    adp.notifyDataSetChanged()
                },
                { error: VolleyError? ->
                    Log.e(
                            "ROR",
                            "/get request ERROR!: $error"
                    )
                })
        queue.add(stringRequest)
        return true
    }

    override fun addI(id: Int, title: String, description : String, image : String) : Boolean{
        var req = HashMap<String,String>()
        req["title"]=title
        req["description"]=description
        req["image"]=image


        //Log.i("ROR.3",req["year"])
        Log.i("info", "Create: "+ JSONObject(req.toMap()))

        val stringRequest = JsonObjectRequest(
            Request.Method.POST, url+id+"/", JSONObject(req.toMap()),
            { r ->
                val key = JSONObject(r.toString()).getInt("id")
            },
            { error: VolleyError? ->
                Log.e(
                    "ROR",
                    "/get request ERROR!: $error"
                )
            })
        queue.add(stringRequest)
        return true
    }

    //curl -H "Content-Type: application/json" -X GET  https://shrouded-shore-31030.herokuapp.com/movies/movieID
    override fun ReadI(position: Int): Itinerary {

        if ((data.isEmpty()) or (data.size < position)) {
            val movie = Itinerary()
            movie.name = "Error: can't find the movie.. "
        }

        val movieID = data.keys.sorted().get(position)
        Log.i("ROR","Getting movieID "+movieID.toString()+" "+data.get(movieID).toString())
        return data.get(movieID)!!
    }


    //curl -H "Content-Type: application/json" -X GET  https://shrouded-shore-31030.herokuapp.com/movies/
    override fun ReadAllI(): Boolean {

        Log.i("ROR","Read all called....")
        if (!data.isEmpty())
            return true

        Log.i("ROR","FETCHING DATA FROM WEB-API....")
        val stringRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                { r ->
                    val reply = JSONArray(r.toString())

                    var movieJSON: JSONObject
                    var movie: Itinerary
                    for (i in 0..reply.length()-1) {
                        movieJSON = JSONObject(reply[i].toString())
                        movie = Itinerary()
                        movie.id = movieJSON.getInt("id")
                        movie.name = movieJSON.getString("name")
                        movie.date = movieJSON.getString("date")
                        movie.image = movieJSON.getString("image")
                        data.put(movie.id, movie)

                    }
                    adp.notifyDataSetChanged()
                    Log.i("ROR", data.toString())
                },
                { error: VolleyError? ->
                    Log.e(
                            "ROR",
                            "/get request ERROR!: $error"
                    )
                })
        // Add the request to the RequestQueue.
        Log.i("ROR", "...sending a request")
        queue.add(stringRequest)
        return true
    }

    override fun UpdateI(position :Int, name: String, date: String): Boolean {

        var req = HashMap<String,String>()
        req["name"]=name
        req["date"]=date.toString()

        val key = data.keys.sorted().get(position)

        Log.i("ROR.2",key.toString())
        //val key = position

        val movie = data.get(key)

        movie!!.name=name
        movie.date=date
        data.set(key,movie)



        val stringRequest = JsonObjectRequest(
                Request.Method.PUT, url+key.toString(), JSONObject(req.toMap()),
                { r ->
                    adp.notifyItemChanged(position)
                },
                { error: VolleyError? ->
                    Log.e(
                            "ROR",
                            "/get request ERROR!: $error"
                    )
                })
        queue.add(stringRequest)

        return true
    }


    override fun DeleteI(position: Int): Boolean {
        data.remove(position)
        adp.notifyDataSetChanged()
        val stringRequest = JsonObjectRequest(
                Request.Method.DELETE, url+position.toString(), null,
                { r ->
                    adp.notifyItemChanged(position)
                },
                { error: VolleyError? ->
                    Log.e(
                            "ROR",
                            "/DELETE ERROR!: $error"
                    )
                })
        queue.add(stringRequest)

        return true
    }

    override fun getItemCountI(): Int {
        return data.size
    }

    override fun addIti(ids: String, title : String, rate: Double, description : String, lat : Double, lon :Double, ima : String) : Boolean{

        var req = HashMap<String,String>()
        req["title"]=title.toString()
        req["description"]=description
        req["rate"]=rate.toString()
        req["lat"]=lat.toString()
        req["lon"]=lon.toString()
        req["ima"]=ima.toString()

        val stringRequest = JsonObjectRequest(
                Request.Method.POST, url+ids, JSONObject(req.toMap()),
                { r ->
                },
                { error: VolleyError? ->
                    Log.e(
                            "ROR",
                            "/get request ERROR!: $error"
                    )
                })
        queue.add(stringRequest)

        return true
    }
}