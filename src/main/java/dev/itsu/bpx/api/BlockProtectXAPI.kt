package dev.itsu.bpx.api

import dev.itsu.bpx.api.model.BlockLog
import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.level.Position
import dev.itsu.bpx.api.model.PlayerData

object BlockProtectXAPI {

    /* Block Logger */
    fun createLog(player: Player, position: Position, block: Block, type: BlockLog.ActionType) = SQLiteProvider.createLog(player, position, block, type)

    fun deleteLog(position: Position) = SQLiteProvider.deleteLog(position)

    fun getLog(x: Int, y: Int, z: Int, levelName: String): BlockLog {
        return getLog(Position(x.toDouble(), y.toDouble(), z.toDouble(), Server.getInstance().getLevelByName(levelName) ?: return BlockLog.emptyLog()))
    }

    fun getLog(position: Position): BlockLog = SQLiteProvider.getLog(position)

    /* Player Data */
    fun createPlayerData(player: Player) = SQLiteProvider.createPlayerData(player)

    fun getPlayerData(player: Player): PlayerData = SQLiteProvider.getPlayerData(player)

    fun setPlayerData(playerData: PlayerData) {
        SQLiteProvider.setPlayerData(Server.getInstance().getPlayer(playerData.name) ?: return, playerData)
    }

}