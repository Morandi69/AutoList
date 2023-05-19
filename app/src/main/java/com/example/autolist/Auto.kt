package com.example.autolist

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Auto (@PrimaryKey
                 val id: UUID = UUID.randomUUID(),
                 var brand: String = "",
                 var model: String = "",
                 var year:Int = 0,
                 var price: Int = 0)