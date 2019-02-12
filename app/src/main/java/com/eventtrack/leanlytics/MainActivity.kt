package com.eventtrack.leanlytics

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.eventtrack.leanlyticssdk.LatLonDistanceCalculator
import com.eventtrack.leanlyticssdk.Point
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var point: Point = Point(floor(-34.6037389f), floor(-58.3815704f))
        var point1: Point = Point(floor(40.6892494f), floor(-74.0445004f))
        var latLonDistanceCalculator = LatLonDistanceCalculator.calculateDistance(point, point1);

        tvDistance.text = "" + latLonDistanceCalculator
    }
}
