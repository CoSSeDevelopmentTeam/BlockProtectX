package dev.itsu.bpx.api.model

data class PlayerData(
        val id: Int,
        val name: String,
        var firstPlayed: Long,
        var lastPlayed: Long,
        var loginCount: Int,
        var type: EditType
) {

    companion object {
        const val ID_NOTFOUND = -1
        fun emptyData(): PlayerData = PlayerData(ID_NOTFOUND, "Unknown", 0, 0, 0, EditType.TYPE_UNEDITABLE)
    }

    enum class EditType(val id: Int) {
        TYPE_EDITABLE(0),
        TYPE_UNEDITABLE(1);

        companion object {
            fun fromId(id: Int) = when(id) {
                0 -> TYPE_EDITABLE
                else -> TYPE_UNEDITABLE
            }
        }
    }
}