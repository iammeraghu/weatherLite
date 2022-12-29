package com.raghu.weatherlite


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings

import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request.Method.GET


import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import java.util.*
import java.util.concurrent.TimeUnit



class MainActivity : AppCompatActivity() {
    private lateinit var cityname: EditText
    private lateinit var region: TextView
    private lateinit var country: TextView
    private lateinit var wind: TextView
    private lateinit var press: TextView
    private lateinit var humidity: TextView
    private lateinit var reprt: TextView
    private lateinit var temp: TextView
    private lateinit var lup :TextView
    private lateinit var iview:ImageView
    private lateinit var debug:TextView
    private lateinit var button: ImageButton
    private lateinit var lbutton:ImageButton
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2










    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initviews()

        button.setOnClickListener{

            dood()
        }

        lbutton.setOnClickListener{

            cityname.setText("")
            getLocation()


        }


    }

    private fun initviews() {
        cityname = findViewById(R.id.cityname)
        button = findViewById(R.id.button)
        temp = findViewById(R.id.tempdisp)
        region = findViewById(R.id.region)
        country = findViewById(R.id.country)
        wind = findViewById(R.id.windspeed)
        press = findViewById(R.id.preassure)
        humidity = findViewById(R.id.humadity)
        reprt=findViewById(R.id.report)
        lup =findViewById(R.id.lastupdate)
        iview=findViewById(R.id.imageview)
        debug=findViewById(R.id.debug)
        lbutton=findViewById(R.id.locationbutton)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }





    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        cityname.setText("${list[0].locality}")
                        dood()




                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

        private fun isLocationEnabled(): Boolean {
            val locationManager: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }
        fun checkPermissions(): Boolean {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            return false
        }
        fun requestPermissions() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                permissionId
            )
        }
        @SuppressLint("MissingSuperCall")
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            if (requestCode == permissionId) {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocation()
                }
            }
        }



    fun dood(){
        var city: String = cityname.text.toString()
        var apiid = getString(R.string.yourapiid)
        val url: String = "https://api.weatherapi.com/v1/current.json?key=$apiid&q=$city&aqi=no"

        weather(url,city)


    }
    @SuppressLint("SetTextI18n")
    private fun weather(url: String,city:String) {

        val queue = Volley.newRequestQueue(this)


        val jsonObjectRequest = JsonObjectRequest(com.android.volley.Request.Method.GET, url,null,Response.Listener
            { response ->
                val current = response.getJSONObject("current")
                val location = response.getJSONObject("location")
                val cond=current.getJSONObject("condition")
                val cntry = location.getString("country")
                val rgn=  location.getString("region")
                val windkph=current.getString("wind_kph")
                val tempc= current.getString("temp_c")
                val pressr=current.getString("pressure_mb")
                val hum=current.getString("humidity")
                val rprt=cond.getString("text")
                val last=current.getString("last_updated")
                val imageur=cond.getString("icon")
                val imageurl="https:$imageur"



                temp.text=    "Temperature : $tempc°C"
                region.text=  "Region      : $rgn"
                country.text= "Country     : $cntry "
                wind.text=    "Wind        : $windkph Km/hr"
                press.text=   "Pressure    : $pressr mb"
                humidity.text="Humidity    : $hum"
                reprt.text=   "Overall     : $rprt"
                lup.text=     "last updated on : $last"
                Glide.with(this).load(imageurl).into(iview)
            },
            {

                temp.text=    ""
                region.text=  ""
                country.text= ""
                wind.text=    ""
                press.text=   ""
                humidity.text=""
                reprt.text=   ""
                iview.setImageResource(R.drawable.error)
                lup.text=     "ERROR"
            })

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)

    }

}


