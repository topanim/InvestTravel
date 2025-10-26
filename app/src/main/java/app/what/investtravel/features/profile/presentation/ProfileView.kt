package app.what.investtravel.features.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.foundation.core.Listener
import app.what.investtravel.features.profile.domain.models.ProfileEvent
import app.what.investtravel.features.profile.domain.models.ProfileState

@Composable
fun ProfileView(
    state: ProfileState,
    listener: Listener<ProfileEvent>
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(state.userName) }

    LaunchedEffect(state.userName) {
        editedName = state.userName
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Аватар пользователя
        Surface(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.userName.isNotEmpty()) state.userName.first().uppercase() else "?",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Имя пользователя
        if (isEditing) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Имя") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                trailingIcon = {
                    Row {
                        IconButton(onClick = {
                            listener(ProfileEvent.UpdateUserName(editedName))
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Сохранить")
                        }
                        IconButton(onClick = {
                            editedName = state.userName
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Отмена")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (state.userName.isNotEmpty()) state.userName else "Имя не указано",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (state.userName.isNotEmpty()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Button(
                    onClick = { isEditing = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Изменить имя")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Информационная карточка
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Это ваш профиль. Вы можете изменить свое имя в любой момент.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}