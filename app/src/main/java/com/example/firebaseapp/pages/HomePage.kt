package com.example.firebaseapp.pages


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.firebaseapp.AuthState
import com.example.firebaseapp.AuthViewModel
import com.example.firebaseapp.R
import com.example.firebaseapp.Util.StorageUtil
import com.example.firebaseapp.ui.theme.FirebaseAppTheme


@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    var uri by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
        }
    )

    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = stringResource(R.string.home_page), fontSize = 32.sp)


        Button(onClick = {
            singlePhotoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text(stringResource(R.string.choose_image))

        }

        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = stringResource(R.string.sign_out))
        }
            AsyncImage(model = uri, contentDescription = null, modifier = Modifier)

            Button(onClick = {
                uri?.let {
                    StorageUtil.uploadToStorage(uri = it, context = context, type = context.getString(
                        R.string.image
                    ))
                }

            }) {
                Text(stringResource(R.string.upload))
            }
        }
    }





@Preview
@Composable
fun HomepagePreview(){
    FirebaseAppTheme {
        //HomePage(modifier = Modifier, NavController(),authViewModel = AuthViewModel())
    }
}


