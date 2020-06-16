package whereismytransport.ui

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import whereismytransport.util.LocationProviderUtil
import whereismytransport.whereismycheese.CheesyTreasure
import javax.inject.Inject

class MainViewModel @Inject constructor(private val locationProviderUtil: LocationProviderUtil) : ViewModel() {

    val currentLocation: LiveData<Location>
        get() = locationProviderUtil.currentLocation

    private val listOfCheese = ArrayList<CheesyTreasure>()

    fun addCheese(cheesyTreasure: CheesyTreasure) {
        listOfCheese.add(cheesyTreasure)
        mutableListOfCheese.value = listOfCheese
    }

    fun removeCheese(cheesyTreasure: CheesyTreasure) {
        listOfCheese.removeAll {
            it.note?.trim() == cheesyTreasure.note?.trim()
        }
        mutableListOfCheese.value = listOfCheese
    }

    companion object {
        val mutableListOfCheese = MutableLiveData<List<CheesyTreasure>>()
    }
}