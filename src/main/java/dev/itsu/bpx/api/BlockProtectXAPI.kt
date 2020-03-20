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
        if (type == BlockLog.ActionType.TYPE_PLACE) {
            logs.forEach {
                if (it.status != BlockLog.BlockStatus.STATUS_PAST) {
                    it.status = BlockLog.BlockStatus.STATUS_PAST
                    updateLog(it)
                }
            }
        }
        SQLiteProvider.createLog(player, position, block, type)
    }

    fun deleteLogsByPosition(position: Position) = SQLiteProvider.deleteLogs(position)

    fun deleteLogsByLevelName(levelName: String) = SQLiteProvider.deleteLogsByLevelName(levelName)

    fun getLogs(x: Int, y: Int, z: Int, levelName: String): List<BlockLog> {
        return getLogs(Position(x.toDouble(), y.toDouble(), z.toDouble(), Server.getInstance().getLevelByName(levelName) ?: return listOf()))
    }

    fun getLogs(position: Position): List<BlockLog> {
        val result = SQLiteProvider.getLog(position)
        return result.sortedWith(Comparator { t, t2 -> if (t2.time - t.time > 0) 0 else 1 })
    }

    fun updateLog(blockLog: BlockLog) = SQLiteProvider.updateBlockLog(blockLog)

    fun contains(logs: List<BlockLog>, compareWith: BlockLog.ActionType): Boolean {
        logs.forEach {
            if (it.status == BlockLog.BlockStatus.STATUS_LATEST && it.type == compareWith) return true
        }
        return false
    }

    /* Player Data */
    fun createPlayerData(player: Player) = SQLiteProvider.createPlayerData(player)

    fun getPlayerData(player: Player): PlayerData = SQLiteProvider.getPlayerData(player)

    fun setPlayerData(playerData: PlayerData) {
        SQLiteProvider.setPlayerData(Server.getInstance().getPlayer(playerData.name) ?: return, playerData)
    }

    /* Level Data */
    fun createLevelData(levelName: String) = SQLiteProvider.createLevelData(levelName)

    fun existsLevel(levelName: String) = SQLiteProvider.existsLevel(levelName)

    fun getLevels() = SQLiteProvider.getLevels()

}