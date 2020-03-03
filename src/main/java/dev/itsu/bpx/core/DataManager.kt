package dev.itsu.bpx.core

class DataManager {

    companion object {
        val coQueue = mutableMapOf<String, Boolean>()
        var exceptLevels = mutableListOf<String>()
        var protectedLevels = mutableListOf<String>()
        var daysCount = 3
    }

}