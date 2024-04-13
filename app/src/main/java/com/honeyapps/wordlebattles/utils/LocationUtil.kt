package com.honeyapps.wordlebattles.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import java.util.Locale

object LocationUtil {

    fun isGpsEnabled(ctx: Context): Boolean {
        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    suspend fun getCountryName(ctx: Context): String? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
        return try {
            val geocoder = Geocoder(ctx, Locale.getDefault())
            val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, null).await()
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val countryName = addresses?.get(0)?.countryName?.lowercase()
            countryName
        } catch (e: Exception) {
            Log.e("getCountryName", "exception: $e")
            null
        }
    }
}