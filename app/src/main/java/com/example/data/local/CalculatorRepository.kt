package com.example.data.local

import kotlinx.coroutines.flow.Flow

class CalculatorRepository(private val dao: CalculatorDao) {
    fun observeActiveState(): Flow<CalculatorStateEntity?> = dao.observeActiveState()

    suspend fun getActiveStateOnce(): CalculatorStateEntity? = dao.getActiveStateOnce()

    suspend fun saveActiveState(entity: CalculatorStateEntity) {
        dao.upsertActiveState(entity)
    }

    suspend fun clearActiveState() {
        dao.clearActiveState()
    }

    fun observeSavedCalculations(): Flow<List<SavedCalculationEntity>> = dao.observeSavedCalculations()

    suspend fun saveSnapshot(item: SavedCalculationEntity) {
        dao.insertSavedCalculation(item)
    }

    suspend fun deleteSnapshot(id: Long) {
        dao.deleteSavedCalculation(id)
    }
}
