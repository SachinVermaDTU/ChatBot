package com.example.c1

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.c1.ui.theme.C1Theme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val uriState = MutableStateFlow("")
    private val imagePicker = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uriState.value = uri.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            C1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary)
                                    .height(55.dp)
                                    .padding(horizontal = 16.dp)
                            )
                            {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    text = stringResource(id = R.string.app_name),
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )

                            }
                        }
                    ) {
                        ChatScreen(paddingValues = it)
                    }
                }
            }
        }
    }


    @Composable
    fun ChatScreen(paddingValues: PaddingValues) {
        val chatViewModel = viewModel<ChatViewModel>()
        val chatState = chatViewModel.chatState.collectAsState().value
        val bitmap = getBitmap()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                itemsIndexed(chatState.chatList) { index, chat ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = if (chat.isFromUser) Arrangement.End else Arrangement.Start
                        ) {
                            if (chat.isFromUser) {
                                UserChatItem(
                                    prompt = chat.prompt,
                                    bitmap = chat.bitmap
                                )
                            } else {
                                ModelChatItem(prompt = chat.prompt)
                            }
                        }

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
            ){
                Column(
                    modifier = Modifier.padding(start = 10.dp , bottom = 22.dp)
                ) {
                    bitmap?.let{
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentDescription = "picked image",
                            contentScale = ContentScale.Crop,
                            bitmap = it.asImageBitmap()
                        )
                    }
                    Icon(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                imagePicker.launch(
                                    PickVisualMediaRequest
                                        .Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        .build()
                                )
                            },
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Add photo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))

                TextField(
                    modifier = Modifier.width(400.dp),
                    value = chatState.prompt,
                    onValueChange ={
                        chatViewModel.onEvent(ChatUiEvents.UpdatePrompt(it))
                    },
                    placeholder = {
                        Text(text = "Type a prompt")
                    }
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    modifier = Modifier
                        .padding(4.dp)

                        .clickable {
                            chatViewModel.onEvent(ChatUiEvents.SendPrompt(chatState.prompt, bitmap))
                        },
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send prompt",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    @Composable
    fun ModelChatItem(prompt : String ){
       Row(
           modifier = Modifier.padding( bottom = 22.dp , end = 43.dp)
       ) {

           Surface(
               modifier = Modifier

                   .padding(bottom = 8.dp),
               shape = RoundedCornerShape(12.dp), // Increased corner radius
               color = MaterialTheme.colorScheme.background, // Use primary color for user chat
               shadowElevation = 2.dp // Subtle elevation
           ) {
               SelectionContainer {
          Text(
              modifier = Modifier

                  .fillMaxSize(0.8f)
                  .selectable(selected = true, onClick = {})
                  .background(MaterialTheme.colorScheme.primary),

                 text = prompt,
                 fontSize = 17.sp,
                 color = MaterialTheme.colorScheme.onTertiary
          )
           }}
       }
    }
    @Composable
    fun UserChatItem(prompt : String , bitmap: Bitmap?){
        Row(
            modifier = Modifier.padding( bottom = 6.dp)
        ) {
            bitmap?.let{
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentDescription = "picked image",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                )
            }
            Surface(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp), // Increased corner radius
                color = MaterialTheme.colorScheme.primary, // Use primary color for user chat
                shadowElevation = 2.dp // Subtle elevation
            ) {
                SelectionContainer {
                Text(
                    modifier = Modifier
                        .selectable(selected = true, onClick = {})
                        .fillMaxSize(0.8f)
                        .background(MaterialTheme.colorScheme.primary),
                    text = prompt,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }}
        }
    }

    @Composable
    private fun getBitmap():Bitmap?{
        val uri = uriState.collectAsState().value

        val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .build()
        ).state

        if(imageState is AsyncImagePainter.State.Success){
            return imageState.result.drawable.toBitmap()
        }
        return null
    }
}

