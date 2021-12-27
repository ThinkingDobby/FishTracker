package com.example.thinkingdobby.fishtracker.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface FishDao {
    @Query("SELECT * FROM Fish ORDER BY id")
    fun getAll(): LiveData<List<Fish>>

    @Query("SELECT * FROM Fish WHERE id = :num")
    fun getByFishNo(num: Int): LiveData<List<Fish>>

    @Query("UPDATE Fish SET fishName = :fishName, date = :date, location = :location, info = :info, count = :count, size = :size, imgOt = :imgOt, image = :image WHERE id = :id")
    fun updateByFishId(id: Long, fishName: String, date: String, location: String, info: String, count: Int, size: Int, imgOt: Int, image: ByteArray)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fish: Fish)

    @Delete
    fun delete(fish: Fish)

    @Query("DELETE FROM Fish")
    fun deleteAll()
}