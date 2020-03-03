package dev.itsu.bpx.core

import cn.nukkit.plugin.LibraryLoader
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import cn.nukkit.utils.Utils
import dev.itsu.bpx.command.CoCommand
import java.io.File
import javax.xml.crypto.Data

class Main : PluginBase() {

    override fun onEnable() {
        loadConfig()
        server.commandMap.register("co", CoCommand())
        server.pluginManager.registerEvents(EventListener(), this)
        logger.info("Enabled.")
    }

    private fun loadConfig() {
        val file = File("./plugins/BlockProtectX/Config.yml").also {
            if (!it.exists()) Utils.writeFile(it, javaClass.classLoader.getResourceAsStream("Config.yml"))
        }

        File("./plugins/BlockProtectX/").also {
            if (it.exists()) it.mkdir()
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