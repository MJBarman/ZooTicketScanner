package com.example.ticketscanner.UI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketscanner.databinding.ActivityResultScreenBinding
import org.json.JSONObject

class ResultScreen : AppCompatActivity() {
    private var TAG: String = "TAG"


    private lateinit var binding: ActivityResultScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        // Retrieve the response data from the intent extras
        val responseData = intent.getStringExtra("response_data")
        if (responseData != null) {
            // Parse the response data as a JSON object
            val jsonObject = JSONObject(responseData)
            // Extract the required fields
            val bookingNo = jsonObject.getString("booking_no")
            val visitorName = jsonObject.getString("visitor_name")
            val visitingDate = jsonObject.getString("visiting_date")
            val mobileNumber = jsonObject.getString("mobile_no")
            val ticketPrice = jsonObject.getString("net_amt")
            val serviceCharge = jsonObject.getString("service_amt")
            val totalAmount = jsonObject.getString("total_amt")
            val noOfVisitors = jsonObject.getString("t_person")
            val noOfCameras = jsonObject.getString("t_camera")

            // Set the extracted fields on the respective TextViews
            binding.ticketNoTv.text = bookingNo
            binding.visitorName.text = visitorName
            binding.visitingDate.text = visitingDate
            binding.noOfVisitorsTv.text = noOfVisitors
            binding.noOfCameras.text = noOfCameras
            binding.ticketPriceTv.text = ticketPrice
            binding.serviceChargeTv.text = serviceCharge
            binding.totalAmountTv.text = totalAmount
            binding.mobileNo.text = mobileNumber

        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, EndScreenSuccess::class.java)
            startActivity(intent)
        }


    }
}