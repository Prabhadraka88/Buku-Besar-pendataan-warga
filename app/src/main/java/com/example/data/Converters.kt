package com.example.data

import androidx.room.TypeConverter
import com.example.model.CitizenCategory

class Converters {
    @TypeConverter
    fun fromCategory(category: CitizenCategory): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): CitizenCategory {
        return CitizenCategory.fromString(value)
    }
}
