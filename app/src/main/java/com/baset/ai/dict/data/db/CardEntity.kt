package com.baset.ai.dict.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baset.ai.dict.domain.entity.Card

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val front: String,
    val back: String
)

fun Card.toCardEntity(): CardEntity {
    return CardEntity(
        id = id,
        front = front,
        back = back
    )
}

fun CardEntity.toCard(): Card {
    return Card(
        id = id,
        front = front,
        back = back
    )
}
