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
import dev.itsu.bpx.command.CoCommand
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EventListener : Listener {

    @EventHandler
    fun onTap(event: PlayerInteractEvent) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return

        if (!DataManager.coQueue.containsKey(event.player.name)) {
            BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_TAP)
            return
        } else {
            event.setCancelled()
        }

        CoCommand.process(event)
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        if (event.player.isOp || DataManager.exceptLevels.contains(event.block.level.name)) {
            BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_BREAK)
            return
        }

        if (DataManager.protectedLevels.contains(event.block.level.name)) {
            BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_BREAK)
            event.setCancelled()
            return
        }

        val data = BlockProtectXAPI.getLogs(event.block)
        if (!BlockProtectXAPI.contains(data, BlockLog.ActionType.TYPE_PLACE)) {
            BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_BREAK)
            return
        }

        val player = BlockProtectXAPI.getPlayerData(event.player)
        var placedByMe = false
        data.forEach {
            if (it.type == BlockLog.ActionType.TYPE_PLACE && it.owner == event.player.name) placedByMe = true
        }

        if (!placedByMe && player.type == PlayerData.EditType.TYPE_UNEDITABLE) {
            event.player.sendMessage("§aシステム§r>>このブロックは破壊できません。")
            event.setCancelled()
        }

        BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_BREAK)
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        if (event.player.isOp || DataManager.protectedLevels.contains(event.block.level.name)) event.setCancelled()
        BlockProtectXAPI.createLog(event.player, event.block, event.block, BlockLog.ActionType.TYPE_PLACE)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        BlockProtectXAPI.createPlayerData(event.player)

        val data = BlockProtectXAPI.getPlayerData(event.player)
        val today = Calendar.getInstance().let {
            val c = Calendar.getInstance()
            it.clear()
            it.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            it.timeInMillis
        }

        if (data.lastModified < today) {
            data.loginCount++
            data.lastModified = System.currentTimeMillis()
            if (data.loginCount >= DataManager.daysCount) {
                data.type = PlayerData.EditType.TYPE_EDITABLE
                event.player.sendMessage("§aシステム§r>>通算ログイン日数が既定の日数を超えたため、§aブロックを破壊可能§rになりました！")
            }
            BlockProtectXAPI.setPlayerData(data)
        }
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val data = BlockProtectXAPI.getPlayerData(event.player)
        data.lastModified = System.currentTimeMillis()
        BlockProtectXAPI.setPlayerData(data)
    }

}