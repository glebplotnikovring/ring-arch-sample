package com.example.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class RingArchFragment : Fragment(), RingArch {
    private val runOnCreates = mutableListOf<RingArchHandler.() -> Unit>()
    private val handler = FragmentRingArchHandler { this }

    override fun runOnCreate(what: RingArchHandler.() -> Unit) {
        runOnCreates += what
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runOnCreates.apply { forEach { it(handler) } }.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = handler.view?.let { it } ?: super.onCreateView(
        inflater,
        container,
        savedInstanceState
    )
}