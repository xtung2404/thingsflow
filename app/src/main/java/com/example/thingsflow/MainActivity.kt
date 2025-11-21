package com.example.thingsflow

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.thingsflow.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.platform.ILogR
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.SmartSdkEventCallback
import rogo.iot.module.rogocore.sdk.define.IoTEventNotify

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val isTablet = resources.getBoolean(R.bool.isTablet)
        requestedOrientation = if (isTablet) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView?.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.fragmentFlowScenManagement,
                    R.id.fragmentDevice
                -> {
                    binding.bottomNavigationView?.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigationView?.visibility = View.GONE
                }
            }
        }
        SmartSdk.registerEventCallback(object : SmartSdkEventCallback() {
            override fun onCloudConnectionReady(p0: Boolean) {
                ILogR.D(TAG, "onCloudConnectionReady", p0)
            }

            override fun onEntityDataSynced(p0: Int) {
                ILogR.D(TAG, "onEntityDataSynced", p0)
            }

            override fun onAllDataResyned() {
                ILogR.D(TAG, "onAllDataResyned")
            }

            override fun onEvent(
                p0: IoTEventNotify?,
                p1: String?
            ) {
                ILogR.D(TAG, "onEvent")
            }

            override fun onEventAppAction(
                p0: IoTEventNotify?,
                p1: String?
            ) {
                ILogR.D(TAG, "onEventAppAction")
            }
        })
    }
}