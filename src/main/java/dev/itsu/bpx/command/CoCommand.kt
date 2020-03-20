package dev.itsu.bpx.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.command.simple.CommandPermission
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.utils.TextFormat
import dev.itsu.bpx.api.BlockProtectXAPI
import dev.itsu.bpx.api.model.BlockLog
import dev.itsu.bpx.api.model.PlayerData
import dev.itsu.bpx.command.model.CoData
import dev.itsu.bpx.core.DataManager
import java.text.SimpleDateFormat

class CoCommand : Command("co", "ブロックログコマンド。/coを実行後、対象のブロックをタップ", "/co") {

    init {
        this.permission = "everyone"
    }

    override fun execute(sender: CommandSender, s: String, args: Array<String>): Boolean {
        if (sender is ConsoleCommandSender) {
            sender.sendMessage("§aシステム§r>>ゲーム内から実行してください。")
            return false
        }

        val data = BlockProtectXAPI.getPlayerData(sender as Player)
        if (!sender.isOp && data.type == PlayerData.EditType.TYPE_UNEDITABLE) {
            sender.sendMessage("§aシステム§r>>このコマンドの実行にはブロックの編集権限が必要です。")
            return false
        }

        if (!DataManager.coQueue.containsKey(sender.name)) {
            DataManager.coQueue[sender.name] = CoData.emptyData()
            sender.sendMessage("§aシステム§r>>ブロックログをオンにしました。対象のブロックをタップするとログが表示されます。")

        } else {
            DataManager.coQueue.remove(sender.name)
            sender.sendMessage("§aシステム§r>>ブロックログをオフにしました。")
        }

        return true
    }

    companion object {

        fun process(e: PlayerInteractEvent) {
            val data = DataManager.coQueue[e.player.name]!!
            val logToDisplay: BlockLog?

            if (data.equals(e.block.floorX, e.block.floorY, e.block.floorZ, e.block.level.name)) {
                if (data.index >= data.data.size) data.index = 0
                logToDisplay = data.data[data.index]
                data.index++

            } else {
                val log = BlockProtectXAPI.getLogs(e.block).reversed()

                if (log.isEmpty()) {
                    e.player.sendMessage("§aシステム§r>>ログはありません。")
                    return
                }

                logToDisplay = log[0]

                data.x = e.block.floorX
                data.y = e.block.floorY
                data.z = e.block.floorZ
                data.levelName = e.block.level.name
                data.index = 1
                data.data = log
            }

            DataManager.coQueue[e.player.name] = data

            e.player.sendMessage("""
            ${TextFormat.RESET}------------------------------
            ${TextFormat.DARK_AQUA}BlockProtectX [${data.index}/${data.data.size}]
            ${TextFormat.RESET}別のブロックをタップすることでリセットできます。
            ${TextFormat.RESET}------------------------------
        """.trimIndent())

            e.player.sendMessage("""
            §aステータス ${logToDisplay.status.text}
            §aユーザー §r${logToDisplay.owner}
            §aIP §r${logToDisplay.ip}
            §aDeviceID §r${logToDisplay.clientId}
            §a時間 §r${SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(logToDisplay.time)}
            §aブロック (id:damage) §r${logToDisplay.id}:${logToDisplay.blockDamage}
            §aワールド名 §r${logToDisplay.levelName}
            §a座標 §r(${logToDisplay.x}, ${logToDisplay.y}, ${logToDisplay.z})
            §aアクション §r${logToDisplay.type.text}
            ${TextFormat.RESET}------------------------------
            """.trimIndent())
        }

    }

}