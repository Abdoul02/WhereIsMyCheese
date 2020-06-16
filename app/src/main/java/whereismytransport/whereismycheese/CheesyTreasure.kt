package whereismytransport.whereismycheese

import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * Cheezy Treasures are such a delight
 */
class CheesyTreasure(location: LatLng?, content: String?) {
    var location: LatLng? = null
    var note: String? = null

    // For simplicities sake, we will just assume all cheesy treasures have unique notes
    fun equals(other: CheesyTreasure): Boolean {
        return note == other.note
    }

    init {
        this.location = location
        note = content
    }
}