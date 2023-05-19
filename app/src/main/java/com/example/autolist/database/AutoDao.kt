package com.example.autolist.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.autolist.Auto
import java.util.*

@Dao
interface AutoDao {

    @Query("SELECT * FROM auto")
    fun getAutos(): LiveData<List<Auto>>

    @Query("SELECT * FROM auto WHERE id=(:id)")
    fun getAuto(id: UUID): LiveData<Auto?>

    @Update
    fun updateAuto(auto: Auto)

    @Insert
    fun addAuto(auto: Auto)

}