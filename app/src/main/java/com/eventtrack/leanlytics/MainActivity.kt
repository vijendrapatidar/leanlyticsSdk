package com.eventtrack.leanlytics

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.eventtrack.leanlyticssdk.LeanlyticsAnalytics
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)

        LeanlyticsAnalytics.initInstance()
        LeanlyticsAnalytics.getInstance().start(this, application, "OkRcYmRk1")
        tvDistance.setOnClickListener { startActivity(Intent(this@MainActivity, TestActivity::class.java)) }

        //LeanlyticsAnalytics.initInstance(applicationContext)
        //LeanlyticsAnalytics.getInstance().takeScreenshot(this.window.decorView.rootView)

    }
}
