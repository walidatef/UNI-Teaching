package com.uni.uniteaching.data

import android.content.SharedPreferences

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.uni.uniteaching.classes.Permission
import com.uni.uniteaching.classes.user.UserTeaching
import com.uni.uniteaching.data.di.FireStoreTable
import com.uni.uniteaching.data.di.PermissionsRequired
import com.uni.uniteaching.data.di.SharedPreferencesTable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database:FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson:Gson
) : AuthRepository {

    override suspend fun updateUserInfo(userStudent: UserTeaching, result: (Resource<String>) ->Unit ) {
            val document=database.collection(FireStoreTable.user)
                .document(userStudent.userId)

            document.set(userStudent)
                .addOnSuccessListener {
                    result.invoke(
                        Resource.Success("user date updated successfully")
                    )
                }
                .addOnFailureListener{
                    result.invoke(
                        Resource.Failure(
                            it.localizedMessage
                        )
                    )
                }
    }

    override val user:FirebaseUser?
    get() =firebaseAuth.currentUser

    override suspend fun register(
        email: String,
        password: String,
        userStudent: UserTeaching,
        result: (Resource<String>) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    GlobalScope.launch {
                        val userId=it.result.user?.uid?:""
                        userStudent.userId=userId
                        updateUserInfo(userStudent){ state->
                            when(state){
                                Resource.Loading -> result.invoke(Resource.Loading)
                                is Resource.Success -> {
                                    storeSession(userId,userStudent) { user->
                                        if (user == null) {
                                            result.invoke(Resource.Failure("user created successfully but session did not stored"))
                                        } else {

                                           result.invoke(
                                                    Resource.Success(
                                                        "user created successfully but you need to check you permission with admin"
                                                    ))
                                            }
                                                }
                                            }
                                is Resource.Failure ->{result.invoke(Resource.Failure(state.exception))}
                            }
                        }
                    }
                }else{
                    result.invoke(
                        Resource.Failure(
                            it.exception.toString()
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun logOut(result: () -> Unit) {
        firebaseAuth.signOut()
        result.invoke()
        appPreferences.edit().putString(SharedPreferencesTable.user_session,null).apply()

    }


    override fun storeSession(id :String, user : UserTeaching, result :(UserTeaching?)-> Unit){
       val document =database.collection(FireStoreTable.user)
           .document(user.userId)
        document
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userStudent = it.result.toObject(UserTeaching::class.java)
                    if (userStudent != null) {
                        setSession(userStudent)
                    }
                    result.invoke(userStudent)
                }else{
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }
    override suspend fun logIn(
        email: String,
        password: String,
        result: (Resource<String>) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                result.invoke(
                    Resource.Success("user date updated successfully")
                )
            }else{
                Resource.Failure(
                    it.exception.toString()
                )
            }}
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }


    }


    override fun getSessionStudent(result: (UserTeaching?) -> Unit) {
        val user = appPreferences.getString(SharedPreferencesTable.user_session,null)
        if (user==null){
            result.invoke(null)
        }else{
            val userStudent = gson.fromJson(user, UserTeaching::class.java)
            result.invoke(userStudent)
        }
    }
    override  suspend fun getUser(id :String, result:(Resource<UserTeaching?>) -> Unit) {
        val docRef =  database.collection(FireStoreTable.user)
            .document(id)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }
            result.invoke(Resource.Success(snapshot?.toObject(UserTeaching::class.java)))
        }
    }

    override fun setSession(user: UserTeaching) {
        appPreferences.edit().putString(SharedPreferencesTable.user_session,gson.toJson(user)).apply()
    }
}