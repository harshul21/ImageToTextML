package com.example.imagetotextml

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imagetotextml.ui.theme.ImageToTextMLTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageToTextMLTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var text by remember { mutableStateOf<String>("") }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        bitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { imageLauncher.launch("image/*") }) {
            Text("Select Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            bitmap?.let { bmp ->
                val image = InputImage.fromBitmap(bmp, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        text = visionText.text
                    }
                    .addOnFailureListener { e ->
                        text = "Error: ${e.localizedMessage}"
                    }
            }
        }) {
            Text("Recognize Text")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text)
    }
}