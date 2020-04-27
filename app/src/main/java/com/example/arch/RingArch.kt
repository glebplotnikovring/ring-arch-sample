package com.example.arch

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.ring.android.architecture.viewmodel.AbstractBaseViewModel
import com.ring.android.architecture.viewmodel.ViewModelUtils
import dagger.Lazy
import kotlin.properties.ReadOnlyProperty

interface RingArch {
    fun runOnCreate(what: RingArchHandler.() -> Unit)
}

interface RingArchHandler {
    val context: Context
    val lifecycleOwner: LifecycleOwner
    var view: View?
    fun <T : AbstractBaseViewModel> getViewModel(lazy: Lazy<T>, clazz: Class<T>): T
    fun getComponents(factory: Factory<out Component>): Components
}

interface RingArchReadOnlyProperty<R, T> : ReadOnlyProperty<R, T> {
    infix fun whenReady(block: (T) -> Unit): RingArchReadOnlyProperty<R, T>
}

class ActivityRingArchHandler(private val activity: () -> RingArchActivity) : RingArchHandler {
    override val context = activity()
    override val lifecycleOwner: LifecycleOwner = activity()
    override var view: View? = null

    override fun <T : AbstractBaseViewModel> getViewModel(lazy: Lazy<T>, clazz: Class<T>): T {
        return ViewModelUtils().createOrFetchViewModel(activity(), lazy, clazz)
    }

    override fun getComponents(factory: Factory<out Component>): Components {
        return Components.of(activity(), factory)
    }
}

class FragmentRingArchHandler(private val fragment: () -> RingArchFragment) : RingArchHandler {
    override val context = fragment().requireActivity()
    override val lifecycleOwner: LifecycleOwner = fragment().viewLifecycleOwner
    override var view: View? = null

    override fun <T : AbstractBaseViewModel> getViewModel(lazy: Lazy<T>, clazz: Class<T>): T {
        return ViewModelUtils().createOrFetchViewModel(fragment(), lazy, clazz)
    }

    override fun getComponents(factory: Factory<out Component>): Components {
        return Components.of(fragment().requireActivity(), factory)
    }
}