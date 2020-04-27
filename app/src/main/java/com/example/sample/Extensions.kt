package com.example.sample

import android.app.Application
import com.example.arch.RingArchActivity

fun Application.get(): App = this as App

fun RingArchActivity.appComponent() = application.get().appComponent