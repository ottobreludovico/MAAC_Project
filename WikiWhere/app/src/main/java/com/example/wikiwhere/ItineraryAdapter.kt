package com.example.wikiwhere

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.createupdate2.view.*
import kotlinx.android.synthetic.main.createupdate2.view.edit_title
import kotlinx.android.synthetic.main.createupdate5.view.*
import kotlinx.android.synthetic.main.singleitinerary.view.*
import kotlinx.android.synthetic.main.singleitinerary.view.position
import kotlinx.android.synthetic.main.singlesavedplace.view.*
import java.util.*


class ItineraryAdapter (c: Context?) : RecyclerView.Adapter<ItineraryAdapter.ViewHolder> (), View.OnClickListener, View.OnLongClickListener
{

    private var repository: ItineraryRepository= ItineraryRepository(c,this)
    private var c :Context? = c
    private var mC :Context? = null
    private val pickImage = 100
    private val imgList : Array<String> = arrayOf<String>("https://img.fotocommunity.com/paesaggio-7f9899b9-24cf-4243-84e8-a37f286f99bd.jpg?height=1080",
            "https://www.giuliophoto.it/easyUp/image/zoom/10_il_sentiero_nel_bosco_vz8c_z.jpg",
            "https://www.alpmovie.it/images/2018/Gallery/AlpMovie2018-Paesaggi_05.webp",
            "https://www.enpam.it/wp-content/uploads/val-dorcia1-2-800.png",
            "https://peopletour.it/wp-content/uploads/2019/10/copertina-768x511.png",
            "https://www.domeggedicadore.info/wp-content/uploads/2020/01/I-paesaggi-delle-Dolomiti-le-cime-più-belle-e1578568806338-1.jpg",
            "https://www.zingarate.com/pictures/2020/12/15/odle.jpeg",
            "https://www.reterurale.it/flex/images/9/2/3/D.351363a0300d2b7790bd/1.jpg")

    init {
        Log.i("ROR","init called"+repository.toString())
        repository.ReadAllI()
    }

    override fun onLongClick(v: View): Boolean {
        repository.DeleteI(v.idI.text.toString().toInt())
        return true
    }


    override fun onClick(it: View) {

        if (it.id==R.id.fab2) {
            val builder = AlertDialog.Builder(c!!)
            val dialogView = LayoutInflater.from(c!!).inflate(R.layout.createupdate2,null)
            builder.setTitle("Create a new Itinerary ")
            builder.setView(dialogView)
            dialogView.edit_title.hint="Itinerary name"

            val day = 1
            val month = 7
            val year = 2021
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = day

            val milliTime = calendar.timeInMillis

            dialogView.calendarView2.setDate (milliTime, true, true);

            var a = ""
            dialogView.calendarView2.setOnDateChangeListener(OnDateChangeListener { view, year, month, day ->
                //show the selected date as a toast
                a ="$day/$month/$year"
            })
            builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
                repository.CreateI(
                        name=dialogView.edit_title.text.toString(),
                        date=a,
                        image=randomImg())
            }
            )
            builder.setNegativeButton("Cancel", null);
            builder.create().show()
        }
        else {
            val bundle = Bundle()
            bundle.putString("url", "http://10.0.2.2:5000/itineraries/"+it.name.text.toString())
            val fragment2 = Elems()
            fragment2.setArguments(bundle)
            (c as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment2).addToBackStack(null).commit()
        }


    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.singleitinerary, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        // return 10
        return repository.getItemCountI()
    }

    fun randomImg() : String {
        val x = kotlin.random.Random.nextInt(imgList.size)
        println(x)
        println(imgList[x])
        return imgList[x]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie : Itinerary = repository.ReadI(position)

        val mWinMgr = c!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val w1 = mWinMgr.currentWindowMetrics.bounds.width()
        if(movie.image != ""){
            Picasso.get()
                    .load(movie.image)
                    .resize(w1,700).into(holder.itemView.imageView);
        }
        holder.itemView.name.text=movie.name
        //holder.itemView.date.text=movie.date
        val date = movie.date
        holder.itemView.date.setText(date)
        holder.itemView.idI.text=movie.id.toString()
        holder.itemView.position.text=position.toString()
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", movie.id.toString())
            val fragment2 = Elems()
            fragment2.setArguments(bundle)
            (c as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment2).addToBackStack(null).commit()
        }
        holder.itemView.delete.setOnClickListener{repository.DeleteI(movie.id)}
        holder.itemView.modify.setOnClickListener{
            val builder = AlertDialog.Builder(c!!)
            val dialogView = LayoutInflater.from(c!!).inflate(R.layout.createupdate5,null)
            builder.setTitle("Update "+holder.itemView.name.text)
            builder.setView(dialogView)
            dialogView.edit_title.text.insert(0,holder.itemView.name.text)
            val parts: List<String> = date.split("/")

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = day

            val milliTime = calendar.timeInMillis

            dialogView.calendarView.setDate (milliTime, true, true);

            var a = ""
            dialogView.calendarView.setOnDateChangeListener(OnDateChangeListener { view, year, month, day ->
                //show the selected date as a toast
                a ="$day/$month/$year"
            })


            builder.setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->
                println(a)
                repository.UpdateI(
                        position=position,
                        name=dialogView.edit_title.text.toString(),
                        date=a)
            }
            )
            builder.setNegativeButton("Cancel", null);
            builder.create().show()
        }

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



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}







//holder.itemView.setOnClickListener { Toast.makeText(c,"ok",Toast.LENGTH_SHORT).show() }