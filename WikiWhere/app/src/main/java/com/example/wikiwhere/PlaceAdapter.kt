package com.example.wikiwhere

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.Shape
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.createupdate.view.*
import kotlinx.android.synthetic.main.createupdate3.view.*
import kotlinx.android.synthetic.main.createupdate4.view.*
import kotlinx.android.synthetic.main.custom_single_list_item.view.*
import kotlinx.android.synthetic.main.fragment_info_place.view.*
import kotlinx.android.synthetic.main.singlesavedplace.view.*
import kotlinx.android.synthetic.main.singlesavedplace.view.description
import kotlinx.android.synthetic.main.singlesavedplace.view.movieItem
import kotlinx.android.synthetic.main.singlesavedplace.view.title
import org.json.JSONArray
import org.json.JSONObject


class PlaceAdapter (c: Context?) : RecyclerView.Adapter<PlaceAdapter.ViewHolder> (), View.OnClickListener, View.OnLongClickListener
{

    private var repository: SavedPlaceRepository= SavedPlaceRepository(c,this)
    private var repository2: ItineraryRepository= ItineraryRepository(c,ItineraryAdapter(c))
    private var c :Context? = c
    init {
        Log.i("ROR","init called"+repository.toString())
        repository.ReadAll()
    }

    override fun onLongClick(v: View): Boolean {
        repository.Delete(v.movieID.text.toString().toInt())
        return true
    }


    override fun onClick(it: View) {
        val builder = AlertDialog.Builder(c!!)

        if (it.id==R.id.fab) {
            val dialogView = LayoutInflater.from(c!!).inflate(R.layout.createupdate,null)
            builder.setTitle("Add new place")
            builder.setView(dialogView)
            dialogView.edit_title.hint="Place name"
            dialogView.edit_description.hint="Description"
            builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
                repository.Create(
                    title=dialogView.edit_title.text.toString(),
                    rate=5.0,
                    description=dialogView.edit_description.text.toString(),
                    lat = 0.0,
                    lon = 0.0,
                    ima = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png",
                    note = "")
            }
            )
        }
        else {
            val dialogView = LayoutInflater.from(c!!).inflate(R.layout.createupdate4,null)
            builder.setTitle("Update " + it.title.text)
            builder.setView(dialogView)
            dialogView.edit_note.text.insert(0, it.note.text)
            val pos = it.position.text.toString().toInt()
            builder.setPositiveButton(
                "Update",
                DialogInterface.OnClickListener { dialog, which ->
                    repository.Update(
                        position = pos,
                        note=dialogView.edit_note.text.toString()
                    )

                }
            )
        }
        builder.setNegativeButton("Cancel", null);
        builder.create().show()

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.singlesavedplace, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        // return 10
        return repository.getItemCount()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie : SavedPlace= repository.Read(position)
        Picasso.get()
                .load(movie.ima)
                .resize(200,150).into(holder.itemView.videoView);
        holder.itemView.title.text=movie.tit
        holder.itemView.description.text=movie.description
        holder.itemView.note.text=movie.note
        holder.itemView.movieID.text=movie.placeId.toString()
        holder.itemView.position.text=position.toString()

        holder.itemView.movieItem.setOnClickListener(this)
        holder.itemView.movieItem.setOnLongClickListener(this)

        /*

        {
            val builder = AlertDialog.Builder(c!!)
            val dialogView = LayoutInflater.from(c!!).inflate(R.layout.movie_createupdate,null)
            builder.setTitle("Update "+it.title.text)
            builder.setView(dialogView)
            dialogView.edit_title.text.insert(0,it.title.text)
            dialogView.edit_year.text.insert(0,it.year.text)
            dialogView.edit_description.text.insert(0,it.description.text)
            builder.setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->
                repository.Update(
                    position=position,
                    title=dialogView.edit_title.text.toString(),
                    rate=5,
                    year=dialogView.edit_year.text.toString().toInt(),
                    description=dialogView.edit_description.text.toString())

                }
            )
            builder.setNegativeButton("Cancel", null);
            builder.create().show()
        }

         */
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}





}






//holder.itemView.setOnClickListener { Toast.makeText(c,"ok",Toast.LENGTH_SHORT).show() }