package com.baset.ai.dict.data

import com.baset.ai.dict.data.db.CardDAO
import com.baset.ai.dict.data.db.toCard
import com.baset.ai.dict.data.db.toCardEntity
import com.baset.ai.dict.domain.CardRepository
import com.baset.ai.dict.domain.entity.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CardRepositoryImpl(
    private val cardDAO: CardDAO
) : CardRepository {
    override suspend fun insertCard(card: Card) {
        return cardDAO.insertCard(card.toCardEntity())
    }

    override suspend fun getCard(id: Int): Card {
        return cardDAO.getCard(id).toCard()
    }

    override fun getCardFlow(id: Int): Flow<Card> {
        return cardDAO.getCardFlow(id).map { it.toCard() }
    }

    override suspend fun deleteCard(card: Card) {
        return cardDAO.deleteCard(card.toCardEntity())
    }

    override suspend fun updateCard(card: Card) {
        return cardDAO.updateCard(card.toCardEntity())
    }

    override fun cardsFlow(): Flow<List<Card>> {
        return cardDAO.cardsFlow().map { list -> list.map { it.toCard() } }
    }
}
