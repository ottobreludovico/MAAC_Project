package com.example.wikiwhere

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso
import java.net.URL
import java.util.ArrayList

class CustomListAdapter(private var c: Context, private var titles: ArrayList<String>, private var images: ArrayList<String>) : BaseAdapter() {

    override fun getCount(): Int   {  return titles.size  }
    override fun getItem(i: Int): Any {  return titles[i] }
    fun getImage(i: Int): Any {  return images[i] }
    override fun getItemId(i: Int): Long { return i.toLong()}

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        if (view == null) {
            //inflate layout resource if its null
            view = LayoutInflater.from(c).inflate(R.layout.single_list_item, viewGroup, false)
        }

        //get current quote
        val quote = this.getItem(i) as String
        val quote2 = this.getImage(i) as String

        //reference textviews and imageviews from our layout
        val img = view!!.findViewById<ImageView>(R.id.icon) as ImageView
        val nameTxt = view!!.findViewById<TextView>(R.id.title) as TextView
        val propTxt = view!!.findViewById<TextView>(R.id.description) as TextView

        //BIND data to TextView and ImageVoew
        nameTxt.text = quote
        propTxt.text = quote
        val s : String = "https://maps.googleapis.com/maps/api/place/photo?maxheight=100&maxwidth=100&key=AIzaSyBc-5WXDKKZhmwDkkQTBTc6Knsl9-k70OM&photoreference="+quote2
        Picasso.get()
            .load(s)
            .resize(100,100).into(img);


        //handle itemclicks for the ListView
        view.setOnClickListener { Toast.makeText(c, quote, Toast.LENGTH_SHORT).show() }

        return view
    }
}