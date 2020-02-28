package com.example.sample.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.arch.Components
import com.example.arch.R
import com.example.arch.RingArchActivity
import com.example.arch.databinding.ActivityExampleBinding
import com.example.sample.Device
import com.example.sample.get
import kotlinx.android.synthetic.main.activity_example.*
import javax.inject.Inject

/**
 * Supports orientation changes
 */
class ExampleActivity : RingArchActivity() {

    companion object {
        fun newIntent(context: Context, exampleDevice: Device) =
            Intent(context, ExampleActivity::class.java).apply {
                putExtra(DEVICE_SERIALIZABLE, exampleDevice)
            }

        private const val DEVICE_SERIALIZABLE = "extraDeviceSerializable"
    }

    // NOTE: Object instances are managed by dagger tool
    @Inject
    lateinit var nonScopedDependency: NonScopedDependency // new instance every time
    @Inject
    @ExampleScope
    lateinit var exampleViewModel: ExampleViewModel // same instance across orientation changes
    @Inject
    @ExampleScope
    lateinit var exampleAnalytics: ExampleAnalytics // same instance, allows to preserve its state

    private lateinit var binding: ActivityExampleBinding
    // Just in case this is needed else-where here in class
    private val exampleDevice by lazy { intent.getSerializableExtra(DEVICE_SERIALIZABLE) as Device }
    private val component by lazy {
        // Components acts just like viewModelStore, but allows to store custom components
        Components.of(this) {
            // This factory will be called only once for creation ExampleComponent instance if it's not created yet
            // whole dependency graph is stored here and user defined injection rules are safe
            application.get().appComponent.exampleComponent(ExampleModule(exampleDevice))
        }.get(ExampleComponent::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        // init data binding
        // if you don't need a binding, just use regular setContentView
        binding = setContentBinding<ActivityExampleBinding>(R.layout.activity_example)
            .apply { viewModel = exampleViewModel }

        // subscribe to live data (can go directly to data binding)
        // if you don't need a view model, just don't use it
        exampleViewModel.device.observe(this, Observer { deviceNameView.text = it.name })
        exampleViewModel.settings.observe(this, Observer {
            settingsFeatureView.text = it.featureEnable.toString()
            settingsReSetupView.text = it.deviceReSetup.toString()
        })
        exampleViewModel.settingsLoading.observe(this, Observer { show ->
            val fragment = supportFragmentManager.findFragmentByTag("loading") as? DialogFragment
            if (show && fragment == null) {
                LoadingDialogFragment().show(supportFragmentManager, "loading")
            } else if (!show && fragment != null) {
                fragment.dismiss()
            }
        })
        exampleViewModel.settingsLoadingError.observe(this, Observer {
            if (it != null) {
                // once error fired!
                Toast.makeText(this, "Can't load settings! $it", LENGTH_SHORT).show()
            }
        })

        // perform initial loading
        // or, do nothing
        if (savedInstanceState == null) {
            exampleViewModel.initialLoad()
            // Note that countReSetupUpdate() is triggered in viewModel
            exampleAnalytics.initialLoadCalled()
        }
    }

    class LoadingDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(requireContext()).setMessage("Loading Settings").setCancelable(false).create()
    }

    fun onShowAnalyticsToastClick(view: View) {
        // This is the same instance on every rotate
        Toast.makeText(this, "$exampleAnalytics", LENGTH_SHORT).show()
    }

    // just like setContentView, but setContentBinding
    // setting this as lifecycleOwner is a bonus!
    private fun <T : ViewDataBinding> setContentBinding(@LayoutRes layoutId: Int) =
        DataBindingUtil.setContentView<T>(this, layoutId).also { it.lifecycleOwner = this }

}

