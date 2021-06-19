package com.example.wikiwhere

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import java.util.*


class CustomListAdapter(private var c: Context, private var titles: ArrayList<String>, private var images: ArrayList<String>, private var ratings: ArrayList<Float>, private var lats: ArrayList<Double>, private var longs: ArrayList<Double>) : BaseAdapter() {

    override fun getCount(): Int   {  return titles.size  }
    override fun getItem(i: Int): Any {  return titles[i] }
    fun getImage(i: Int): Any {  return images[i] }
    fun getLat(i: Int): Double {  return lats[i] }
    fun getLong(i: Int): Double {  return longs[i] }
    fun getRating(i: Int): Any {  return ratings[i] }
    override fun getItemId(i: Int): Long { return i.toLong()}

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        if (view == null) {
            //inflate layout resource if its null
            view = LayoutInflater.from(c).inflate(R.layout.custom_single_list_item, viewGroup, false)
        }
        val mWinMgr = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        val w1 = mWinMgr.currentWindowMetrics.bounds.width()

        var width = displayMetrics.widthPixels

        //get current quote
        val quote = this.getItem(i) as String
        val quote2 = this.getImage(i) as String
        val quote3 = this.getRating(i) as Float
        val quote4 = this.getLat(i)
        val quote5 = this.getLong(i)


        //reference textviews and imageviews from our layout
        val img = view!!.findViewById<ImageView>(R.id.icon) as ImageView
        val nameTxt = view!!.findViewById<TextView>(R.id.title) as TextView
        val propTxt = view!!.findViewById<TextView>(R.id.description) as TextView
        val rtnBar = view!!.findViewById<TextView>(R.id.ratingBar) as RatingBar

        //BIND data to TextView and ImageVoew
        nameTxt.text = quote
        propTxt.text = quote
        rtnBar.rating = quote3
        if(quote2!=""){
            val s : String = "https://maps.googleapis.com/maps/api/place/photo?maxheight=700&maxwidth="+w1+"&key=AIzaSyBc-5WXDKKZhmwDkkQTBTc6Knsl9-k70OM&photoreference="+quote2
            /* Picasso.get()
                 .load(s)
                 .resize(100,100).into(img);*/
            Picasso.get()
                    .load(s)
                    .resize(w1,700).into(img);
        }else{
            Picasso.get().load("https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png").resize(w1,700).into(img);
        }




        //handle itemclicks for the ListView
       view.setOnClickListener {
           val bundle = Bundle()
           bundle.putString("place", quote) // Put anything what you want
           bundle.putDouble("lat", quote4)
           bundle.putDouble("lon", quote5)
           val fragment2 = InfoPlaceFragment()
           fragment2.setArguments(bundle)
           (c as FragmentActivity).supportFragmentManager.beginTransaction()
               .replace(R.id.fragmentContainer, fragment2).addToBackStack(null).commit()
       }


        return view
    }
}