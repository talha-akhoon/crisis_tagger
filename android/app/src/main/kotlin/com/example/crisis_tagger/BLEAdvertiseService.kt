package com.example.crisis_tagger

import android.Manifest
import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission

class BLEAdvertiseService : Service() {
    companion object {
        private const val TAG = "BLEAdvertiseService"
        private const val MANUFACTURER_ID = 0x1234 // Register your own ID
    }

    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    private var isAdvertising = false

    override fun onBind(intent: Intent?): IBinder? {
        return null // We don't need binding for this service
    }

    override fun onCreate() {
        super.onCreate()
        initializeBluetooth()
    }

    private fun initializeBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothLeAdvertiser = bluetoothManager.adapter?.bluetoothLeAdvertiser

        if (bluetoothLeAdvertiser == null) {
            Log.e(TAG, "Device doesn't support BLE advertising")
            stopSelf()
        }
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun startAdvertising(serviceUuid: String, data: ByteArray) {
        if (isAdvertising) {
            stopAdvertising()
        }

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setConnectable(false)
            .setTimeout(0)
            .build()

        val advertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceUuid(ParcelUuid.fromString(serviceUuid))
            .addManufacturerData(MANUFACTURER_ID, data)
            .build()

        bluetoothLeAdvertiser?.startAdvertising(
            settings,
            advertiseData,
            advertiseCallback
        )
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    private fun stopAdvertising() {
        if (isAdvertising) {
            bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
            isAdvertising = false
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.d(TAG, "Advertising started successfully")
            isAdvertising = true
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e(TAG, "Advertising failed with error: $errorCode")
            isAdvertising = false
            // Handle error codes:
            // ADVERTISE_FAILED_DATA_TOO_LARGE = 1
            // ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = 2
            // ADVERTISE_FAILED_ALREADY_STARTED = 3
            // ADVERTISE_FAILED_INTERNAL_ERROR = 4
            // ADVERTISE_FAILED_FEATURE_UNSUPPORTED = 5
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    override fun onDestroy() {
        stopAdvertising()
        super.onDestroy()
    }
}