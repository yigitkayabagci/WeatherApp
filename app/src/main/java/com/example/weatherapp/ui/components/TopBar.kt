package com.example.weatherapp.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import R
import com.example.weatherapp.R
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    onSearch: (String) -> Unit,
    onLocationClick: () -> Unit,
    onRefreshClick: () -> Unit,
    languageManager: LanguageManager,
    onLanguageChange: (String) -> Unit

) {
    var showSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var showLanguageMenu by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (showSearch) {
                    showSearch = false
                    searchText = ""
                }
            }
    ) {
        if (showSearch) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.7f)
                ),
                placeholder = { Text(stringResource(id = R.string.search_city), color = Color.Black.copy(alpha = 0.7f)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text
                ),
                textStyle = TextStyle(color = Color.Black),
                keyboardActions = KeyboardActions(onSearch = {
                    onSearch(searchText)
                    showSearch = false
                    searchText = ""
                }),
                singleLine = true
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showSearch = true }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(45.dp)
                            .offset(y = 6.dp)
                    )
                }

                Row {
                    Box {
                        IconButton(onClick = { showLanguageMenu = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_world),
                                contentDescription = "Language",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(28.dp)
                                    .offset(y = 6.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            LanguageMenuItem(
                                name = "Türkçe",
                                emoji = "🇹🇷",
                                code = "tr"

                            ) {
                                languageManager.setLocale("tr"){
                                    onLanguageChange("tr")
                                }
                                showLanguageMenu = false
                            }
                            LanguageMenuItem(
                                name = "English",
                                emoji = "🇬🇧",
                                code = "en"
                            ) {
                                languageManager.setLocale("en"){
                                    onLanguageChange("en")
                                }

                                showLanguageMenu = false
                            }
                            LanguageMenuItem(
                                name = "Français",
                                emoji = "🇫🇷",
                                code = "fr"
                            ) {
                                languageManager.setLocale("fr"){
                                    onLanguageChange("fr")
                                }
                                showLanguageMenu = false
                            }
                            LanguageMenuItem(
                                name = "Deutsch",
                                emoji = "🇩🇪",
                                code = "de"
                            ) {
                                languageManager.setLocale("de"){
                                    onLanguageChange("de")
                                }
                                showLanguageMenu = false
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = onRefreshClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(45.dp)
                                .offset(y = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = onLocationClick) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(30.dp)
                                .offset(y = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageMenuItem(
    name: String,
    emoji: String,
    code: String,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    DropdownMenuItem(
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 20.sp)
                Text(text = name)
            }
        },
        onClick = {
            scope.launch {
                onClick()
            }
        }
    )
}