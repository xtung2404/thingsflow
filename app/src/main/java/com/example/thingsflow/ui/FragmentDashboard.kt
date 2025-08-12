package com.example.thingsflow.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.platform.ILogR

@AndroidEntryPoint
class FragmentDashboard : BaseFragment<FragmentHomeBinding>() {
    private val TAG = "HomeFragment"
    override val layoutId: Int
        get() = R.layout.fragment_home


    override fun initAction() {
        super.initAction()
        initializeBluetoothOrRequestPermission()
        binding.apply {
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.identifyDeviceFragment)
            }
        }
    }

    /**
     * Checks and requests the necessary Bluetooth permissions before initializing Bluetooth.
     *
     * This function performs the following steps:
     * 1. Creates a list of required permissions:
     *    - BLUETOOTH_SCAN: required for scanning BLE devices (Android 12+).
     *    - BLUETOOTH_CONNECT: required for connecting to BLE devices (Android 12+).
     *    - ACCESS_FINE_LOCATION: required for BLE scanning on Android < 12.
     *
     * 2. Checks whether all required permissions are granted.
     *    - If **all permissions are granted**, it calls `initializeBluetooth()` to proceed with Bluetooth initialization.
     *    - If **some permissions are missing**, it requests them using `ActivityCompat.requestPermissions(...)`.
     */
    private fun initializeBluetoothOrRequestPermission() {
        val requiredPermissions = mutableListOf<String>()

        requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
        requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            initializeBluetooth()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), missingPermissions.toTypedArray(), 2)
        }
    }

    /**
     * Initializes the Bluetooth system service and requests the user to enable Bluetooth if it's disabled.
     */
    private fun initializeBluetooth() {
        val bluetoothManager: BluetoothManager = ContextCompat.getSystemService(
            requireActivity(),
            BluetoothManager::class.java
        ) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter?.isEnabled == false) {
            val turnOn = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            ActivityCompat.startActivityForResult(requireActivity(), turnOn, 0, null)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                if (grantResults.none { it != PackageManager.PERMISSION_GRANTED }) {
                    initializeBluetooth()
                } else {
                    ILogR.D(TAG, "onRequestPermissionsResult:notGranted")
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}