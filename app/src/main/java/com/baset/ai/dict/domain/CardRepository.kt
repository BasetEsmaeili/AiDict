package com.baset.ai.dict.domain

import com.baset.ai.dict.domain.entity.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    suspend fun insertCard(card: Card)
    suspend fun getCard(id: Int): Card
    fun getCardFlow(id: Int): Flow<Card>
    suspend fun deleteCard(card: Card)
    suspend fun updateCard(card: Card)
    fun cardsFlow(): Flow<List<Card>>
}
