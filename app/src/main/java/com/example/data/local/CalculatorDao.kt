package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM calculator_state WHERE id = 1")
    fun observeActiveState(): Flow<CalculatorStateEntity?>

    @Query("SELECT * FROM calculator_state WHERE id = 1")
    suspend fun getActiveStateOnce(): CalculatorStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertActiveState(entity: CalculatorStateEntity)

    @Query("DELETE FROM calculator_state")
    suspend fun clearActiveState()

    @Query("SELECT * FROM saved_calculations ORDER BY savedAtMillis DESC")
    fun observeSavedCalculations(): Flow<List<SavedCalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCalculation(item: SavedCalculationEntity)

    @Query("DELETE FROM saved_calculations WHERE id = :id")
    suspend fun deleteSavedCalculation(id: Long)
}
