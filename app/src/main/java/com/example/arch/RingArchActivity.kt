package com.example.arch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

// Is it necessary?
abstract class RingArchActivity : AppCompatActivity() {

    private val onCreateInitList = mutableListOf<OnCreateInit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateInitList.forEach { it.run() }
    }

    private class InitBinding<T : ViewDataBinding>(private val activity: RingArchActivity, private val layoutId: Int) : Lazy<T>, OnCreateInit {
        override fun isInitialized(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        init {
            activity.onCreateInitList.add(this)
        }

        private lateinit var _value: T

        override fun run() {
            _value = DataBindingUtil.inflate(activity.layoutInflater, layoutId, null, false)
        }

        override val value: T
            get() = if (!this::_value.isInitialized) throw RuntimeException() else _value

    }

    protected fun <T : ViewDataBinding> binding(block: () -> Int) : Lazy<T> = InitBinding(this, block())
    private interface OnCreateInit {
        fun run()
    }
}