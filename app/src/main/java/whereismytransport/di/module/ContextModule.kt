package whereismytransport.di.module

import android.content.Context

import dagger.Module
import dagger.Provides
import whereismytransport.di.qualifier.ApplicationContext
import whereismytransport.di.scope.ApplicationScope

@Module(includes = [ViewModelModule::class])
class ContextModule(private val context: Context) {

    @Provides
    @ApplicationScope
    @ApplicationContext
    fun provideContext(): Context {
        return context
    }
}