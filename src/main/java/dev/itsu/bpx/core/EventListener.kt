package dev.itsu.bpx.core

import dev.itsu.bpx.api.BlockProtectXAPI
import dev.itsu.bpx.api.model.BlockLog
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.utils.TextFormat
import java.text.SimpleDateFormat

class EventListener : Listener {

    @EventHandler
    fun onTap(event: PlayerInteractEvent) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return

        if (DataManager.coQueue[event.player.name] == false) {
            BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_TAP)
            return
        }

        val blockLog = BlockProtectXAPI.getLog(event.block)

        if (blockLog.id == BlockLog.ID_NOTFOUND) {
            event.player.sendMessage("システム>>ログはありません。")
            return
        }

        event.player.sendMessage("""
            ${TextFormat.DARK_AQUA}BlockProtectX
            ${TextFormat.RESET}------------------------------
            §aユーザー §r${blockLog.owner}
            §aIP §r${blockLog.ip}
            §aDeviceID §r${blockLog.clientId}
            §a時間 §r${SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(blockLog.time)}
            §aブロック §r${blockLog.id}:${blockLog.blockDamage}
            §aワールド名 §r${blockLog.levelName}
            §a座標 §r(${blockLog.x}, ${blockLog.y}, ${blockLog.z})
            §aアクション §r${blockLog.type.text}
        """.trimIndent())
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_BREAK)
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_PLACE)
    }

}