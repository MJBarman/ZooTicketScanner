package com.example.ticketscanner.UI.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.amtron.zooticket.helper.NotificationsHelper
import com.amtron.zooticket.helper.ResponseHelper
import com.amtron.zooticket.helper.Util
import com.example.ticketscanner.R
import com.example.ticketscanner.model.ScannedTicket
import com.example.ticketscanner.model.User
import com.example.ticketscanner.network.Client
import com.example.ticketscanner.network.RetrofitHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@DelicateCoroutinesApi
class DailyScannedAdapter(
    private val scannedList: List<ScannedTicket>, private val c: Context
) : RecyclerView.Adapter<DailyScannedAdapter.ViewHolder>() {

    private lateinit var mItemClickListener: OnRecyclerViewItemClickListener
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mContext: Context
    private lateinit var user: User
    private lateinit var ticket: ScannedTicket

    fun setOnItemClickListener(mItemClickListener: OnRecyclerViewItemClickListener?) {
        this.mItemClickListener = mItemClickListener!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        sharedPreferences = c.getSharedPreferences(
            "ASZCounter", AppCompatActivity.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
        user = Gson().fromJson(
            sharedPreferences.getString("user", ""), object : TypeToken<User>() {}.type
        )

        mContext = parent.context

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.scanned_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ticket = scannedList[position]



        holder.mobileNumber.text = ticket.mobile_no
        holder.bookingNumber.text = ticket.total_person.toString()
        holder.price.text = ticket.total_amt.toString()
        holder.numberVisitors.text = ticket.total_person.toString()
        holder.numberCameras.text = ticket.total_camera.toString()


    }

    private fun getScannedDetails() {
        val api = RetrofitHelper.getInstance().create(Client::class.java)
        GlobalScope.launch {
            val call: Call<JsonObject> = api.getDailyScanData(
                Util().getJwtToken(
                    sharedPreferences.getString("user", "").toString()
                )
            )
            call.enqueue(object : Callback<JsonObject> {
                @SuppressLint("CommitPrefEdits", "NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<JsonObject>, response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        val helper = ResponseHelper()
                        helper.ResponseHelper(response.body())
                        if (helper.isStatusSuccessful()) {
                            val scannedTicketList: ScannedTicket = Gson().fromJson(
                                helper.getDataAsString(),
                                object : TypeToken<ScannedTicket>() {}.type
                            )
                            Log.d("list", scannedTicketList.toString())
                        }
                    } else {
                        NotificationsHelper().getErrorAlert(
                            mContext, "Response Error Code" + response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    NotificationsHelper().getErrorAlert(mContext, "Server Error")
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return scannedList.size


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mobileNumber: TextView = itemView.findViewById(R.id.tv_mobile_number)
        val bookingNumber: TextView = itemView.findViewById(R.id.tv_booking_no)
        val price: TextView = itemView.findViewById(R.id.tv_total_amount)
        val numberVisitors: Button = itemView.findViewById(R.id.total_visitors)
        val numberCameras: Button = itemView.findViewById(R.id.total_camera)
    }
}