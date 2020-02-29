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
        val type: ActionType
) {
    companion object {
        const val ID_NOTFOUND = -1
        fun emptyLog(): BlockLog = BlockLog(ID_NOTFOUND, "Unknown", "0.0.0.0", "0000-0000-0000-0000", 0, 0, 0, "Unknown", 0, 0, 0, ActionType.TYPE_UNKNOWN)
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
}