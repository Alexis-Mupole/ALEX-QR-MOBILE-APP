package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrRecordDao {

    @Query("SELECT * FROM qr_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<QrRecordEntity>>

    @Query("SELECT * FROM qr_records WHERE recordType = :recordType ORDER BY timestamp DESC")
    fun getRecordsByType(recordType: String): Flow<List<QrRecordEntity>>

    @Query("SELECT * FROM qr_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Long): QrRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: QrRecordEntity): Long

    @Delete
    suspend fun deleteRecord(record: QrRecordEntity)

    @Query("DELETE FROM qr_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM qr_records WHERE recordType = :recordType")
    suspend fun clearByType(recordType: String)

    @Query("DELETE FROM qr_records")
    suspend fun clearAll()
}
