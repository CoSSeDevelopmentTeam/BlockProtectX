package dev.itsu.bpx.api

import dev.itsu.bpx.api.model.BlockLog
import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.level.Position
import dev.itsu.bpx.api.model.LevelData
import dev.itsu.bpx.api.model.PlayerData
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object SQLiteProvider {

    private lateinit var connection: Connection

    init {
        connectSQL()
    }

    fun createLog(player: Player, position: Position, block: Block, type: BlockLog.ActionType) {
        try {
            val sql = "INSERT INTO block_log (owner, ip, cid, time, b_id, b_damage, level, x, y, z, type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setString(1, player.name)
            statement.setString(2, player.address)
            statement.setString(3, player.loginChainData.deviceId)
            statement.setLong(4, System.currentTimeMillis())
            statement.setInt(5, block.id)
            statement.setInt(6, block.damage)
            statement.setString(7, position.level.name)
            statement.setInt(8, position.floorX)
            statement.setInt(9, position.floorY)
            statement.setInt(10, position.floorZ)
            statement.setInt(11, type.id)
            statement.setInt(12, BlockLog.BlockStatus.STATUS_LATEST.id)

            statement.executeUpdate()
            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun deleteLogs(position: Position) {
        if (!existsLog(position)) return
        //if (getLog(position).owner != player.name) return

        try {
            val sql = "DELETE FROM block_log WHERE x = ? AND y = ? AND z = ? AND level = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setInt(1, position.floorX)
            statement.setInt(2, position.floorY)
            statement.setInt(3, position.floorZ)
            statement.setString(4, position.level.name)

            statement.executeUpdate()
            statement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun deleteLogsByLevelName(levelName: String) {
        try {
            val sql = "DELETE FROM block_log WHERE level = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setString(1, levelName)

            statement.executeUpdate()
            statement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getLog(position: Position): List<BlockLog> {
        val logs = mutableListOf<BlockLog>()

        if (!existsLog(position)) return logs

        try {
            val sql = "SELECT * FROM block_log WHERE x = ? AND y = ? AND z = ? AND level = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setInt(1, position.floorX)
            statement.setInt(2, position.floorY)
            statement.setInt(3, position.floorZ)
            statement.setString(4, position.level.name)

            val result = statement.executeQuery()

            while (result.next()) {
                logs.add(BlockLog(
                        result.getInt("id"),
                        result.getString("owner"),
                        result.getString("ip"),
                        result.getString("cid"),
                        result.getLong("time"),
                        result.getInt("b_id"),
                        result.getInt("b_damage"),
                        result.getString("level"),
                        result.getInt("x"),
                        result.getInt("y"),
                        result.getInt("z"),
                        BlockLog.ActionType.fromId(result.getInt("type")),
                        BlockLog.BlockStatus.fromId(result.getInt("status"))
                ))
            }

            result.close()
            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return logs
    }

    fun existsLog(position: Position): Boolean {
        return existsLog(position.floorX, position.floorY, position.floorZ, position.level.name)
    }

    fun existsLog(x: Int, y: Int, z: Int, levelName: String): Boolean {
        try {
            val sql = "SELECT * FROM block_log WHERE x = ? AND y = ? AND z = ? AND level = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setInt(1, x)
            statement.setInt(2, y)
            statement.setInt(3, z)
            statement.setString(4, levelName)

            return statement.executeQuery().next().also {
                statement.close()
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return true
    }

    fun updateBlockLog(blockLog: BlockLog) {
        if (!existsLog(blockLog.x, blockLog.y, blockLog.z, blockLog.levelName)) return

        try {
            val sql = "UPDATE block_log SET status = ? WHERE id = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setInt(1, blockLog.status.id)
            statement.setInt(2, blockLog.id)

            statement.executeUpdate()
            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun createPlayerData(player: Player) {
        if (existsPlayer(player)) return

        try {
            val sql = "INSERT INTO player (name, first_played, last_played, login_count, type) VALUES (?, ?, ?, ?, ?)"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setString(1, player.name)
            statement.setLong(2, System.currentTimeMillis())
            statement.setLong(3, System.currentTimeMillis())
            statement.setInt(4, 0)
            statement.setInt(5, PlayerData.EditType.TYPE_UNEDITABLE.id)

            statement.executeUpdate()
            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getPlayerData(player: Player): PlayerData {
        if (!existsPlayer(player)) return PlayerData.emptyData()

        try {
            val sql = "SELECT * FROM player WHERE name = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50
            statement.setString(1, player.name)

            val result = statement.executeQuery()
            if (result.next()) {
                return PlayerData(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getLong("first_played"),
                        result.getLong("last_played"),
                        result.getInt("login_count"),
                        PlayerData.EditType.fromId(result.getInt("type"))
                ).also {
                    statement.close()
                }
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return PlayerData.emptyData()
    }

    fun setPlayerData(player: Player, playerData: PlayerData) {
        if (!existsPlayer(player)) return

        try {
            val sql = "UPDATE player SET last_played = ?, login_count = ?, type = ? WHERE name = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setLong(1, playerData.lastModified)
            statement.setInt(2, playerData.loginCount)
            statement.setInt(3, playerData.type.id)
            statement.setString(4, player.name)

            statement.executeUpdate()
            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun existsPlayer(player: Player): Boolean {
        try {
            val sql = "SELECT * FROM player WHERE name = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setString(1, player.name)

            return statement.executeQuery().next().also {
                statement.close()
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return true
    }

    fun createLevelData(levelName: String) {
        if (existsLevel(levelName)) return

        try {
            val sql = "INSERT INTO mcbe_level (name) VALUES (?)"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setString(1, levelName)

            statement.executeUpdate()
            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun existsLevel(levelName: String): Boolean {
        try {
            val sql = "SELECT * FROM mcbe_level WHERE name = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setString(1, levelName)

            return statement.executeQuery().next().also {
                statement.close()
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return true
    }

    fun getLevels(): List<LevelData> {
        val levels = mutableListOf<LevelData>()
        try {
            val sql = "SELECT * FROM mcbe_level"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            val result = statement.executeQuery()
            while (result.next()) {
                levels.add(LevelData(
                        result.getInt("id"),
                        result.getString("name")
                ))
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return levels
    }

    private fun connectSQL() {
        try {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection("jdbc:sqlite:" + "./plugins/BlockProtectX/DataDB.db")

            val statement = connection.createStatement()
            statement.queryTimeout = 50

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS block_log (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "owner TEXT NOT NULL," +
                    "ip TEXT NOT NULL," +
                    "cid TEXT NOT NULL," +
                    "time LONG NOT NULL," +
                    "b_id INTEGER NOT NULL," +
                    "b_damage INTEGER NOT NULL," +
                    "level TEXT NOT NULL," +
                    "x INTEGER NOT NULL," +
                    "y INTEGER NOT NULL," +
                    "z INTEGER NOT NULL," +
                    "type INTEGER NOT NULL," +
                    "status INTEGER NOT NULL" +
                    ")"
            )

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS player (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "first_played LONG NOT NULL," +
                    "last_played LONG NOT NULL," +
                    "login_count INT NOT NULL," +
                    "type INT NOT NULL" +
                    ")"
            )

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS mcbe_level (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL" +
                    ")"
            )

            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

}