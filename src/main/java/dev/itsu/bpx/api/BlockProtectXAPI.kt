package dev.itsu.bpx.api

import dev.itsu.bpx.api.model.BlockLog
import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.level.Position
import dev.itsu.bpx.api.model.PlayerData

object BlockProtectXAPI {

    /* Block Logger */
    fun createLog(player: Player, position: Position, block: Block, type: BlockLog.ActionType) {
        val logs = getLogs(position)
        if (logs.isNotEmpty() && type == BlockLog.ActionType.TYPE_PLACE) deleteLogs(position)
        SQLiteProvider.createLog(player, position, block, type)
    }

    fun deleteLogs(position: Position) = SQLiteProvider.deleteLogs(position)

    fun getLogs(x: Int, y: Int, z: Int, levelName: String): List<BlockLog> {
        return getLogs(Position(x.toDouble(), y.toDouble(), z.toDouble(), Server.getInstance().getLevelByName(levelName) ?: return listOf()))
    }

    fun getLogs(position: Position): List<BlockLog> {
        val result = SQLiteProvider.getLog(position)
        return result.sortedWith(Comparator { t, t2 -> if (t2.time - t.time > 0) 1 else 0 })
    }

    fun contains(logs: List<BlockLog>, compareWith: BlockLog.ActionType): Boolean {
        logs.forEach {
            if (it.type == compareWith) return true
        }
        return false
    }

    /* Player Data */
    fun createPlayerData(player: Player) = SQLiteProvider.createPlayerData(player)

    fun getPlayerData(player: Player): PlayerData = SQLiteProvider.getPlayerData(player)

    fun setPlayerData(playerData: PlayerData) {
        SQLiteProvider.setPlayerData(Server.getInstance().getPlayer(playerData.name) ?: return, playerData)
    }

}