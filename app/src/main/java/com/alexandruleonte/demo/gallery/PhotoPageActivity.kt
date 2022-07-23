package com.alexandruleonte.demo.gallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexandruleonte.demo.R

class PhotoPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)

    }
    companion object {
        fun newIntent(context: Context, photoPageUri: Uri): Intent {
            return Intent(context, PhotoPageActivity::class.java).apply {
                data = photoPageUri
            }
        }
    }
}