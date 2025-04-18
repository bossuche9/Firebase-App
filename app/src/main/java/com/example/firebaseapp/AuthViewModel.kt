package com.example.firebaseapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
     private val auth : FirebaseAuth = FirebaseAuth.getInstance()
     private val firestore  = FirebaseFirestore.getInstance()

     private val _authState = MutableLiveData<AuthState>()
     val authState: LiveData<AuthState> = _authState

     init {
          checkAuthStatus()
     }

     fun checkAuthStatus(){
          if(auth.currentUser == null){
               _authState.value = AuthState.Unauthenticated
          }else{
               _authState.value = AuthState.Authenticated
          }
     }

     fun login(email: String, password: String){
          if(email.isEmpty() || password.isEmpty()){
               _authState.value = AuthState.Error("Email or password can't be empty")
               return
          }

          _authState.value = AuthState.Loading
          auth.signInWithEmailAndPassword(email,password)
               .addOnCompleteListener{task->
                    if(task.isSuccessful){
                         _authState.value = AuthState.Authenticated
                    }else{
                         _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                    }
               }
     }

     fun signup(email: String, password: String){
          if(email.isEmpty() || password.isEmpty()){
               _authState.value = AuthState.Error("Email or password can't be empty")
               return
          }
          _authState.value = AuthState.Loading
          auth.createUserWithEmailAndPassword(email,password)
               .addOnCompleteListener{task->
                    if(task.isSuccessful){

                         val uid = auth.currentUser?.uid ?:return@addOnCompleteListener

                         val userData = mapOf(
                              "email" to email,
                              "createdAt" to FieldValue.serverTimestamp()
                         )

                         firestore.collection("users")
                              .document(uid)
                              .set(userData)
                              .addOnSuccessListener {
                                   _authState.value = AuthState.Authenticated
                              }
                              .addOnFailureListener{e ->
                                   _authState.value =AuthState.Error("Could not save user: ${e.message}")
                              }
                    }else{
                         _authState.value = AuthState.Error(task.exception?.message?:"Sign up Failed")
                    }
               }
     }

     fun signout(){
          auth.signOut()
          _authState.value = AuthState.Unauthenticated
     }
}

sealed class AuthState {
     object Authenticated : AuthState()
     object Unauthenticated : AuthState()
     object Loading: AuthState()
     data class Error(val message : String) : AuthState()
}