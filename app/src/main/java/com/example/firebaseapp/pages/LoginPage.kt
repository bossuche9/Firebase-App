package com.example.firebaseapp.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebaseapp.AuthState
import com.example.firebaseapp.AuthViewModel
import com.example.firebaseapp.R

@Composable
fun LoginPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val passwordVisible by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()

    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.login_page), fontSize = 32.sp)

        Spacer(modifier = Modifier.height((16.dp)))

        OutlinedTextField(
            value = email,
            onValueChange = {
            email = it
        },
            label = {
                Text(text = stringResource(R.string.email))
            }
        )

        Spacer(modifier = Modifier.height((16.dp)))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = stringResource(R.string.password))
            },

            visualTransformation = if(passwordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

/*  trailingIcon = {
      IconButton(onClick = {passwordVisible = !passwordVisible}) {
          val icon = if (passwordVisible)
              Icons.Filled.Visibility
          else
              Icons.Filled.VisibilityOff
          Icon(imageVector = icon,
              contentDescription = if(passwordVisible) "Hide password" else "Show password")
      }
  } */
)

Spacer(modifier = Modifier.height((16.dp)))

Button(onClick = {
  authViewModel.login(email,password)
},
  enabled = authState.value != AuthState.Loading
){
  Text(text = stringResource(R.string.login))
}

Spacer(modifier = Modifier.height((8.dp)))

TextButton(onClick = {
  navController.navigate("signup")
}) {
  Text(text = stringResource(R.string.dont_have_an_account_signup))
}
}
}