package com.eventtrack.leanlytics

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ListedActivity : AppCompatActivity() {

    private lateinit var mAdapter: MovieAdapter
    private var movieList: ArrayList<Movie>? = null
    private lateinit var list: LinearLayout
    private var viewHeight: Int = 0

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
        val tree = list.viewTreeObserver
        if (tree.isAlive) {
            tree.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    list.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    viewHeight = list.height

                    Log.e("height 1", "" + viewHeight)
                }

            })
        }
//
        val display = getWindowManager().getDefaultDisplay()
        val height = display.height
//
        Log.e("height 2", "" + height)
//
        Handler().postDelayed({
            //getScreen()
            //bitScroll = getBitmapFromView(list, list.getChildAt(0).height, list.getChildAt(0).width)
            //getScreenBitmap(list.rootView, height)
            //getScreenShot(list)
            //loadBitmapFromView(list.rootView,display.width,3000)
           // getViewBitmap(list)
        }, 1000)

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

    //create bitmap from the ScrollView
//    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        val bgDrawable = view.background
//        if (bgDrawable != null)
//            bgDrawable.draw(canvas)
//        else
//            canvas.drawColor(Color.WHITE)
//        view.draw(canvas)
//        return bitmap
//    }

    fun saveBitmap(bitmap: Bitmap) {

        val now = Date()
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
        val mPath = Environment.getExternalStorageDirectory().toString() + "/screenshot/" + now + ".jpeg"
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

    private fun takeScreenshot(view: View) {
        try {
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height, Bitmap.Config.ARGB_8888
            )
            view.destroyDrawingCache()
            view.isDrawingCacheEnabled = false
            storeBp(bitmap, "" + System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getBitmapFromView(view: View, height: Int): Bitmap {

        val returnedBitmap = Bitmap.createBitmap(
            view.measuredWidth,
            height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        storeBp(returnedBitmap, "" + System.currentTimeMillis())
        return returnedBitmap
    }

    fun getScreenBitmap(v: View,height: Int) {
        v.isDrawingCacheEnabled = true
        v.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        v.layout(0, 0, v.measuredWidth, height)

        v.buildDrawingCache(true)
        val b = v.drawingCache
        v.isDrawingCacheEnabled = false; // clear drawing cache
        storeBp(b, "" + System.currentTimeMillis())
    }

    fun getViewBitmap(v: View): Bitmap? {
        v.clearFocus()
        v.isPressed = false

        val willNotCache = v.willNotCacheDrawing()
        v.setWillNotCacheDrawing(false)

        val color = v.drawingCacheBackgroundColor
        v.drawingCacheBackgroundColor = 0

        if (color != 0) {
            v.destroyDrawingCache()
        }
        v.buildDrawingCache()
        val cacheBitmap = v.drawingCache ?: return null

        val bitmap = Bitmap.createBitmap(cacheBitmap)

        v.destroyDrawingCache()
        v.setWillNotCacheDrawing(willNotCache)
        v.drawingCacheBackgroundColor = color
        storeBp(bitmap,""+System.currentTimeMillis())
        return bitmap
    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(0, 0, v.layoutParams.width, v.layoutParams.height)
        v.draw(c)
        storeBp(b,""+System.currentTimeMillis())
        return b
    }


    private fun storeBp(bm: Bitmap, fileName: String) {
        if (!(bm.width == 0 && bm.height == 0)) {
            val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/Screenshots"
            val dir = File(dirPath)
            if (!dir.exists())
                dir.mkdirs()
            val file = File(dirPath, "$fileName.png")
            try {
                val fOut = FileOutputStream(file)
                bm.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.flush()
                fOut.close()
                Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    private fun takePhoto(view: View) {

        // Create a bitmap the size of the scene view.
        val bitmap = Bitmap.createBitmap(
            view.getWidth(), view.getHeight(),
            Bitmap.Config.ARGB_8888
        )

        // Create a handler thread to offload the processing of the image.
        val handlerThread = HandlerThread("PixelCopier")
        handlerThread.start()
        // Make the request to copy.
        PixelCopy.request(window, bitmap, { copyResult ->
            if (copyResult === PixelCopy.SUCCESS) {
                try {
                    storeBp(bitmap, "" + System.currentTimeMillis())
                } catch (e: IOException) {
                    val toast = Toast.makeText(
                        this@ListedActivity, e.toString(),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }

                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Photo saved", Snackbar.LENGTH_LONG
                )
                snackbar.setAction("Open in Photos") { v ->
                    val photoFile = File("" + System.currentTimeMillis())

                    val photoURI = FileProvider.getUriForFile(
                        this@ListedActivity,
                        this@ListedActivity.getPackageName() + ".ar.codelab.name.provider",
                        photoFile
                    )
                    val intent = Intent(Intent.ACTION_VIEW, photoURI)
                    intent.setDataAndType(photoURI, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)

                }
                snackbar.show()
            } else {
                val toast = Toast.makeText(
                    this@ListedActivity,
                    "Failed to copyPixels: $copyResult", Toast.LENGTH_LONG
                )
                toast.show()
            }
            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))
    }

    fun getScreenShot(view: View): Bitmap {
        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false

        val saveFile = getMainDirectoryName(this)//get the path to save screenshot
        val file =
            store(bitmap, "screenshotFULL.jpg", saveFile)//save the screenshot to selected path
        return bitmap
    }

    /*  Create Directory where screenshot will save for sharing screenshot  */
    fun getMainDirectoryName(context: Context): File {
        //Here we will use getExternalFilesDir and inside that we will make our Demo folder
        //benefit of getExternalFilesDir is that whenever the app uninstalls the images will get deleted automatically.
        val mainDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Demo"
        )

        //If File is not present create directory
        if (!mainDir.exists()) {
            if (mainDir.mkdir())
                Log.e("Create Directory", "Main Directory Created : $mainDir")
        }
        return mainDir
    }

    /*  Store taken screenshot into above created path  */
    fun store(bm: Bitmap, fileName: String, saveFilePath: File): File {
        val dir = File(saveFilePath.absolutePath)
        if (!dir.exists())
            dir.mkdirs()
        val file = File(saveFilePath.absolutePath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }
}
