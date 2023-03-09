package com.example.ticketscanner.UI

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketscanner.UI.Adapters.DailyScannedAdapter
import com.example.ticketscanner.databinding.ActivityDailyScanedListScreenBinding
import com.example.ticketscanner.model.ScannedTicket
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class DailyScreen : AppCompatActivity() {
    private lateinit var binding: ActivityDailyScanedListScreenBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dailyScannedList: ArrayList<ScannedTicket>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyScannedAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyScanedListScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        sharedPreferences = this@DailyScreen.getSharedPreferences(
            "ASZCounter",
            MODE_PRIVATE
        )

        dailyScannedList = ArrayList()

        Log.d("Tag", dailyScannedList.toString())
        adapter = DailyScannedAdapter(dailyScannedList, this)

        recyclerView = binding.ticketListRecyclerView
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
    }


}