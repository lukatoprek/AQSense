package hr.ferit.ltoprek.aqsense.models

interface AuthorizationRepository {
    suspend fun getCurrentUser(): User?
    suspend fun login(email: String, password: String)
    suspend fun register(email: String, password: String, name: String)
    suspend fun logout()
    suspend fun changePassword(password: String)
    suspend fun deleteUser()
}