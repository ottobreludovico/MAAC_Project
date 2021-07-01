package com.example.wikiwhere

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class SavedPlaceRepository (c: Context?, adapter: PlaceAdapter) : SavedPlaceInterface {

    private val queue = Volley.newRequestQueue(c)
    // private var url = "https://shrouded-shore-31030.herokuapp.com/movies"

    //  private var url = "https://pure-beyond-97319.herokuapp.com/movies"

    private var url = "http://10.0.2.2:5000/"

    private var adp = adapter

    private var data: MutableMap<Int, SavedPlace> = mutableMapOf()

    //curl -H "Content-Type: application/json" -X POST  -d '{"movie": {"title":"Matrix"}}' https://shrouded-shore-31030.herokuapp.com/movies
    override fun Create(title :String, rate: Double, description:String, lat: Double, lon: Double, ima : String, note: String): Boolean {

        var req = HashMap<String,String>()
        req["title"]=title
        req["description"]=description
        req["rate"]=rate.toString()
        req["lat"]=lat.toString()
        req["lon"]=lon.toString()
        req["ima"]=ima.toString()
        req["note"]=note.toString()

        var movie = SavedPlace()

        movie.tit=title
        movie.description=description
        movie.rate=rate
        movie.lat=lat
        movie.lon=lon
        movie.ima=ima
        movie.note=note

        //Log.i("ROR.3",req["year"])
        Log.i("info", "Create: "+JSONObject(req.toMap()))

        val stringRequest = JsonObjectRequest(
                Request.Method.POST, url, JSONObject(req.toMap()),
                { r ->
                    val key = JSONObject(r.toString()).getInt("id")
                    movie.placeId = key
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

    //curl -H "Content-Type: application/json" -X GET  https://shrouded-shore-31030.herokuapp.com/movies/movieID
    override fun Read(position: Int): SavedPlace {

        if ((data.isEmpty()) or (data.size < position)) {
            val movie = SavedPlace()
            movie.tit = "Error: can't find the movie.. "
        }

        val movieID = data.keys.sorted().get(position)
        Log.i("ROR","Getting movieID "+movieID.toString()+" "+data.get(movieID).toString())
        return data.get(movieID)!!
    }


    //curl -H "Content-Type: application/json" -X GET  https://shrouded-shore-31030.herokuapp.com/movies/
    override fun ReadAll(): Boolean {

        Log.i("ROR","Read all called....")
        if (!data.isEmpty())
            return true

        Log.i("ROR","FETCHING DATA FROM WEB-API....")
        val stringRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                { r ->
                    val reply = JSONArray(r.toString())

                    var movieJSON: JSONObject
                    var movie: SavedPlace
                    for (i in 0..reply.length()-1) {
                        movieJSON = JSONObject(reply[i].toString())
                        movie = SavedPlace()
                        movie.placeId = movieJSON.getInt("id")
                        movie.tit = movieJSON.getString("title")
                        movie.description = movieJSON.getString("description")
                        movie.ima = movieJSON.getString("ima")
                        movie.note = movieJSON.getString("note")
                        data.put(movie.placeId, movie)

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




    override fun Update(position :Int, note:String): Boolean {

        var req = HashMap<String,String>()
        req["note"]=note


        val key = data.keys.sorted().get(position)

        Log.i("ROR.2",key.toString())
        //val key = position

        val movie = data.get(key)


        movie!!.note=note
        data.set(key,movie)



        val stringRequest = JsonObjectRequest(
                Request.Method.PUT, url+"/"+key.toString(), JSONObject(req.toMap()),
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

    override fun Delete(position: Int): Boolean {
        data.remove(position)
        adp.notifyDataSetChanged()
        val stringRequest = JsonObjectRequest(
                Request.Method.DELETE, url+"/"+position.toString(), null,
                { r ->
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

    override fun getItemCount(): Int {
        return data.size
    }
}