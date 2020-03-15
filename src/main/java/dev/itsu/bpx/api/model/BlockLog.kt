package dev.itsu.bpx.api.model

data class BlockLog(
        val id: Int,
        val owner: String,
        val ip: String,
        val clientId: String,
        val time: Long,
        val blockId: Int,
        val blockDamage: Int,
        val levelName: String,
        val x: Int,
        val y: Int,
        val z: Int,
        val type: ActionType,
        var status: BlockStatus
) {
    companion object {
        const val ID_NOTFOUND = -1
        fun emptyLog(): BlockLog = BlockLog(ID_NOTFOUND, "UNKNOWN", "0.0.0.0", "0000-0000-0000-0000", 0, 0, 0, "UNKNOWN", 0, 0, 0, ActionType.TYPE_UNKNOWN, BlockStatus.STATUS_UNKNOWN)
    }



    enum class ActionType(val id: Int, val text: String) {
        TYPE_PLACE(0, "設置"),
        TYPE_TAP(1, "タップ"),
        TYPE_BREAK(2, "破壊"),
        TYPE_UNKNOWN(3, "不明");

        companion object {
            fun fromId(id: Int): ActionType = when (id) {
                0 -> TYPE_PLACE
                1 -> TYPE_TAP
                2 -> TYPE_BREAK
                else -> TYPE_UNKNOWN
            }
        }
    }

    enum class BlockStatus(val id: Int, val text: String) {
        STATUS_LATEST(0, "§b最新のブロック"),
        STATUS_PAST(1, "§e過去のブロック"),
        STATUS_UNKNOWN(2, "§d不明");

        companion object {
            fun fromId(id: Int): BlockStatus = when(id) {
                0 -> STATUS_LATEST
                1 -> STATUS_PAST
                else -> STATUS_UNKNOWN
            }
        }
    }

    override fun toString(): String {
        return "BlockLog(id=$id, owner='$owner', ip='$ip', clientId='$clientId', time=$time, blockId=$blockId, blockDamage=$blockDamage, levelName='$levelName', x=$x, y=$y, z=$z, type=$type)"
    }
}