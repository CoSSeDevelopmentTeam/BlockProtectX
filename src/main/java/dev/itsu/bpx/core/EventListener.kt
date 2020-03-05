package dev.itsu.bpx.core

import dev.itsu.bpx.api.BlockProtectXAPI
import dev.itsu.bpx.api.model.BlockLog
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.utils.TextFormat
import dev.itsu.bpx.api.model.PlayerData
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class EventListener : Listener {

    @EventHandler
    fun onTap(event: PlayerInteractEvent) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return

        if (DataManager.coQueue[event.player.name] != true) {
            BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_TAP)
            return
        } else {
            event.setCancelled()
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
        if (event.player.isOp || DataManager.exceptLevels.contains(event.block.level.name)) return
        if (event.player.isOp || !DataManager.protectedLevels.contains(event.block.level.name)) return

        val data = BlockProtectXAPI.getPlayerData(event.player)
        if (data.type == PlayerData.EditType.TYPE_UNEDITABLE) {
            event.player.sendMessage("システム>>このブロックは破壊できません。")
            event.setCancelled()
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_PLACE)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        BlockProtectXAPI.createPlayerData(event.player)

        val data = BlockProtectXAPI.getPlayerData(event.player)
        if (System.currentTimeMillis() - data.lastPlayed > TimeUnit.DAYS.toMillis(1)) {
            data.loginCount++
            if (data.loginCount >= DataManager.daysCount) {
                data.type = PlayerData.EditType.TYPE_EDITABLE
                event.player.sendMessage("システム>>ログイン日数が既定の日数を超えたため、§aブロックを破壊可能§rになりました！")
            }
            BlockProtectXAPI.setPlayerData(data)
        }
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val data = BlockProtectXAPI.getPlayerData(event.player)
        data.lastPlayed = System.currentTimeMillis()
        BlockProtectXAPI.setPlayerData(data)
    }

}