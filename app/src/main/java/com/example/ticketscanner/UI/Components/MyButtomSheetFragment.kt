package com.example.ticketscanner.UI.Components

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtron.zooticket.helper.ResponseHelper
import com.amtron.zooticket.helper.Util
import com.example.ticketscanner.R
import com.example.ticketscanner.UI.ResultScreen
import com.example.ticketscanner.databinding.FragmentMyBottomSheetBinding
import com.example.ticketscanner.network.Client
import com.example.ticketscanner.network.RetrofitHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class MyBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMyBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private var scannedValue: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBottomSheetBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("ASZCounter", MODE_PRIVATE)
        binding.btnSubmit.setOnClickListener {
            scannedValue = binding.bookingNumber.text.toString().uppercase()
            sendBookingDataToServer(scannedValue)
        }
        return binding.root
    }

    private fun sendBookingDataToServer(bookingNumber: String?) {
        val api = RetrofitHelper.getInstance().create(Client::class.java)

        api.sendBookingDataToServer(
            Util().getJwtToken(
                sharedPreferences.getString("user", "").toString()
            ), scannedValue
        ).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // Handle successful response from the server
                    val helper = ResponseHelper()
                    helper.ResponseHelper(response.body())
                    if (helper.isStatusSuccessful()) {
                        val obj = JSONObject(helper.getDataAsString())
                        val status = obj.get("status") as Int
                        Log.d("status: ", status.toString())
                        // Create an intent to start the ResultScreen activity and pass the response data
                        val intent = Intent(requireActivity(), ResultScreen::class.java)
                        intent.putExtra("response_data", helper.getDataAsString())
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    } else {
                        sendResultValue(helper.getErrorMsg())
                    }
                } else {
                    sendResultValue("Response Error Code: " + response.message())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                sendResultValue("Server Error")
            }
        })
    }

    private fun sendResultValue(result: String) {
        val bundle = Bundle()
        val intent = Intent(requireActivity(), ResultScreen::class.java)
        bundle.putString("result", result)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
