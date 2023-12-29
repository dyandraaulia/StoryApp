package id.my.storyapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class ListStory(
    @PrimaryKey
    @field: ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "photoUrl")
    val photoUrl: String? = null,

    @field:ColumnInfo(name = "createdAt")
    val createdAt: String? = null,

    @field:ColumnInfo(name = "name")
    val name: String? = null,

    @field:ColumnInfo(name = "description")
    val description: String? = null,

    @field:ColumnInfo(name = "lon")
    val lon: Double? = null,

    @field:ColumnInfo(name = "lat")
    val lat: Double? = null
)