package com.cr4sh.nhlauncher.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ToolsDao {
    @Insert
    suspend fun insertTool(tool: ToolEntity)

    @Update
    suspend fun updateTool(tool: ToolEntity)

    @Query("UPDATE TOOLS SET FAVOURITE = :favorite WHERE NAME = :name")
    suspend fun updateToolFavorite(name: String, favorite: Int)

    @Query("UPDATE TOOLS SET USAGE = :usage WHERE NAME = :name")
    suspend fun updateToolUsage(name: String, usage: Int)

    @Query("UPDATE TOOLS SET CMD = :cmd WHERE NAME = :name")
    suspend fun updateToolCmd(name: String, cmd: String?)

    @Query("DELETE FROM TOOLS WHERE NAME = :toolName")
    suspend fun deleteTool(toolName: String)

    @Query("SELECT * FROM TOOLS WHERE NAME = :name")
    suspend fun getToolByName(name: String): ToolEntity?
}
