package dev.itsu.bpx.core

import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import cn.nukkit.utils.Utils
import dev.itsu.bpx.api.BlockProtectXAPI
import dev.itsu.bpx.command.CoCommand
import java.io.File
import java.util.*

class Main : PluginBase() {

    override fun onEnable() {
        loadConfig()
        server.commandMap.register("co", CoCommand())
        server.pluginManager.registerEvents(EventListener(), this)

        val levels = (File("./worlds").list() ?: arrayOf()).toList()
        levels.forEach {
            BlockProtectXAPI.createLevelData(it)
        }

        BlockProtectXAPI.getLevels().forEach {
            if (!levels.contains(it.name)) BlockProtectXAPI.deleteLogsByLevelName(it.name)
        }

        logger.info("Enabled.")
    }

    private fun loadConfig() {
        File("./plugins/BlockProtectX/").also {
            if (!it.exists()) it.mkdir()
        }

        val file = File("./plugins/BlockProtectX/Config.yml").also {
            if (!it.exists()) {
                it.createNewFile()
                Utils.writeFile(it, javaClass.classLoader.getResourceAsStream("Config.yml"))
            }
        }

        val config = Config(file, Config.YAML)
        config.load(file.absolutePath)

        DataManager.daysCount = config.getInt("LoginDaysCount")
        ((config.getList("ExceptLevels") ?: return) as List<String>).forEach {
            DataManager.exceptLevels.add(it)
        }
        ((config.getList("ProtectedLevels") ?: return) as List<String>).forEach {
            DataManager.protectedLevels.add(it)
        }
    }
}