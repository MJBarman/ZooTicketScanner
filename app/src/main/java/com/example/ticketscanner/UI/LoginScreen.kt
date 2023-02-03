package com.example.ticketscanner.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.amtron.zooticket.helper.NotificationsHelper
import com.amtron.zooticket.helper.ResponseHelper
import com.amtron.zooticket.helper.Util
import com.example.ticketscanner.network.Client
import com.amtron.zooticket.network.RetrofitHelper
import com.example.ticketscanner.R
import com.example.ticketscanner.databinding.ActivityLoginScreenBinding
import com.example.ticketscanner.model.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginScreen : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var userString: String
    private lateinit var binding: ActivityLoginScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        sharedPreferences = this.getSharedPreferences("ASZCounter", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        userString = sharedPreferences.getString("user", "").toString()
        if (userString.isNotEmpty()) {
            val intent = Intent(this@LoginScreen, HomeScreen::class.java)
            startActivity(intent)
        }

        if (!Util().isOnline(this@LoginScreen)) {
            binding.buttonProceed.visibility = View.GONE
            binding.noInternetCard.visibility = View.VISIBLE
            binding.refresh.setOnClickListener {
                finish()
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        } else {
            binding.buttonProceed.visibility = View.VISIBLE
            binding.noInternetCard.visibility = View.GONE

            binding.buttonProceed.setOnClickListener {
                if (binding.phoneNumber.text.isEmpty() || binding.password.text.isEmpty()) {
                    NotificationsHelper().getErrorAlert(
                        this@LoginScreen,
                        "Please enter all the fields"
                    )
                } else {
                    if (binding.phoneNumber.text.length != 10) {
                        NotificationsHelper().getErrorAlert(
                            this@LoginScreen,
                            "please enter valid phone number"
                        )
                    } else {
                        login(
                            binding.phoneNumber.text.toString(),
                            binding.password.text.toString()
                        )
                    }
                }
            }
        }

    }

    private fun login(phoneNo: String, password: String) {
        binding.progressbar.show()
        val api = RetrofitHelper.getInstance().create(Client::class.java)
        GlobalScope.launch {
            val call: Call<JsonObject> = api.login(
                phoneNo,
                password
            )
            call.enqueue(object : Callback<JsonObject> {
                @SuppressLint("CommitPrefEdits", "NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        binding.progressbar.hide()
                        val helper = ResponseHelper()
                        helper.ResponseHelper(response.body())
                        if (helper.isStatusSuccessful()) {
                            val user: User = Gson().fromJson(
                                helper.getDataAsString(),
                                object : TypeToken<User>() {}.type
                            )
                            editor.putString("user", Gson().toJson(user))
                            editor.apply()
                            startActivity(Intent(this@LoginScreen, HomeScreen::class.java))
//                                getPriceDetails()
                        } else {
                            NotificationsHelper().getErrorAlert(
                                this@LoginScreen,
                                helper.getErrorMsg()
                            )
                        }
                    } else {
                        binding.progressbar.hide()
                        NotificationsHelper().getErrorAlert(
                            this@LoginScreen,
                            "Response Error Code" + response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    binding.progressbar.hide()
                    NotificationsHelper().getErrorAlert(this@LoginScreen, "Server Error")
                }
            })
        }
    }
}