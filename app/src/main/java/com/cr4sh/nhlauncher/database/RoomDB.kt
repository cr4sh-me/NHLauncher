package com.cr4sh.nhlauncher.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TOOLS")
data class ToolEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val system: Int,
    val category: String?,
    val favourite: Int,
    val name: String?,
    val descriptionEN: String?,
    val descriptionPL: String?,
    val cmd: String?,
    val icon: String?,
    val usage: Int
)
