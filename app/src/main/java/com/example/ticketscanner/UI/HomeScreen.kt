package com.example.ticketscanner.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amtron.zooticket.helper.ResponseHelper
import com.example.ticketscanner.R
import com.example.ticketscanner.databinding.ActivityMainBinding
import com.example.ticketscanner.network.Client
import com.example.ticketscanner.network.RetrofitHelper
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class HomeScreen : AppCompatActivity() {
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        if (ContextCompat.checkSelfPermission(
                this@HomeScreen, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }
    }


    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()



        binding.cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            val animation = AnimationUtils.loadAnimation(this@HomeScreen, R.anim.scanner_animation)


            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Log.d("Response", "Value: $scannedValue")

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue
                    barcodeDetector.release()

                    if (scannedValue.isNotEmpty()) {
//                        sendBookingDataToServer(scannedValue)
                        sendBookingDataToServer(scannedValue)
                    }

                    Log.d("RESPONSE: ", "VALUE: $scannedValue")
                } else {
                    Log.d("RESPONSE", "VALUE: ELSE")
                }
            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@HomeScreen,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(this@HomeScreen, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun sendBookingDataToServer(bookingNumber: String?) {
        val api = RetrofitHelper.getInstance().create(Client::class.java)
        api.sendBookingDataToServer(scannedValue).enqueue(object : Callback<JsonObject> {
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
                        val intent = Intent(this@HomeScreen, ResultScreen::class.java)
                        intent.putExtra("response_data", helper.getDataAsString())
                        startActivity(intent)
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
        val intent = Intent(this, ResultScreen::class.java)
        bundle.putString("result", result)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun stopCamera() {
        cameraSource.stop()
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }


}