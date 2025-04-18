package com.example.firebaseapp.pages


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.firebaseapp.AuthState
import com.example.firebaseapp.AuthViewModel
import android.Manifest
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.compose.rememberNavController
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

    var isCameraImage by remember { mutableStateOf(false) }

    var uri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val photoUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        success ->
        if (success) {
            uri = photoUri.value
            isCameraImage = true
        } else {
            Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if(granted) {
            val newPhotoUri = StorageUtil.createImageUri(context)
            photoUri.value = newPhotoUri
            cameraLauncher.launch(newPhotoUri)
        }else{
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
            isCameraImage = false
        }
    )


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

        Spacer(Modifier.height(8.dp))

        Button( onClick = {
            permissionLauncher.launch((Manifest.permission.CAMERA))
        }) {
            Text("Take Picture with camera")
        }

        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = stringResource(R.string.sign_out))
        }
            // Preview Image
        uri?.let {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = if(isCameraImage){
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(8.dp)
                } else{
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(8.dp)
                },
                contentScale = if (isCameraImage) ContentScale.Fit else ContentScale.FillWidth
            )
            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                StorageUtil.uploadToStorage(uri = it, context = context, type = context.getString(R.string.image))
            }) {
                Text(stringResource(R.string.upload))
            }
        }


        }
    }




@Preview
@Composable
fun HomepagePreview(){
    FirebaseAppTheme {
        HomePage(modifier = Modifier, navController = rememberNavController(),authViewModel = AuthViewModel())
    }
}


