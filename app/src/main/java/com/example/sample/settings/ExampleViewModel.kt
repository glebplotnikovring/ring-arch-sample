package com.example.sample.settings

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.arch.RingArchViewModel
import com.example.sample.Device
import com.example.sample.NetworkService
import com.example.sample.Settings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class ExampleViewModel(
    application: Application,
    // scoped by global application
    private val networkService: NetworkService,
    // its just an example of such dependencies
    private val nonScopedDependency: NonScopedDependency,
    // ExampleComponent scope
    private val exampleAnalytics: ExampleAnalytics,
    device: Device // initial data, no Bundle
) : RingArchViewModel(application) {

    private val _device = MutableLiveData<Device>(device)
    // Setting retrieving stuff
    private val _settings = MutableLiveData<Settings>()
    private val _settingsLoading = MutableLiveData<Boolean>(false)
    private val _settingsError = MutableLiveData<Error>()
    private var settingsLoadingDisposable: Disposable? = null
    // Settings updating stuff
    private val _settingsUpdating = MutableLiveData<Boolean>(false)
    private val _settingsUpdatingError = MutableLiveData<Error>()
    private var settingsUpdatingDisposable: Disposable? = null

    val device: LiveData<Device> = _device
    val settings: LiveData<Settings> = _settings
    val settingsLoading: LiveData<Boolean> = _settingsLoading
    val settingsLoadingError: LiveData<Error> = _settingsError
    val settingsUpdating: LiveData<Boolean> = _settingsUpdating
    val settingsUpdatingError: LiveData<Error> = _settingsUpdatingError

    fun initialLoad() {
        if (settingsLoadingDisposable != null) return
        val exampleDevice = device.value ?: return
        Observable.fromCallable { networkService.getExampleSettings(exampleDevice) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _settingsLoading.value = true
                _settingsError.value = null
            }
            .doAfterTerminate {
                _settingsLoading.value = false
                settingsLoadingDisposable = null
            }
            .subscribe(
                {
                    _settings.value = it
                    exampleAnalytics.settingsRetrieved(it)
                },
                {
                    // example of the various error handling
                    when (it) {
                        is IOException -> _settingsError.value = Error.NoInternet
                        else -> _settingsError.value = Error.InvalidValue
                    }
                }
            )
            .also { settingsLoadingDisposable = it }
    }

    fun onChangeReSetup() {
        device.value ?: return
        val exampleSettings = settings.value ?: return
        if (settingsUpdatingDisposable != null) return
        Observable.just(exampleSettings.deviceReSetup.inc())
            .map { networkService.updateReSetup(exampleSettings, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _settingsUpdating.value = true
                _settingsUpdatingError.value = null
            }
            .doAfterTerminate {
                _settingsUpdating.value = false
                settingsUpdatingDisposable = null
            }
            .subscribe(
                {
                    _settings.value = it
                    exampleAnalytics.settingsRetrieved(it)
                },
                {
                    _settingsUpdatingError.value = Error.CantUpdateSettings
                })
            .also { settingsUpdatingDisposable = it }
        exampleAnalytics.countReSetupUpdate()
    }

    sealed class Error {
        object NoInternet : Error()
        object InvalidValue : Error()
        object CantUpdateSettings : Error()
    }

}