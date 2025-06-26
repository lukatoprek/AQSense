package hr.ferit.ltoprek.aqsense.models

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.GeoPoint
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import hr.ferit.ltoprek.aqsense.utilities.MeasurementTimestamp
import hr.ferit.ltoprek.aqsense.utilities.TimestampConverter.toKotlinInstant
import kotlinx.serialization.Serializable

@Serializable
data class MeasurementsDTO(
    val value: Double? = null,
    val time: Timestamp? = null
)

@Serializable
data class SensorDTO(
    val name: String? = null,
    val unitOfMeasurement: String? = null,
    val measurements: List<MeasurementsDTO>? = null,
    val ownerId: String? = null,
    val type: Long? = null,
    val coordinates: GeoPoint?
)


class SharedSensorRepository : SensorRepository
{
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override suspend fun getSensors(): List<Sensor> {
        val currentUser = auth.currentUser ?: return emptyList()

        try {
            val querySnapshot =
                firestore.collection("sensors").where { "ownerId" equalTo currentUser.uid }.get()

            return querySnapshot.documents.mapNotNull { doc ->
                try {
                    val sensorDto = doc.data<SensorDTO>()
                    val documentId = doc.id

                    val mappedMeasurements = sensorDto.measurements?.mapNotNull { m ->
                        if(m.value!=null && m.time!=null){
                            MeasurementTimestamp(
                                value = m.value,
                                time = m.time.toKotlinInstant()
                            )
                        } else {
                            throw Exception("Measurement value or time is null")
                        }
                    }?.toMutableList() ?: mutableListOf()

                    Sensor(
                        id = documentId,
                        name = sensorDto.name ?: "Unknown Sensor",
                        unitOfMeasurement = sensorDto.unitOfMeasurement ?: "N/A",
                        measurements = mappedMeasurements,
                        ownerId = sensorDto.ownerId ?: "Unknown Owner",
                        type = SensorType.fromDbValue(sensorDto.type?:0),
                        coordinates = sensorDto.coordinates
                    )
                } catch (e: Exception) {
                    throw Exception("Error deserializing document ${doc.id} to SensorDTO. Skipping", e)
                }
            }
        } catch (e: Exception) {
            throw SensorFetchException("Failed to fetch sensors for user ${currentUser.uid}; ${e.message}", e)
        }
    }

    override suspend fun getSensorById(id: String): Sensor {
        try {
            val document = firestore.collection("sensors")
                .document(id)
                .get()
            if(document.exists) {
                val sensorDto = document.data<SensorDTO>()
                val mappedMeasurements = sensorDto.measurements?.mapNotNull { m ->
                    if(m.value!=null && m.time!=null){
                        MeasurementTimestamp(
                            value = m.value,
                            time = m.time.toKotlinInstant()
                        )
                    } else {
                        throw Exception("Measurement value or time is null")
                    }
                }?.toMutableList() ?: mutableListOf()
                return Sensor(
                    id = document.id,
                    name = sensorDto.name ?: "Unknown Sensor",
                    unitOfMeasurement = sensorDto.unitOfMeasurement ?: "N/A",
                    measurements = mappedMeasurements,
                    ownerId = sensorDto.ownerId ?: "Unknown Owner",
                    type = SensorType.fromDbValue(sensorDto.type?:0),
                    coordinates = sensorDto.coordinates
                )
            } else{
                throw Exception("Sensor not found")
            }
        } catch(e: Exception){
            throw SensorFetchException("Failed to fetch sensor with id $id", e)
        }
    }

    override suspend fun deleteSensor(sensorId: String) {
        try {
            firestore.collection("sensors")
                .document(sensorId)
                .delete()
        } catch(e: Exception){
            throw SensorMutationException("Failed to delete sensor $sensorId", e)
        }
    }
}

class SensorFetchException(message: String, cause: Throwable? = null) : Exception(message, cause)
class SensorMutationException(message: String, cause: Throwable? = null) : Exception(message, cause)