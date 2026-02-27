package com.example.beeranking.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.beeranking.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE id = :userId")
    fun getUserById(userId: String): User?

    @Query("SELECT * FROM User WHERE id = :userId")
    fun getUserByIdLiveData(userId: String): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)
}
