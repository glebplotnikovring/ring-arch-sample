package com.example.arch

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.ring.android.architecture.viewmodel.AbstractBaseViewModel
import kotlin.reflect.KProperty

inline fun <reified T : ViewDataBinding> RingArch.binding(): RingArchReadOnlyProperty<RingArch, T> =
    RingArchBinding(this, T::class.java)

inline fun <reified T : AbstractBaseViewModel> RingArch.viewModelUtils(noinline block: () -> dagger.Lazy<T>): RingArchReadOnlyProperty<RingArch, T> =
    RingArchViewModelUtils(this, block, T::class.java)

inline fun <reified T : Component> RingArch.components(noinline block: () -> T, noinline also: (T) -> Unit): RingArchReadOnlyProperty<RingArch, T> =
    RingArchComponents(this, block, T::class.java, also)

inline fun <reified C : Component> RingArch.component(noinline block: () -> C): RingArchReadOnlyProperty<RingArch, C> =
    RingArchComponent(this, C::class.java, block)

fun ViewDataBinding.setViewModel(viewModel: RingArchViewModel, id: Int = BR.viewModel) = setVariable(id, viewModel)

fun <T : ViewDataBinding> RingArchActivity.bindViewModel(id: Int = BR.viewModel, block: () -> RingArchViewModel): (T) -> Unit {
    return { binding ->
        binding.lifecycleOwner = this
        binding.setVariable(id, block())
    }
}

fun <T : ViewDataBinding> RingArchActivity.bindViewModelCompat(id: Int = BR.viewModel, block: () -> AbstractBaseViewModel): (T) -> Unit {
    return { binding ->
        binding.lifecycleOwner = this
        binding.setVariable(id, block())
    }
}

fun <T : ViewDataBinding> RingArchFragment.bindViewModel(id: Int = BR.viewModel, block: () -> RingArchViewModel): (T) -> Unit {
    return { binding ->
        binding.lifecycleOwner = viewLifecycleOwner
        binding.setVariable(id, block())
    }
}

class RingArchBinding<T : ViewDataBinding>(
    ringArch: RingArch,
    cls: Class<T>
) : RingArchReadOnlyProperty<RingArch, T> {

    private lateinit var binding: T
    private var whenReady: ((T) -> Unit)? = null

    init {
        ringArch.runOnCreate {
            val layoutInflater = LayoutInflater.from(context)
            val inflateMethod = cls.getMethod("inflate", LayoutInflater::class.java)
            binding = inflateMethod.invoke(null, layoutInflater) as T
            view = binding.root
            whenReady?.invoke(binding)
            whenReady = null
        }
    }

    override fun getValue(thisRef: RingArch, property: KProperty<*>): T {
        if (!::binding.isInitialized) throw IllegalStateException("Super onCreate is not called")
        return binding
    }

    override fun whenReady(block: (T) -> Unit): RingArchReadOnlyProperty<RingArch, T> {
        if (::binding.isInitialized) block(binding) else whenReady = block
        return this
    }
}

class RingArchViewModelUtils<T : AbstractBaseViewModel>(
    ringArch: RingArch,
    lazy: () -> dagger.Lazy<T>,
    cls: Class<T>
) : RingArchReadOnlyProperty<RingArch, T> {

    private lateinit var viewModel: T
    private var whenReady: ((T) -> Unit)? = null

    init {
        ringArch.runOnCreate {
            viewModel = getViewModel(lazy(), cls)
            whenReady?.invoke(viewModel)
            whenReady = null
        }
    }

    override fun getValue(thisRef: RingArch, property: KProperty<*>): T {
        if (!::viewModel.isInitialized) throw IllegalStateException("Super onCreate is not called")
        return viewModel
    }

    override fun whenReady(block: (T) -> Unit): RingArchReadOnlyProperty<RingArch, T> {
        if (::viewModel.isInitialized) block(viewModel) else whenReady = block
        return this
    }
}

class RingArchComponents<T : Component>(
    ringArch: RingArch,
    block: () -> T,
    cls: Class<T>,
    also: (T) -> Unit
) : RingArchReadOnlyProperty<RingArch, T> {

    private lateinit var component: T
    private var whenReady: ((T) -> Unit)? = null

    init {
        ringArch.runOnCreate {
            component = getComponents { block() }.get(cls).also(also)
            whenReady?.invoke(component)
            whenReady = null
        }
    }

    override fun getValue(thisRef: RingArch, property: KProperty<*>): T {
        if (!::component.isInitialized) throw IllegalStateException("Super onCreate is not called")
        return component
    }

    override fun whenReady(block: (T) -> Unit): RingArchReadOnlyProperty<RingArch, T> {
        if (::component.isInitialized) block(component) else whenReady = block
        return this
    }
}

class RingArchComponent<C : Component>(
    ringArch: RingArch,
    cls: Class<C>,
    init: () -> C
) : RingArchReadOnlyProperty<RingArch, C> {
    private lateinit var component: C
    private var whenReady: ((C) -> Unit)? = null

    init {
        ringArch.runOnCreate {
            component = getComponents { init() }.get(cls)
            whenReady?.invoke(component)
            whenReady = null
        }
    }

    override fun getValue(thisRef: RingArch, property: KProperty<*>): C {
        if (!::component.isInitialized) throw IllegalStateException("Super onCreate is not called")
        return component
    }

    override fun whenReady(block: (C) -> Unit): RingArchReadOnlyProperty<RingArch, C> {
        if (::component.isInitialized) block(component) else whenReady = block
        return this
    }

}