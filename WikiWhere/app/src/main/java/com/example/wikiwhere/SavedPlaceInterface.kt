package com.example.wikiwhere

import android.media.Image
import java.util.*

interface SavedPlaceInterface {
    fun Create(title: String, rate: Double, description:String, lat: Double, lon: Double, ima : String, note : String): Boolean
    fun ReadAll() : Boolean
    fun Read(position: Int) : SavedPlace
    fun Update(placeid: Int, note: String): Boolean
    fun Delete(position: Int) : Boolean
    fun getItemCount(): Int
}
