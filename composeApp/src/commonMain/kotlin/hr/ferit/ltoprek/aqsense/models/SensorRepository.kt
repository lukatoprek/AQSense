package hr.ferit.ltoprek.aqsense.models

interface SensorRepository {
    suspend fun getSensors(): List<Sensor>

    suspend fun getSensorById(id: String): Sensor?

    suspend fun deleteSensor(id: String)
}