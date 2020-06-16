package whereismytransport.di.component

import android.content.Context
import dagger.Component
import whereismytransport.di.module.ContextModule
import whereismytransport.di.module.ServiceModule
import whereismytransport.di.qualifier.ApplicationContext
import whereismytransport.di.scope.ApplicationScope
import whereismytransport.ui.MainActivity
import whereismytransport.whereismycheese.CheesyService

@ApplicationScope
@Component(modules = [ContextModule::class, ServiceModule::class])
interface ApplicationComponent {

    @ApplicationContext
    fun getContext(): Context?

    fun injectMainActivity(mainActivity: MainActivity)
    fun injectCheeseService(cheesyService: CheesyService)
}