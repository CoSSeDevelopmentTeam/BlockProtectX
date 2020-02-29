package dev.itsu.bpx.core

import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import cn.nukkit.utils.Utils
import dev.itsu.bpx.command.CoCommand
import java.io.File

class Main : PluginBase() {

    override fun onEnable() {
        loadConfig()
        server.commandMap.register("co", CoCommand())
        server.pluginManager.registerEvents(EventListener(), this)
        logger.info("Enabled.")
    }

    private fun loadConfig() {
        val file = File("./plugins/BlockProtectX/Config.yml")
        val parent = File("./plugins/BlockProtectX/")
        val config = Config(file, Config.YAML)

        if (!parent.exists()) parent.mkdir()

        if (!file.exists()) {
            Utils.writeFile(file, javaClass.classLoader.getResourceAsStream("Config.yml"))
        }

        config.load(file.absolutePath)

        DataManager.daysCount = config.getInt("LoginDaysCount")
    }
}