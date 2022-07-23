package com.alexandruleonte.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alexandruleonte.demo.cintent.CrimeFragment
import com.alexandruleonte.demo.cintent.CrimeListFragment
import com.alexandruleonte.demo.cintent.CrimeRepository
import java.util.*

private const val TAG = "MainActivity"
class CriminalIntentActivity : AppCompatActivity(), CrimeListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criminal_intent)
        CrimeRepository.initialize(this)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}