package com.eventtrack.leanlytics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class ListActivity : AppCompatActivity() {

    private lateinit var mAdapter: MovieAdapter
    private var movieList: ArrayList<Movie>? = null
    private lateinit var list: LinearLayout

    private lateinit var bitScroll: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        movieList = ArrayList()

        val recyclerView = findViewById<RecyclerView>(R.id.rvDocuments)
        list = findViewById<LinearLayout>(R.id.list)

        mAdapter = MovieAdapter(movieList)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter

        prepareMovieData()

        /* val rootView = window.decorView.findViewById<View>(android.R.id.content)
         getScreenShot(recyclerView.rootView)*/

        /*Handler().postDelayed({
            //getScreen()
            bitScroll = getBitmapFromView(list, list.getChildAt(0).height, list.getChildAt(0).width)
            saveBitmap(bitScroll)
        }, 1000)*/

    }

    private fun prepareMovieData() {
        var movie = Movie("Mad Max: Fury Road", "Action & Adventure", "2015")
        movieList!!.add(movie)

        movie = Movie("Inside Out", "Animation, Kids & Family", "2015")
        movieList!!.add(movie)

        movie = Movie("Star Wars: Episode VII - The Force Awakens", "Action", "2015")
        movieList!!.add(movie)

        movie = Movie("Shaun the Sheep", "Animation", "2015")
        movieList!!.add(movie)

        movie = Movie("The Martian", "Science Fiction & Fantasy", "2015")
        movieList!!.add(movie)

        movie = Movie("Mission: Impossible Rogue Nation", "Action", "2015")
        movieList!!.add(movie)

        movie = Movie("Up", "Animation", "2009")
        movieList!!.add(movie)

        movie = Movie("Star Trek", "Science Fiction", "2009")
        movieList!!.add(movie)

        movie = Movie("The LEGO Movie", "Animation", "2014")
        movieList!!.add(movie)

        movie = Movie("Iron Man", "Action & Adventure", "2008")
        movieList!!.add(movie)

        movie = Movie("Aliens", "Science Fiction", "1986")
        movieList!!.add(movie)

        movie = Movie("Chicken Run", "Animation", "2000")
        movieList!!.add(movie)

        movie = Movie("Back to the Future", "Science Fiction", "1985")
        movieList!!.add(movie)

        movie = Movie("Raiders of the Lost Ark", "Action & Adventure", "1981")
        movieList!!.add(movie)

        movie = Movie("Goldfinger", "Action & Adventure", "1965")
        movieList!!.add(movie)

        movie = Movie("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014")
        movieList!!.add(movie)

        mAdapter.notifyDataSetChanged()
    }

    fun getScreenShot(view: View) {
        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        store(bitmap, "" + System.currentTimeMillis())
    }

    fun store(bm: Bitmap, fileName: String) {
        val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/Sagar"
        val dir = File(dirPath)
        if (!dir.exists())
            dir.mkdirs()
        val file = File(dirPath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getScreen() {
        val v = list.rootView
        v.isDrawingCacheEnabled = true
        val b = v.drawingCache
        val extr = Environment.getExternalStorageDirectory().toString()
        val myPath = File(extr, "" + System.currentTimeMillis() + ".jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            MediaStore.Images.Media.insertImage(
                contentResolver, b,
                "Screen", "screen"
            )
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    //create bitmap from the ScrollView
    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    fun saveBitmap(bitmap: Bitmap) {

        val now = Date()
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg"
        val imagePath = File(mPath)

        val fos: FileOutputStream
        try {
            fos = FileOutputStream(imagePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            Toast.makeText(applicationContext, imagePath.absolutePath + "", Toast.LENGTH_LONG).show()
            Log.e("ImageSave", "Saveimage")
        } catch (e: FileNotFoundException) {
            Log.e("GREC", e.message, e)
        } catch (e: IOException) {
            Log.e("GREC", e.message, e)
        }

    }
}
