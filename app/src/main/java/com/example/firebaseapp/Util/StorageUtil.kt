package com.example.firebaseapp.Util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object StorageUtil {

    fun uploadToStorage(uri: Uri, context: Context, type: String) {

        FirebaseApp.initializeApp(context.applicationContext)

        val ext = if (type == "image") "jpg" else "mp4"
        val storageRef: StorageReference =
            Firebase.storage.reference.child("images/${UUID.randomUUID()}.$ext")

        storageRef
            .putFile(uri)
            .addOnSuccessListener { snapshot ->
                // get the download URL if you want
                snapshot.storage.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        val data = hashMapOf(
                            "url" to downloadUri.toString(),
                            "uploadedAt" to FieldValue.serverTimestamp(),
                            "uploaderId" to Firebase.auth.currentUser?.uid
                        )

                        Firebase.firestore
                            .collection("images")
                            .add(data)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Upload + Firestore write succeeded!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Storage ok, Firestore write failed:\n${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                    }
                    .addOnFailureListener { e ->
                        // now you’ll see the real exception
                        Toast.makeText(
                            context,
                            "Could not get download URL:\n ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener{ e ->
                Toast.makeText(
                    context,
                    "Upload failed:\n${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun createImageUri(context: Context): Uri {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "JPEG_${timestamp}.jpg"
        // This writes into your app's cache directory
        val file = File(context.cacheDir, filename)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
}
