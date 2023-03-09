package com.example.ticketscanner.UI
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtron.zooticket.helper.NotificationsHelper
import com.amtron.zooticket.helper.ResponseHelper
import com.amtron.zooticket.helper.Util
import com.example.ticketscanner.R
import com.example.ticketscanner.UI.Components.MyBottomSheetFragment
import com.example.ticketscanner.databinding.ActivityMainScreenBinding
import com.example.ticketscanner.model.OverallScan
import com.example.ticketscanner.model.ScanData
import com.example.ticketscanner.network.Client
import com.example.ticketscanner.network.RetrofitHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(DelicateCoroutinesApi::class)
class MainScreen : AppCompatActivity() {


    private lateinit var binding: ActivityMainScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        var fabVisible = false
        sharedPreferences = this.getSharedPreferences("ASZCounter", MODE_PRIVATE)




        binding.profile.setOnClickListener {
            val intent = Intent(this@MainScreen, ProfileScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.dailyScannedCv.setOnClickListener{
            val intent = Intent(this@MainScreen, DailyScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }





        binding.fabAdd.setOnClickListener {
            if (!fabVisible) {

                binding.fabBooking.show()
                binding.fabScanner.show()
                binding.fabBooking.visibility = View.VISIBLE
                binding.fabScanner.visibility = View.VISIBLE


                binding.fabAdd.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_cancel_24))
                fabVisible = true
            } else {
                binding.fabBooking.hide()
                binding.fabScanner.hide()
                binding.fabBooking.visibility = View.GONE
                binding.fabScanner.visibility = View.GONE

                binding.fabAdd.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_circle_24))
                fabVisible = false
            }
        }
        binding.fabBooking.setOnClickListener {
            // on below line we are displaying a toast message.
            val bottomSheetFragment = MyBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.fabScanner.setOnClickListener {
            val intent = Intent(this@MainScreen, ScannerScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }


        getDailyScanData()
        getOverAllScanData()


    }

    private fun getOverAllScanData() {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !Util().isOnline(this@MainScreen)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            SweetAlertDialog(this@MainScreen, SweetAlertDialog.ERROR_TYPE).setTitleText("ERROR!")
                .setContentText("No Internet Available").setConfirmText("Retry")
                .setConfirmClickListener {
                    getDailyScanData()
                }.show()
        } else {
            val api = RetrofitHelper.getInstance().create(Client::class.java)
            GlobalScope.launch {
                val call: Call<JsonObject> = api.getOverallScanData(
                    Util().getJwtToken(
                        sharedPreferences.getString("user", "").toString()
                    )
                )
                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>, response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            val helper = ResponseHelper()
                            helper.ResponseHelper(response.body())
                            if (helper.isStatusSuccessful()) {
                                val gson = Gson()
                                val response =
                                    gson.fromJson(helper.getDataAsString(), OverallScan::class.java)
                                Log.d("Overall Count: ", response.overallCount.toString())
                                binding.tvOverallScanCount.text = response.overallCount.toString()
                            } else {
                                NotificationsHelper().getErrorAlert(
                                    this@MainScreen, helper.getErrorMsg()
                                )
                            }
                        } else {
                            NotificationsHelper().getErrorAlert(
                                this@MainScreen, "Response Error Code" + response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        NotificationsHelper().getErrorAlert(this@MainScreen, "Server Error")
                    }
                })
            }
        }
    }


    private fun getDailyScanData() {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !Util().isOnline(this@MainScreen)
            } else {
                throw UnsupportedOperationException("Unsupported Android version")
            }
        ) {
            SweetAlertDialog(this@MainScreen, SweetAlertDialog.ERROR_TYPE).setTitleText("ERROR!")
                .setContentText("No Internet Available").setConfirmText("Retry")
                .setConfirmClickListener {
                    getDailyScanData()
                }.show()
        } else {
            val api = RetrofitHelper.getInstance().create(Client::class.java)
            lifecycleScope.launch {
//            val call: Call<JsonObject> = api.getPriceDetails(Util().getJwtToken(userString))
                val call: Call<JsonObject> = api.getDailyScanData(
                    Util().getJwtToken(
                        sharedPreferences.getString("user", "").toString()
                    )
                )
                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>, response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            val helper = ResponseHelper()
                            helper.ResponseHelper(response.body())
                            if (helper.isStatusSuccessful()) {
                                val gson = Gson()
                                val response =
                                    gson.fromJson(helper.getDataAsString(), ScanData::class.java)
                                binding.tvDailyScannedCount.text = response.dailyCount.toString()
                            } else {
                                NotificationsHelper().getErrorAlert(
                                    this@MainScreen, helper.getErrorMsg()
                                )
                            }
                        } else {
                            NotificationsHelper().getErrorAlert(
                                this@MainScreen, "Response Error Code" + response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        NotificationsHelper().getErrorAlert(this@MainScreen, "Server Error")
                    }
                })
            }
        }
    }




    override fun onBackPressed() {
        if (!shouldAllowBack()) {
            return
        }
        super.onBackPressed()
    }

    private fun shouldAllowBack(): Boolean {
        return false
    }
}
