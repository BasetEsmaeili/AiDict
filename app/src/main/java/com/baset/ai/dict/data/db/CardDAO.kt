package com.baset.ai.dict.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: CardEntity)

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCard(id: Int): CardEntity

    @Query("SELECT * FROM cards WHERE id = :id")
    fun getCardFlow(id: Int): Flow<CardEntity>

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("SELECT * FROM cards")
    fun cardsFlow(): Flow<List<CardEntity>>
}
