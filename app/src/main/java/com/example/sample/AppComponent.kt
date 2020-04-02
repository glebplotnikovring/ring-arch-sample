package com.example.sample

import com.example.sample.activity.DummyComponent
import com.example.sample.activity.DummyModule
import com.example.sample.settings.ExampleComponent
import com.example.sample.settings.ExampleModule
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import java.io.IOException
import java.io.Serializable
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AndroidInjectionModule::class])
interface AppComponent : AndroidInjector<App> {
    fun exampleComponent(exampleModule: ExampleModule): ExampleComponent
    fun dummyComponent(dummyModule: DummyModule): DummyComponent

    @Component.Builder
    interface Builder {
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }
}

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    fun app() = app

    @Provides
    @Singleton
    fun networkService(app: App): NetworkService = ExampleNetworkService(app)

    @Provides
    @Singleton
    fun userSpecifics(app: App): UserSpecifics = ExampleUserSpecifics(app)

    @Provides
    fun utilities() = Utilities()
}

interface NetworkService {
    @Throws(IOException::class)
    fun getExampleSettings(exampleDevice: Device): Settings

    @Throws(IOException::class)
    fun updateReSetup(settings: Settings, reSetupCount: Int): Settings
}

internal class ExampleNetworkService(app: App) : NetworkService {

    @Throws(IOException::class)
    override fun getExampleSettings(exampleDevice: Device): Settings {
        // just an example of some long-running task
        return try {
            Thread.sleep(2000)
            Settings(false, 1)
        } catch (e: InterruptedException) {
            Settings(false, 1)
        }
    }

    @Throws(IOException::class)
    override fun updateReSetup(
        settings: Settings,
        reSetupCount: Int
    ): Settings {
        return try {
            Thread.sleep(1000)
            settings.copy(deviceReSetup = reSetupCount)
        } catch (e: InterruptedException) {
            throw IOException(e)
        }
    }
}

interface UserSpecifics {
    fun getUserName(): String
}

internal class ExampleUserSpecifics(app: App) : UserSpecifics {
    override fun getUserName(): String = "Ivan Petrov"
}

class Utilities

data class Device(val id: Int, val name: String) : Serializable
data class Settings(val featureEnable: Boolean, val deviceReSetup: Int) : Serializable