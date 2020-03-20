package dev.itsu.bpx.command.model

import dev.itsu.bpx.api.model.BlockLog

data class CoData(
        var x: Int,
        var y: Int,
        var z: Int,
        var levelName: String,
        var index: Int,
        var data: List<BlockLog>
) {

    fun equals(x: Int, y: Int, z: Int, levelName: String): Boolean {
        return this.x == x && this.y == y && this.z == z && this.levelName == levelName
    }

    companion object {
        fun emptyData() = CoData(-1, -1,-1, "Unknown", -1, listOf())
    }

}