package com.example.lab_week_11_b

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Date
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var providerManager: ProviderFileManager
    private var latestTmpUri: FileInfo? = null

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            providerManager.insertImageToStore(latestTmpUri)
        }
    }

    private val recordVideo = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) 
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            providerManager.insertVideoToStore(latestTmpUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        providerManager = ProviderFileManager(
            this,
            FileHelper(this),
            contentResolver,
            Executors.newSingleThreadExecutor(),
            MediaContentHelper()
        )

        findViewById<Button>(R.id.photo_button).setOnClickListener {
            takePicture()
        }

        findViewById<Button>(R.id.video_button).setOnClickListener {
            recordVideo()
        }
    }

    private fun takePicture() {
        val time = Date().time
        latestTmpUri = providerManager.generatePhotoUri(time)
        latestTmpUri?.let {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, it.uri)
            takePicture.launch(takePictureIntent)
        }
    }

    private fun recordVideo() {
        val time = Date().time
        latestTmpUri = providerManager.generateVideoUri(time)
        latestTmpUri?.let {
            val recordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            recordVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, it.uri)
            recordVideo.launch(recordVideoIntent)
        }
    }
}