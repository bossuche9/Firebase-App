package com.example.firebaseapp.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firebaseapp.AuthViewModel

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier, navController,authViewModel)
        }
        composable("signup"){
            SignupPage(modifier, navController,authViewModel)
        }
        composable("home"){
            HomePage(modifier,navController,authViewModel)
        }
        composable("upload") {
            //PhotoUploadScreen()
        }
    })


}