package com.app.dreamiaselite.ui.screen.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    navController: NavController,
    userName: String?,
    userEmail: String?,
    targetYear: Int?,
    createdAt: Long?,
    avatarUrl: String?,
    isSaving: Boolean,
    profileMessage: String?,
    profileMessageIsError: Boolean,
    onUpdateProfile: (String, String, Uri?) -> Unit,
    onClearMessage: () -> Unit
) {
    val displayName = userName?.ifBlank { null } ?: "Dreamer"
    val initials = displayName.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }.ifBlank { "DE" }
    val email = userEmail ?: "Not set"
    val target = targetYear?.toString() ?: "Not set"
    val joinedDate = createdAt?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Since day one"
    val context = LocalContext.current

    var showEditSheet by remember { mutableStateOf(false) }
    var selectedPhotoUriString by rememberSaveable(avatarUrl) { mutableStateOf(avatarUrl) }
    val selectedPhotoUri = selectedPhotoUriString?.let { Uri.parse(it) }
    val avatarBitmap by rememberBitmapFromUri(selectedPhotoUri)

    var showConfirmDialog by remember { mutableStateOf(false) }
    var croppedImageUri by remember { mutableStateOf<Uri?>(null) }

    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            croppedImageUri = result.uriContent
            showConfirmDialog = true
        }
    }
    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val startCropper: () -> Unit = remember(cropLauncher) {
        {
            cropLauncher.launch(
                CropImageContractOptions(
                    null,
                    CropImageOptions(
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        outputCompressQuality = 70,
                        imageSourceIncludeCamera = false,
                        cropMenuCropButtonTitle = "Set",
                        activityTitle = "Back"
                    )
                )
            )
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startCropper()
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permission needed to change profile photo") }
        }
    }
    val handleChangePhoto: () -> Unit = {
        if (ContextCompat.checkSelfPermission(context, imagePermission) == PackageManager.PERMISSION_GRANTED) {
            startCropper()
        } else {
            permissionLauncher.launch(imagePermission)
        }
    }

    LaunchedEffect(profileMessage) {
        profileMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (profileMessageIsError) "⚠️ $message" else "✅ $message"
                )
                onClearMessage()
            }
        }
    }

    if (showConfirmDialog) {
        ConfirmCropDialog(
            croppedImageUri = croppedImageUri,
            onConfirm = { uri ->
                selectedPhotoUriString = uri?.toString()
                onUpdateProfile(displayName, target, uri)
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            item {
                ProfileHeaderCard(
                    displayName = displayName,
                    email = email,
                    target = target,
                    initials = initials,
                    joinedDate = joinedDate,
                    avatarBitmap = avatarBitmap,
                    onAddPhoto = handleChangePhoto,
                    onEdit = { showEditSheet = true }
                )
            }

            item {
                Spacer(Modifier.height(6.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    if (showEditSheet) {
        EditProfileSheet(
            currentName = displayName,
            currentTarget = target,
            isSaving = isSaving,
            onDismiss = { showEditSheet = false },
            onSave = { name, year ->
                onUpdateProfile(name, year, selectedPhotoUri)
                showEditSheet = false
            }
        )
    }
}

@Composable
private fun ProfileHeaderCard(
    displayName: String,
    email: String,
    target: String,
    initials: String,
    joinedDate: String,
    avatarBitmap: ImageBitmap?,
    onAddPhoto: () -> Unit,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .then(
                    Modifier
                        .background(Color.Transparent)
                        .clip(CircleShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap,
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile placeholder",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Target: $target",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "Joined $joinedDate",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Edit profile")
            }
            OutlinedButton(
                onClick = onAddPhoto,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Change photo")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileSheet(
    currentName: String,
    currentTarget: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember(currentName) { mutableStateOf(currentName) }
    var targetYear by remember(currentTarget) { mutableStateOf(currentTarget.filter { it.isDigit() }) }

    val nameError = name.isBlank()
    val targetError = targetYear.toIntOrNull()?.let { it !in 2024..2100 } ?: true
    val canSave = !nameError && !targetError && !isSaving

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Edit profile",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Keep your details fresh so we can personalize your experience.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full name") },
                isError = nameError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = targetYear,
                onValueChange = { targetYear = it.filter { char -> char.isDigit() } },
                label = { Text("Target year") },
                isError = targetError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError || targetError) {
                Text(
                    text = "Name can't be empty and target year must be between 2024 and 2100.",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFD32F2F))
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { onSave(name.trim(), targetYear.trim()) },
                    enabled = canSave,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSaving) {
                        Text("Saving...")
                    } else {
                        Text("Save")
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ConfirmCropDialog(
    croppedImageUri: Uri?,
    onConfirm: (Uri?) -> Unit,
    onDismiss: () -> Unit
) {
    val imageBitmap by rememberBitmapFromUri(uri = croppedImageUri)

    if (imageBitmap != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Set as profile picture?") },
            text = {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = "Cropped image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            },
            confirmButton = {
                Button(onClick = { onConfirm(croppedImageUri) }) {
                    Text("Set")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun rememberBitmapFromUri(uri: Uri?): androidx.compose.runtime.State<ImageBitmap?> {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        if (uri == null) {
            value = null
            return@produceState
        }
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
}
