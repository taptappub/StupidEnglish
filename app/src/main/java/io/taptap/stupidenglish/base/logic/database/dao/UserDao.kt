package io.taptap.stupidenglish.base.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.taptap.stupidenglish.base.logic.database.dto.UserDto
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    //-----------------User-----------------

    @Insert
    suspend fun insertUser(user: UserDto): Long

    @Update
    suspend fun updateUser(user: UserDto)

    @Query(
        """
        DELETE FROM UserTable 
        WHERE UserTable.id = :userId
        """
    )
    suspend fun deleteUser(userId: Long): Int

    @Query(
        """
        SELECT *
        FROM UserTable
        LIMIT 1
        """
    )
    suspend fun getUserDto(): UserDto?

    @Query(
        """
        SELECT *
        FROM UserTable
        """
    )
    fun observeUser(): Flow<List<UserDto>>
}
