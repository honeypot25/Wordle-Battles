package com.honeyapps.wordlebattles.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

// TODO: add a vibration upon a shake
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit,
    private var canTrigger: Boolean = true
): SensorEventListener {

    companion object {
        // a lower value makes it more sensitive
        private const val SHAKE_THRESHOLD = 13f
        // minimum interval between two shakes
        private const val SHAKE_INTERVAL = 1000
    }

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var lastShakeTime: Long = 0

    init {
        registerListener()
    }

    fun registerListener() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }

//    fun setCanTrigger(value: Boolean) {
//        canTrigger = value
//    }

    // required
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (canTrigger) {
            val now = System.currentTimeMillis()

            // if a shake event happens after the minimum interval
            if (
                isShakeDetected(values = event.values) &&
                (now - lastShakeTime >= SHAKE_INTERVAL)
            ) {
                lastShakeTime = now
                onShake()
            }
        }
    }

    private fun isShakeDetected(values: FloatArray): Boolean {
        val x: Float = values[0]
        val y: Float = values[1]
        val z: Float = values[2]
        val acceleration = sqrt(x*x + y*y + z*z)

        return acceleration > SHAKE_THRESHOLD
    }
}
