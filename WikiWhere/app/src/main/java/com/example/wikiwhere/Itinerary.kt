package com.example.wikiwhere

import android.graphics.Bitmap
import android.net.Uri
import java.text.DateFormat
import java.time.LocalDate
import java.util.*


class Itinerary {
    var id : Int = 0
    var image : String = ""
    var name : String = ""
    var date : String = "01/01/2020"
    var list : Array<SavedPlace> =  arrayOf<SavedPlace>()
}