package com.eventtrack.leanlytics

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        Handler().postDelayed(Runnable {
            takeScreenshot()
        },2000)
    }

    private fun takeScreenshot(): Bitmap {
        val v1 = window.decorView.rootView
        v1.isDrawingCacheEnabled = true
        v1.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(v1.drawingCache)
        v1.destroyDrawingCache()
        v1.isDrawingCacheEnabled = false

        Log.e("bitmap", "" + bitmap)
        return bitmap
    }
}
