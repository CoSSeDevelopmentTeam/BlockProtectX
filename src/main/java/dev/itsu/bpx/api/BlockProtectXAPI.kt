package dev.itsu.bpx.api

import dev.itsu.bpx.api.model.BlockLog
import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.level.Position

object BlockProtectXAPI {

    fun createLog(player: Player, position: Position, block: Block, type: BlockLog.ActionType) = SQLiteProvider.createLog(player, position, block, type)
    fun deleteLog(position: Position) = SQLiteProvider.deleteLog(position)
    fun getLog(x: Int, y: Int, z: Int, levelName: String): BlockLog = getLog(Position(x.toDouble(), y.toDouble(), z.toDouble(), Server.getInstance().getLevelByName(levelName)))
    fun getLog(position: Position): BlockLog = SQLiteProvider.getLog(position)

}