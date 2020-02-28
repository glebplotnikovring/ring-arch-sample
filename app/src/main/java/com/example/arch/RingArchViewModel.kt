package com.example.arch

import android.app.Application
import androidx.lifecycle.AndroidViewModel

// Is it necessary?
abstract class RingArchViewModel(application: Application) : AndroidViewModel(application)