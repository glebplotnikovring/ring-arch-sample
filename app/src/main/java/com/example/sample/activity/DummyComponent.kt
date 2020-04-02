package com.example.sample.activity

import com.example.arch.Component
import com.example.sample.App
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class DummyScope

@DummyScope
@Subcomponent(modules = [DummyModule::class])
interface DummyComponent : Component {
    fun inject(bindingViewModelActivityAlternative2: BindingViewModelActivityAlternative2)
}

@Module
class DummyModule {
    @DummyScope
    @Provides
    fun dummyViewModel(app: App) = DummyViewModel(app)
}