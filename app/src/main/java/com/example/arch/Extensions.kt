package com.example.arch

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.ring.android.architecture.viewmodel.AbstractBaseViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : ViewDataBinding> RingArch.binding(): ReadOnlyProperty<RingArch, T> =
    RingArchBinding(this, T::class.java)

inline fun <reified T : AbstractBaseViewModel> RingArch.viewModelUtils(noinline block: () -> dagger.Lazy<T>): ReadOnlyProperty<RingArch, T> =
    RingArchViewModelUtils(this, block, T::class.java)

inline fun <reified T : Component> RingArch.components(noinline block: () -> T, noinline also: (T) -> Unit): ReadOnlyProperty<RingArch, T> =
    RingArchComponents(this, block, T::class.java, also)

class RingArchBinding<T : ViewDataBinding>(
    ringArch: RingArch,
    cls: Class<T>
) : ReadOnlyProperty<RingArch, T> {

    private lateinit var binding: T

    init {
        ringArch.runOnCreate {
            val layoutInflater = LayoutInflater.from(context)
            val inflateMethod = cls.getMethod("inflate", LayoutInflater::class.java)
            binding = inflateMethod.invoke(null, layoutInflater) as T
            view = binding.root
        }
    }

    override fun getValue(thisRef: RingArch, property: KProperty<*>): T {
        if (!::binding.isInitialized) throw IllegalStateException("Super onCreate is not called")
        return binding
    }
}

class RingArchViewModelUtils<T : AbstractBaseViewModel>(
    ringArch: RingArch,
    lazy: () -> dagger.Lazy<T>,
    cls: Class<T>
) : ReadOnlyProperty<RingArch, T> {

    private lateinit var viewModel: T

    init {
        ringArch.runOnCreate { viewModel = getViewModel(lazy(), cls) }
    }

    override fun getValue(thisRef: RingArch, property: KProperty<*>): T {
        if (!::viewModel.isInitialized) throw IllegalStateException("Super onCreate is not called")
        return viewModel
    }
}

class RingArchComponents<T : Component>(
    ringArch: RingArch,
    block: () -> T,
    cls: Class<T>,
    also: (T) -> Unit
) : ReadOnlyProperty<RingArch, T> {

    private lateinit var component: T

    init {
        ringArch.runOnCreate { component = getComponents { block() }.get(cls).also(also) }
    }

    override fun getValue(thisRef: RingArch, property: KProperty<*>): T {
        if (!::component.isInitialized) throw IllegalStateException("Super onCreate is not called")
        return component
    }
}