package com.example.arch

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

// Very simple implementation of viewModelStore-like utility
class Components private constructor(
    private val activity: FragmentActivity,
    private val factory: Factory<out Component>
) {

    companion object {
        fun of(activity: FragmentActivity, factory: Factory<out Component>): Components =
            Components(activity, factory)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Component> get(clazz: Class<T>): T = with(storage(activity)) {
        var value = get(clazz)
        if (value == null) {
            value = factory(clazz).also { put(clazz, it) }
        }
        value as T
    }

    private fun storage(activity: FragmentActivity): Storage =
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

interface Component