package com.example.thinkingdobby.fishtracker.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface FishDao {
    @Query("SELECT * FROM Fish ORDER BY fishNo")
    fun getAll(): LiveData<List<Fish>>

    @Query("SELECT * FROM Fish WHERE fishNo = :num")
    fun getByFishNo(num: Int): LiveData<List<Fish>>

    @Query("UPDATE Fish Set image = :newImage WHERE fishNo = :num")
    fun updateByFishNo(num: Int, newImage: ByteArray)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fish: Fish)

    @Delete
    fun delete(fish: Fish)
}