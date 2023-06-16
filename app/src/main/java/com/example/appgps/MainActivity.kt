package com.example.appgps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), SensorEventListener, LocationListener {

    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mLocationManager: LocationManager? = null

    private var sensorDataList: MutableList<SensorData> = mutableListOf()

    private lateinit var txtX: TextView
    private lateinit var txtY: TextView
    private lateinit var txtZ: TextView
    private lateinit var txtLatitude: TextView
    private lateinit var txtLongitude: TextView

    private var adapter: SensorDataAdapter? = null

    private val handler = Handler()
    private val interval: Long = 1000 // Intervalo de 1 segundo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtX = findViewById(R.id.txtX)
        txtY = findViewById(R.id.txtY)
        txtZ = findViewById(R.id.txtZ)
        txtLatitude = findViewById(R.id.txtLatitude)
        txtLongitude = findViewById(R.id.txtLongitude)

        // ACELEROMETRO
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mSensorManager!!.flush(this)
        mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        // GPS
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        }

        startLocationUpdates()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewSensorData)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = SensorDataAdapter(sensorDataList)
        recyclerView.adapter = adapter

        startDataCollection()
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        txtX.text = x.toString()
        txtY.text = y.toString()
        txtZ.text = z.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Ignorar
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        runOnUiThread {
            txtLatitude.text = latitude.toString()
            txtLongitude.text = longitude.toString()
        }

        val x = try {
            txtX.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Valor padrão em caso de formato incorreto
        }

        val y = try {
            txtY.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Valor padrão em caso de formato incorreto
        }

        val z = try {
            txtZ.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Valor padrão em caso de formato incorreto
        }

        val atleta = "Bruno"
        val timestamp = System.currentTimeMillis()
        val sensorData = SensorData(atleta, latitude, longitude, x, y, z, timestamp)
        sensorDataList.add(sensorData)

        adapter?.notifyDataSetChanged()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Ignorar
    }

    override fun onProviderEnabled(provider: String) {
        // Ignorar
    }

    override fun onProviderDisabled(provider: String) {
        // Ignorar
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                this
            )
        }
    }

    private fun startDataCollection() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                createSensorDataEntry()
                handler.postDelayed(this, interval)
            }
        }, interval)
    }

    private fun createSensorDataEntry() {
        val latitude = try {
            txtLatitude.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Valor padrão em caso de formato incorreto
        }

        val longitude = try {
            txtLongitude.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Valor padrão em caso de formato incorreto
        }

        val x = try {
            txtX.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Valor padrão em caso de formato incorreto
        }

        val y = try {
            txtY.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Valor padrão em caso de formato incorreto
        }

        val z = try {
            txtZ.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Valor padrão em caso de formato incorreto
        }

        val atleta = "BRUNO"
        val timestamp = System.currentTimeMillis()
        val sensorData = SensorData(atleta, latitude, longitude, x, y, z, timestamp)
        sensorDataList.add(sensorData)

        adapter?.notifyDataSetChanged()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 5000 // 5 segundos
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 5 // 5 metros
    }
}
