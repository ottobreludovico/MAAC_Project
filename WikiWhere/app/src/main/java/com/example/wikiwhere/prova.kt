package com.example.wikiwhere

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.savedplacelist.*

class prova : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.savedplacelist)



        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        list.addItemDecoration(itemDecoration)

        val adapter = PlaceAdapter(this)

        list.layoutManager= LinearLayoutManager(this)
        list.adapter=adapter
        fab.setOnClickListener(adapter)

    }
}