package whereismytransport.di.module

import dagger.Module
import dagger.Provides
import whereismytransport.whereismycheese.CheesyService

@Module
class ServiceModule(private val cheesyService: CheesyService) {

    @Provides
    fun cheeseService(): CheesyService {
        return cheesyService
    }
}