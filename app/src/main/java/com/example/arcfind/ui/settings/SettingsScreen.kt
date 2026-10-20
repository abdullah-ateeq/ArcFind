package com.example.arcfind.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arcfind.model.AppSettings
import com.example.arcfind.model.AppTheme
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdateSettings: (AppSettings) -> Unit,
    onClearHistory: () -> Unit,
    onResetSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Reset all preferences to default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetSettings()
                        showResetConfirmDialog = false
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Clear all saved arc calculations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance
            item {
                SettingsSection(title = "APPEARANCE") {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppTheme.entries.forEach { theme ->
                            FilterChip(
                                selected = settings.theme == theme,
                                onClick = { onUpdateSettings(settings.copy(theme = theme)) },
                                label = { Text(theme.displayName) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SwitchSettingRow(
                        title = "Dynamic Color",
                        subtitle = "Use colors inspired by system wallpaper (Android 12+)",
                        checked = settings.dynamicColor,
                        onCheckedChange = { onUpdateSettings(settings.copy(dynamicColor = it)) }
                    )
                }
            }

            // Behavior
            item {
                SettingsSection(title = "BEHAVIOR") {
                    SwitchSettingRow(
                        title = "Haptic Feedback",
                        subtitle = "Vibrate on buttons, results, and actions",
                        checked = settings.hapticFeedback,
                        onCheckedChange = { onUpdateSettings(settings.copy(hapticFeedback = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SwitchSettingRow(
                        title = "Animations",
                        subtitle = "Animate circle arc sweep and mode transitions",
                        checked = settings.animations,
                        onCheckedChange = { onUpdateSettings(settings.copy(animations = it)) }
                    )
                }
            }

            // Number Display
            item {
                SettingsSection(title = "NUMBER DISPLAY") {
                    DecimalPlacesSelector(
                        selectedPlaces = settings.decimalPlaces,
                        onPlacesSelected = { onUpdateSettings(settings.copy(decimalPlaces = it)) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    RoundingModeSelector(
                        selectedMode = settings.roundingMode,
                        onModeSelected = { onUpdateSettings(settings.copy(roundingMode = it)) }
                    )
                }
            }

            // Display
            item {
                SettingsSection(title = "DISPLAY") {
                    SwitchSettingRow(
                        title = "Show Formula",
                        subtitle = "Display arc formula reference card",
                        checked = settings.showFormula,
                        onCheckedChange = { onUpdateSettings(settings.copy(showFormula = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SwitchSettingRow(
                        title = "Show Calculation Steps",
                        subtitle = "Expandable step-by-step mathematical reasoning",
                        checked = settings.showCalculationSteps,
                        onCheckedChange = { onUpdateSettings(settings.copy(showCalculationSteps = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SwitchSettingRow(
                        title = "Show Circle Diagram",
                        subtitle = "Visual vector representation of the arc",
                        checked = settings.showCircleDiagram,
                        onCheckedChange = { onUpdateSettings(settings.copy(showCircleDiagram = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SwitchSettingRow(
                        title = "Show More Circle Details",
                        subtitle = "Include diameter, circumference & arc percentage",
                        checked = settings.showMoreDetails,
                        onCheckedChange = { onUpdateSettings(settings.copy(showMoreDetails = it)) }
                    )
                }
            }

            // History & Data
            item {
                SettingsSection(title = "HISTORY & DATA") {
                    SwitchSettingRow(
                        title = "Confirm Delete",
                        subtitle = "Show confirmation dialog before removing saved items",
                        checked = settings.confirmDelete,
                        onCheckedChange = { onUpdateSettings(settings.copy(confirmDelete = it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear History", color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showResetConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Settings to Default")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SwitchSettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun DecimalPlacesSelector(
    selectedPlaces: Int,
    onPlacesSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(0, 1, 2, 3, 4, 6, 8, 10, 12)
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Decimal Places",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Maximum decimal places displayed ($selectedPlaces)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Text(
                text = "$selectedPlaces places",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { places ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "$places decimal places ${if (places == 6) "(Default)" else ""}",
                                fontWeight = if (places == selectedPlaces) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onPlacesSelected(places)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundingModeSelector(
    selectedMode: RoundingMode,
    onModeSelected: (RoundingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        RoundingMode.HALF_UP to "Half Up (Default)",
        RoundingMode.HALF_DOWN to "Half Down",
        RoundingMode.HALF_EVEN to "Half Even (Banker's)",
        RoundingMode.UP to "Up",
        RoundingMode.DOWN to "Down",
        RoundingMode.CEILING to "Ceiling",
        RoundingMode.FLOOR to "Floor"
    )
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Rounding Mode",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Mathematical rounding algorithm",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Text(
                text = selectedMode.name.replace("_", " "),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                modes.forEach { (mode, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (mode == selectedMode) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onModeSelected(mode)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
