package com.example.arch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

// Very simple implementation of viewModelStore-like utility
class Components private constructor(
    private val activity: AppCompatActivity,
    private val factory: Factory<*>?
) {

    companion object {
        fun of(activity: AppCompatActivity, factory: Factory<*>?): Components =
            Components(activity, factory)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(clazz: Class<T>): T = with(storage(activity)) {
        var value = get(clazz)
        if (value == null) {
            value = factory?.invoke(clazz)?.also { put(clazz, it) }
        }
        checkNotNull(value)
        value as T
    }

    private fun storage(activity: AppCompatActivity): Storage =
        with(activity.supportFragmentManager) {
            findFragmentByTag("simpleComponentsStorage") as? Storage ?: Storage().also {
                beginTransaction().add(it, "simpleComponentsStorage").commitNow()
            }
        }

    class Storage : Fragment() {
        private val components = mutableMapOf<Any, Any>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        fun put(key: Any, value: Any) {
            components[key] = value
        }

        fun get(key: Any) = components[key]
    }

}

typealias Factory<T> = (Class<T>) -> T