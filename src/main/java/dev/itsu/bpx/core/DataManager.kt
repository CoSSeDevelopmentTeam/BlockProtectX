package dev.itsu.bpx.core

import dev.itsu.bpx.command.model.CoData

class DataManager {

    companion object {
        val coQueue = mutableMapOf<String, CoData>()
        var exceptLevels = mutableListOf<String>()
        var protectedLevels = mutableListOf<String>()
        var daysCount = 3
    }

}