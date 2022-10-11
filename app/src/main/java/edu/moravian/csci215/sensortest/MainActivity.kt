package edu.moravian.csci215.sensortest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.moravian.csci215.sensortest.databinding.ActivityMainBinding

val ACCURACIES = listOf("", "Low", "Medium", "High")
val REPORTING_MODES = listOf("Continuous", "On Change", "One Shot", "Special Trigger")

class MainActivity : AppCompatActivity(), SensorEventListener, AdapterView.OnItemSelectedListener {
    /** The view binding for the activity. */
    private lateinit var binding: ActivityMainBinding

    /** The TextViews that will display the sensor event values. */
    private val valueViews: List<TextView> by lazy {
        listOf(
            binding.value0, binding.value1, binding.value2, binding.value3, binding.value4,
            binding.value5, binding.value6, binding.value7, binding.value8, binding.value9,
            binding.value10, binding.value11, binding.value12, binding.value13, binding.value14,
            binding.value15
        )
    }

    // TODO: lots more properties to add here

    private  var sensorManager: SensorManager? = null
    private var sensors: List<Sensor>  = emptyList()
    private  var sensor: Sensor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensors = sensorManager?.getSensorList(Sensor.TYPE_ALL) ?: emptyList()
        updateSensorsList(sensors)

        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensor?.let { updateSensorInfo(it) }

        binding.sensors.onItemSelectedListener = this

        val sensorPos = savedInstanceState?.getInt("sensor_position", 0) ?: 0
        binding.sensors.setSelection(sensorPos)
        sensor = sensors[sensorPos]

        // TODO: lots more code to add here
    }

    // TODO: lots more methods to add here

    override fun onResume()
    {
        super.onResume()
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause()
    {
        super.onPause()
        sensorManager?.unregisterListener(this, sensor)

    }

    override fun onDestroy()
    {
        super.onDestroy()
        sensorManager = null
        sensor = null
    }

    /**
     * Update sensor info being displayed
     * @param sensor the sensor to show the information about
     */
    private fun updateSensorInfo(sensor: Sensor) {
        binding.id.text = sensor.id.toString()
        binding.name.text = sensor.name
        binding.vendor.text = sensor.vendor
        binding.type.text = getString(R.string.number_description, sensor.type, sensor.stringType)
        binding.version.text = sensor.version.toString()
        binding.reportingMode.text = getString(R.string.number_description, sensor.reportingMode, REPORTING_MODES[sensor.reportingMode])
        binding.isWakeUpSensor.text = sensor.isWakeUpSensor.toString()
        binding.isDynamicSensor.text = sensor.isDynamicSensor.toString()
        binding.maxRange.text = sensor.maximumRange.toString()
        binding.resolution.text = sensor.resolution.toString()
        binding.power.text = getString(R.string.float_unit, sensor.power, "mA")
        binding.delay.text = getString(R.string.range_unit, sensor.minDelay, sensor.maxDelay, "µs")
    }

    /**
     * Update sensor event info being displayed
     * @param event the sensor event to show the information about
     */
    private fun updateSensorEventInfo(event: SensorEvent) {
        binding.accuracy.text = getString(R.string.number_description, event.accuracy, ACCURACIES[event.accuracy])
        binding.timestamp.text = getString(R.string.int_unit, event.timestamp, "ns")
        event.values.zip(valueViews).forEach { (value, view) -> view.text = value.toString() }
        valueViews.subList(event.values.size, valueViews.size).forEach { it.text = "" }
    }

    /**
     * Clear all sensor event info being displayed
     */
    private fun clearSensorEventInfo() {
        binding.accuracy.text = ""
        binding.timestamp.text = ""
        valueViews.forEach { it.text = "" }
    }

    /**
     * Updates the list of sensors being displayed in the spinner.
     * @param sensors the list of sensors to be displayed in the spinner
     */
    private fun updateSensorsList(sensors: List<Sensor>) {
        val dataAdapter = ArrayAdapter(this, R.layout.spinner_style, sensors.map { it.name })
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style)
        binding.sensors.adapter = dataAdapter
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putInt("sensor_position", binding.sensors.selectedItemPosition)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            updateSensorEventInfo(event)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        sensorManager?.unregisterListener(this, sensor)
        sensor = sensors[position]
        updateSensorInfo(sensor!!)
        clearSensorEventInfo()
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) { }
}
