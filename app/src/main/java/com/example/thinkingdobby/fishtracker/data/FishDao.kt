package com.example.thinkingdobby.fishtracker.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Query

@Dao
interface FishDao {
    @Query("SELECT * FROM Fish ORDER BY fishNo")
    fun getAll(): LiveData<List<Fish>>

    @Query("SELECT * FROM Fish WHERE fishNo = :num")
    fun getByFishNo(num: Int): LiveData<List<Fish>>

    @Query("UPDATE Fish Set image = :newImage WHERE fishNo = :num")
    fun updateByFishNo(num: Int, newImage: ByteArray)

    @Delete
    fun delete(fish: Fish)
}