package com.example.arch

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.ring.android.architecture.viewmodel.AbstractBaseViewModel

abstract class RingArchActivity : FragmentActivity, RingArch {
    private var tieOp: (() -> Unit)? = null
    private val onCreates = mutableListOf<RingArchHandler.() -> Unit>()

    constructor() : super()
    constructor(@LayoutRes layoutId: Int) : super(layoutId)

    override fun runOnCreate(what: RingArchHandler.() -> Unit) {
        if (lifecycle.currentState == Lifecycle.State.INITIALIZED) {
            onCreates += what
        }
    }

    protected fun <T : ViewDataBinding> setContentBinding(layoutId: Int): T {
        return DataBindingUtil.inflate<T>(layoutInflater, layoutId, null, false)
            .apply { setContentView(root) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreates.takeIf { it.isNotEmpty() }?.apply {
            val handler = ActivityRingArchHandler { this@RingArchActivity }
            forEach { it(handler) }
            if (handler.view != null) setContentView(handler.view)
        }?.clear()
        tieOp?.invoke()
        tieOp = null
    }

    protected fun tieCompat(
        viewModel: () -> AbstractBaseViewModel,
        binding: () -> ViewDataBinding,
        id: Int = 0
    ) {
        tieOp = {
            binding().apply {
                setVariable(id, viewModel())
                setLifecycleOwner { lifecycle }
            }
        }
    }

    protected fun tie(
        viewModel: () -> RingArchViewModel,
        binding: () -> ViewDataBinding,
        id: Int = 0
    ) {
        tieOp = {
            binding().apply {
                setVariable(id, viewModel())
                setLifecycleOwner { lifecycle }
            }
        }
    }
}