package hr.ferit.ltoprek.aqsense.models

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore


class SharedAuthorizationRepository : AuthorizationRepository
{
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override suspend fun getCurrentUser():User? {
        val user: FirebaseUser = auth.currentUser?: return null
        try {
            val documentSnapshot: DocumentSnapshot = 
                firestore.collection("users").document(user.uid).get()
            val dbUser: User = if (documentSnapshot.exists){
                User(
                    id = documentSnapshot.id,
                    email = documentSnapshot.get("email")?:"",
                    name = documentSnapshot.get("name")?:""
                )
            } else
            {
                throw Exception("User document does not exist")
            }
            return dbUser
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun login(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password)
        } catch (e:Exception){
            throw e
        }
    }

    override suspend fun register(email: String, password: String, name: String) {
        try {
            val userCredential = auth.createUserWithEmailAndPassword(email, password)
            val user = userCredential.user
            if(user!=null)
            {
                val userData = mapOf(
                    "email" to email,
                    "name" to name
                )
                firestore.collection("users").document(user.uid).set(userData)
            } else {
                throw Exception("User creation succeeded, but user object is null")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun changePassword(password: String) {
        try {
            auth.currentUser?.updatePassword(password)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteUser(){
        try {
            val user = auth.currentUser
            firestore.collection("users").document(user?.uid?:"").delete()
            user?.delete()
        } catch (e: Exception){
            throw e
        }
    }
}