package dev.itsu.bpx.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import dev.itsu.bpx.api.BlockProtectXAPI
import dev.itsu.bpx.core.DataManager

class CoCommand : Command("co", "ブロックログコマンド。/coを実行後、対象のブロックをタップ", "/co") {

    override fun execute(sender: CommandSender, s: String, args: Array<String>): Boolean {
        if (sender is ConsoleCommandSender) {
            sender.sendMessage("§aシステム§r>>ゲーム内から実行してください。")
            return false
        }

        val data = BlockProtectXAPI.getPlayerData(sender as Player)
        if (!sender.isOp && data.loginCount < DataManager.daysCount) {
            sender.sendMessage("§aシステム§r>>このコマンドの実行にはブロックの編集権限が必要です。")
            return false
        }

        if (DataManager.coQueue[sender.name] == null || !DataManager.coQueue[sender.name]!!) {
            DataManager.coQueue[sender.name] = true
            sender.sendMessage("§aシステム§r>>ブロックログをオンにしました。対象のブロックをタップするとログが表示されます。")

        } else {
            DataManager.coQueue[sender.name] = false
            sender.sendMessage("§aシステム§r>>ブロックログをオフにしました。")
        }

        return true
    }

}