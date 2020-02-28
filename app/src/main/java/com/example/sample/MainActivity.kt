package com.example.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.arch.R
import com.example.sample.settings.ExampleActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    fun onManyViewModelExampleClick(view: View) {

    }

    fun onSingleViewModelExampleClick(view: View) {
        startActivity(
            ExampleActivity.newIntent(
                this,
                Device(1, "The Device")
            )
        )
    }
}
