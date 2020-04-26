package com.googlegeofenceapi

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_new_reminder.*
import kotlin.math.roundToInt


class ReminderActivity : AppCompatActivity(), OnMapReadyCallback {

  private lateinit var map: GoogleMap

  lateinit var myRepository: MyReminderRepository

  private var reminder = MyReminder(latLng = null, radius = null, message = null)


  private fun updateRadius(progress: Int) {
    val radius = getCicleRadius(progress)
    reminder.radius = radius
    radiusDescription.text = radius.roundToInt().toString()+" meters"
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_reminder)
    myRepository = MyReminderRepository(this)
    val mapFragment = supportFragmentManager
        .findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)

    instructionTitle.visibility = View.GONE
    radiusDescription.visibility = View.GONE
    message.visibility = View.GONE

  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    map.uiSettings.isMapToolbarEnabled = false

    centerCamera()

    showConfigureLocationStep()
  }

  private fun centerCamera() {
    val latLng = intent.extras?.get(AppConstants.LAT_LNG) as LatLng
    val zoom = intent.extras?.get(AppConstants.ZOOM) as Float
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
  }

  private fun showConfigureLocationStep() {
    marker.visibility = View.VISIBLE
    instructionTitle.visibility = View.VISIBLE
    radiusDescription.visibility = View.GONE
    message.visibility = View.GONE
    instructionTitle.text = "Drag map to set location"
    next.setOnClickListener {
      reminder.latLng = map.cameraPosition.target
      showConfigureRadiusStep()
    }

    showReminderUpdate()
  }

  private fun showConfigureRadiusStep() {
    marker.visibility = View.GONE
    instructionTitle.visibility = View.VISIBLE
    radiusDescription.visibility = View.VISIBLE
    message.visibility = View.GONE
    instructionTitle.text = "Your radius is"
    next.setOnClickListener {
      showConfigureMessageStep()
    }

    updateRadius(1)

    map.animateCamera(CameraUpdateFactory.zoomTo(15f))

    showReminderUpdate()
  }

  private fun getCicleRadius(progress: Int) = 100 + (2 * progress.toDouble() + 1) * 100

  private fun showConfigureMessageStep() {
    marker.visibility = View.GONE
    instructionTitle.visibility = View.VISIBLE
    message.visibility = View.VISIBLE
    instructionTitle.text = "Enter a message"
    next.setOnClickListener {

      reminder.message = message.text.toString()

      if (reminder.message.isNullOrEmpty()) {
        message.error = "Message Error"
      } else {
        addReminder(reminder)
      }
    }

    showReminderUpdate()
  }

  private fun addReminder(myReminder: MyReminder) {

    myRepository.add(myReminder,
        success = {
          setResult(Activity.RESULT_OK)
          finish()
        },
        failure = {
          Snackbar.make(main, it, Snackbar.LENGTH_LONG).show()
        })
  }

  private fun showReminderUpdate() {
    map.clear()
    showReminderInMap(this, map, reminder)
  }
}
