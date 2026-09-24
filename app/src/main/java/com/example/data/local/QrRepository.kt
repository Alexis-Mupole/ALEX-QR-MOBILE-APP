package com.example.data.local

import kotlinx.coroutines.flow.Flow

class QrRepository(private val qrRecordDao: QrRecordDao) {

    val allRecords: Flow<List<QrRecordEntity>> = qrRecordDao.getAllRecords()

    fun getRecordsByType(recordType: String): Flow<List<QrRecordEntity>> {
        return qrRecordDao.getRecordsByType(recordType)
    }

    suspend fun getRecordById(id: Long): QrRecordEntity? {
        return qrRecordDao.getRecordById(id)
    }

    suspend fun insertRecord(record: QrRecordEntity): Long {
        return qrRecordDao.insertRecord(record)
    }

    suspend fun deleteRecord(record: QrRecordEntity) {
        qrRecordDao.deleteRecord(record)
    }

    suspend fun deleteById(id: Long) {
        qrRecordDao.deleteById(id)
    }

    suspend fun clearByType(recordType: String) {
        qrRecordDao.clearByType(recordType)
    }

    suspend fun clearAll() {
        qrRecordDao.clearAll()
    }
}
