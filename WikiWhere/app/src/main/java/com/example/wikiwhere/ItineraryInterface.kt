package com.example.wikiwhere
import android.net.Uri
import java.util.*

interface ItineraryInterface {
    fun CreateI(name: String, date: String, image: String): Boolean
    fun ReadAllI() : Boolean
    fun ReadI(position: Int) : Itinerary
    fun UpdateI(position :Int, name: String, date: String): Boolean
    fun DeleteI(position: Int) : Boolean
    fun addIti(ids: String, title : String, rate: Double, description : String, lat : Double, lon :Double, ima : String) : Boolean
    fun getItemCountI(): Int
    fun addI(id: Int, title: String, description : String, image : String) : Boolean

}