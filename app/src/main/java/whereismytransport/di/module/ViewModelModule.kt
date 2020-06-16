package whereismytransport.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import whereismytransport.di.scope.ViewModelKey
import whereismytransport.ui.MainViewModel
import whereismytransport.viewModel.ViewModelFactory

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindHomeViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}