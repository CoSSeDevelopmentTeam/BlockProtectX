package dev.itsu.bpx.api

import dev.itsu.bpx.api.model.BlockLog
import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.level.Location
import cn.nukkit.level.Position
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
            if (existsLog(position)) deleteLog(position)

            val sql = "INSERT INTO block_log (owner, ip, cid, time, b_id, b_damage, level, x, y, z, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setString(1, player.name)
            statement.setString(2, player.address)
            statement.setString(3, player.loginChainData.deviceId)
            statement.setString(4, System.currentTimeMillis().toString())
            statement.setInt(5, block.id)
            statement.setInt(6, block.damage)
            statement.setString(7, position.level.name)
            statement.setInt(8, position.floorX)
            statement.setInt(9, position.floorY)
            statement.setInt(10, position.floorZ)
            statement.setInt(11, type.id)

            statement.executeUpdate()
            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun deleteLog(position: Position) {
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

    fun getLog(position: Position): BlockLog {
        if (!existsLog(position)) return BlockLog.emptyLog()

        try {
            val sql = "SELECT * FROM block_log WHERE x = ? AND y = ? AND z = ? AND level = ?"
            val statement = connection.prepareStatement(sql)
            statement.queryTimeout = 50

            statement.setInt(1, position.floorX)
            statement.setInt(2, position.floorY)
            statement.setInt(3, position.floorZ)
            statement.setString(4, position.level.name)

            val result = statement.executeQuery()

            if (result.next()) {
                return BlockLog(
                        result.getInt("id"),
                        result.getString("owner"),
                        result.getString("ip"),
                        result.getString("cid"),
                        result.getString("time").toLong(),
                        result.getInt("b_id"),
                        result.getInt("b_damage"),
                        result.getString("level"),
                        result.getInt("x"),
                        result.getInt("y"),
                        result.getInt("z"),
                        BlockLog.ActionType.fromId(result.getInt("type"))

                ).also {
                    result.close()
                    statement.close()
                }
            }

            result.close()
            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return BlockLog.emptyLog()
    }

    fun existsLog(position: Position): Boolean {
        val sql = "SELECT * FROM block_log WHERE x = ? AND y = ? AND z = ? AND level = ?"
        val statement = connection.prepareStatement(sql)
        statement.queryTimeout = 50

        statement.setInt(1, position.floorX)
        statement.setInt(2, position.floorY)
        statement.setInt(3, position.floorZ)
        statement.setString(4, position.level.name)

        return statement.executeQuery().next().also {
            statement.close()
        }
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
                    "time TEXT NOT NULL," +
                    "b_id INTEGER NOT NULL," +
                    "b_damage INTEGER NOT NULL," +
                    "level TEXT NOT NULL," +
                    "x INTEGER NOT NULL," +
                    "y INTEGER NOT NULL," +
                    "z INTEGER NOT NULL," +
                    "type INTEGER NOT NULL" +
                    ")"
            )

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS player (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "first_played TEXT NOT NULL," +
                    "last_played TEXT NOT NULL," +
                    "login_count TEXT NOT NULL" +
                    ")"
            )

            statement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

}