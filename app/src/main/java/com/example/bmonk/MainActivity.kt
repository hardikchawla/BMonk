package com.example.bmonk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        val firstTime = sharedPreferences.getString("FirstTimeInstall", "")

        if(firstTime.equals("Yes")) {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
            finish()

        } else {

            val editor = sharedPreferences.edit()
            editor.apply {
                putString("FirstTimeInstall", "Yes")
            }.apply()

        }

    }

    fun getstart(view: View) {

        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)

    }
}