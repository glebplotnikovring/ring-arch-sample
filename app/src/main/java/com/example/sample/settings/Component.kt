package com.example.sample.settings

import com.example.sample.App
import com.example.sample.Device
import com.example.sample.Settings
import com.example.sample.NetworkService
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class ExampleScope

@ExampleScope
@Subcomponent(modules = [ExampleModule::class])
interface ExampleComponent {
    fun inject(exampleActivity: ExampleActivity)
}

@Module
class ExampleModule(private val exampleDevice: Device) {

    @Provides
    @ExampleScope
    fun exampleViewModel(
        app: App,
        networkService: NetworkService,
        nonScopedDependency: NonScopedDependency,
        exampleAnalytics: ExampleAnalytics
    ) = ExampleViewModel(
        app,
        networkService,
        nonScopedDependency,
        exampleAnalytics,
        exampleDevice
    )

    @Provides
    fun nonScopedDependency() = NonScopedDependency()

    @Provides
    @ExampleScope
    fun exampleAnalytics() = ExampleAnalytics()

}

class ExampleAnalytics {
    var count: Int = 0
        private set
    private var isInitialLoadCalled = false
    private var settings: Settings? = null

    fun countReSetupUpdate() {
        count = count.inc()
    }

    fun initialLoadCalled() {
        isInitialLoadCalled = true
    }

    fun settingsRetrieved(settings: Settings) {
        this.settings = settings
    }

    override fun toString(): String {
        return "ExampleAnalytics(count=$count, isInitialLoadCalled=$isInitialLoadCalled, settings=$settings)"
    }

}

class NonScopedDependency
