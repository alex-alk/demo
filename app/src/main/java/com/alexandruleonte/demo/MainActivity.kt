package com.alexandruleonte.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sunsetBtn = findViewById<Button>(R.id.sunset_btn)
        val dragBtn = findViewById<Button>(R.id.drag)
        val quizBtn = findViewById<Button>(R.id.quiz)
        val nerdLauncherBtn = findViewById<Button>(R.id.launcher)
        val beatBoxBtn = findViewById<Button>(R.id.beatbox)
        val cintentBtn = findViewById<Button>(R.id.cintent)
        val galleryBtn = findViewById<Button>(R.id.gallery)

        sunsetBtn.setOnClickListener() {
            intent = Intent(this, SunsetActivity::class.java)
            startActivity(intent)
        }

        dragBtn.setOnClickListener() {
            intent = Intent(this, DragAndDrawActivity::class.java)
            startActivity(intent)
        }

        quizBtn.setOnClickListener() {
            intent = Intent(this, GeoQuizActivity::class.java)
            startActivity(intent)
        }

        nerdLauncherBtn.setOnClickListener() {
            intent = Intent(this, NerdLauncherActivity::class.java)
            startActivity(intent)
        }

        beatBoxBtn.setOnClickListener() {
            intent = Intent(this, BeatboxActivity::class.java)
            startActivity(intent)
        }

        cintentBtn.setOnClickListener() {
            intent = Intent(this, CriminalIntentActivity::class.java)
            startActivity(intent)
        }

        galleryBtn.setOnClickListener() {
            intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }
}