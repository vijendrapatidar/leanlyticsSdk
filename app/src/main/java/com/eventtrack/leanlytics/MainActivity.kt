package com.eventtrack.leanlytics

import android.Manifest
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.eventtrack.leanlyticssdk.LeanlyticsAnalytics


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)

        val rootView = findViewById<ConstraintLayout>(R.id.mainLayout)
        LeanlyticsAnalytics.initInstance()
        LeanlyticsAnalytics.getInstance().start(application)

//        LeanlyticsAnalytics.initInstance(applicationContext)
//        LeanlyticsAnalytics.getInstance().uploadScreenShot(true, rootView)
    }
}
