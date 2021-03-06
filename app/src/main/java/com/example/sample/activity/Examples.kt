package com.example.sample.activity

import android.app.Application
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.example.arch.*
import com.example.arch.databinding.ActivityDummyBinding
import com.example.arch.databinding.ActivityStubBinding
import com.example.sample.appComponent
import com.ring.android.architecture.view.AbstractBaseActivity
import com.ring.android.architecture.viewmodel.AbstractBaseViewModel
import javax.inject.Inject

// These all overrides are needed in order to have simple activity without view model and binding
// NOTE: You need to add this activity to ContributesInjector!
class SimpleActivity : AbstractBaseActivity<ViewDataBinding, StubViewModel>() {
    override val tag: String = "SimpleActivity"
    override val layoutId: Int = 0
    override val viewModelClass: Class<StubViewModel> = StubViewModel::class.java
    override val viewModelId: Int = 0
    override val bindingEnabled: Boolean = false
    override val viewModelEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
        binding.root // NullPointerException
        viewModel.stub() // there is no need in it here but a reference exist
    }
}

class SimpleActivityAlternative : RingArchActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
    }
}

class OnlyBindingActivity : AbstractBaseActivity<ActivityDummyBinding, StubViewModel>() {
    override val tag: String = ""
    override val layoutId: Int = R.layout.activity_dummy
    override val viewModelClass: Class<StubViewModel> = StubViewModel::class.java
    override val viewModelId: Int = 0
    override val viewModelEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root // no NullPointerException, but a reference
    }
}

// If you need to have object binding reference
class OnlyBindingActivityAlternative : RingArchActivity() {
    private val binding by binding<ActivityDummyBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root
    }
}

// If you need to have binding reference in onCreate()
class OnlyBindingActivityAlternative2 : RingArchActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = setContentBinding<ActivityDummyBinding>(R.layout.activity_dummy)
        binding.root
    }
}

class OnlyViewModelActivity : AbstractBaseActivity<ViewDataBinding, StubViewModel>() {
    override val tag: String = ""
    override val layoutId: Int = 0
    override val viewModelClass: Class<StubViewModel> = StubViewModel::class.java
    override val viewModelId: Int = 0
    override val bindingEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
        viewModel.stub()
    }
}

class OnlyViewModelActivityAlternative : RingArchActivity(R.layout.activity_dummy) {

    @Inject
    lateinit var viewModelLazy: dagger.Lazy<StubViewModel>
    private val viewModel by viewModelUtils { viewModelLazy }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.stub()
    }
}

class OnlyViewModelActivityAlternative2 : RingArchActivity(R.layout.activity_dummy) {
    init {
        component { appComponent().dummyComponent(DummyModule()) } whenReady { it.inject(this) }
    }

    @Inject
    lateinit var viewModel: DummyViewModel
}

class OnlyViewModelActivityAlternative3 : RingArchActivity(R.layout.activity_dummy) {
    private val component by component { appComponent().dummyComponent(DummyModule()) } whenReady { it.inject(this) }

    @Inject
    lateinit var viewModel: DummyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // do something with a component reference here
        component.inject(this)
    }
}

class BindingViewModelActivity : AbstractBaseActivity<ActivityDummyBinding, StubViewModel>() {
    override val tag: String = ""
    override val layoutId: Int = R.layout.activity_dummy
    override val viewModelClass: Class<StubViewModel> = StubViewModel::class.java
    override val viewModelId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root
        viewModel.stub()
    }
}

class BindingViewModelActivityAlternative : RingArchActivity() {
    @Inject
    lateinit var viewModelLazy: dagger.Lazy<StubViewModel>
    private val viewModel by viewModelUtils { viewModelLazy }
    private val binding by binding<ActivityStubBinding>() whenReady bindViewModelCompat { viewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root
        viewModel.stub()
    }
}

class BindingViewModelActivityAlternative2 : RingArchActivity() {
    init {
        component { appComponent().dummyComponent(DummyModule()) } whenReady { it.inject(this) }
        binding<ActivityDummyBinding>() whenReady bindViewModel { viewModel }
    }

    @Inject
    lateinit var viewModel: DummyViewModel
}

class StubViewModel(application: Application) : AbstractBaseViewModel(application) {
    override val tag: String = ""
    override fun init(bundle: Bundle) {}
    fun stub() {}
}

class DummyViewModel(application: Application) : RingArchViewModel(application)

class TemplateActivity : AbstractBaseActivity<ActivityDummyBinding, StubViewModel>() {
    override val layoutId: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val tag: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val viewModelClass: Class<StubViewModel>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val viewModelId: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val bindingEnabled: Boolean
        get() = super.bindingEnabled
    override val viewModelEnabled: Boolean
        get() = super.viewModelEnabled
}
