package com.example.arcfind.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arcfind.domain.ArcFindFormatter
import com.example.arcfind.domain.LengthUnitConverter
import com.example.arcfind.model.LengthUnit
import com.example.arcfind.model.SolveType
import com.example.arcfind.ui.ArcFindUiState
import com.example.arcfind.ui.components.ArcCircleDiagram
import com.example.arcfind.ui.components.CalculationStepsCard
import com.example.arcfind.ui.components.CircleDetailsCard
import com.example.arcfind.ui.components.FormulaCard
import com.example.arcfind.ui.components.LengthUnitDropdown
import com.example.arcfind.ui.components.SolveModeSelector
import com.example.arcfind.ui.util.ClipboardUtil
import com.example.arcfind.ui.util.HapticUtil
import com.example.arcfind.ui.util.ShareUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: ArcFindUiState,
    onSolveTypeSelected: (SolveType) -> Unit,
    onRadiusChanged: (String) -> Unit,
    onAngleChanged: (String) -> Unit,
    onArcLengthChanged: (String) -> Unit,
    onRadiusUnitChanged: (LengthUnit) -> Unit,
    onArcLengthUnitChanged: (LengthUnit) -> Unit,
    onResultUnitChanged: (LengthUnit) -> Unit,
    onSaveCalculation: () -> Unit,
    onResetAll: () -> Unit,
    onClearNotification: () -> Unit,
    getShareText: () -> String,
    getCopyValueOnly: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userNotification) {
        uiState.userNotification?.let {
            snackbarHostState.showSnackbar(it)
            onClearNotification()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ArcFind",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Calculate missing radius, angle, or arc length.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtil.performLight(view, uiState.settings.hapticFeedback)
                            onResetAll()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset calculation to default",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Solve Mode Selector
            item {
                SolveModeSelector(
                    selectedType = uiState.solveType,
                    onSelectType = {
                        HapticUtil.performLight(view, uiState.settings.hapticFeedback)
                        onSolveTypeSelected(it)
                    }
                )
            }

            // Input Fields Card
            item {
                InputCard(
                    uiState = uiState,
                    onRadiusChanged = onRadiusChanged,
                    onAngleChanged = onAngleChanged,
                    onArcLengthChanged = onArcLengthChanged,
                    onRadiusUnitChanged = onRadiusUnitChanged,
                    onArcLengthUnitChanged = onArcLengthUnitChanged
                )
            }

            // Error Message (if any)
            item {
                AnimatedVisibility(
                    visible = uiState.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    uiState.errorMessage?.let { errorText ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = errorText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Primary Result Card
            item {
                uiState.result?.let { result ->
                    ResultCard(
                        uiState = uiState,
                        onResultUnitChanged = onResultUnitChanged,
                        onSaveCalculation = {
                            HapticUtil.performSuccess(view, uiState.settings.hapticFeedback)
                            onSaveCalculation()
                        },
                        onCopyValue = {
                            val value = getCopyValueOnly()
                            ClipboardUtil.copyToClipboard(context, "ArcFind Value", value)
                            HapticUtil.performSuccess(view, uiState.settings.hapticFeedback)
                        },
                        onCopyAll = {
                            val shareText = getShareText()
                            ClipboardUtil.copyToClipboard(context, "ArcFind Calculation", shareText)
                            HapticUtil.performSuccess(view, uiState.settings.hapticFeedback)
                        },
                        onShare = {
                            val shareText = getShareText()
                            ShareUtil.shareCalculation(context, shareText)
                            HapticUtil.performLight(view, uiState.settings.hapticFeedback)
                        }
                    )
                }
            }

            // Circle Diagram
            if (uiState.settings.showCircleDiagram && uiState.result != null) {
                item {
                    ArcCircleDiagram(
                        angleDegrees = uiState.result.angleDegrees,
                        arcType = uiState.result.arcType,
                        animated = uiState.settings.animations
                    )
                }
            }

            // Formula Card
            if (uiState.settings.showFormula) {
                item {
                    FormulaCard()
                }
            }

            // Calculation Steps Card
            if (uiState.settings.showCalculationSteps && uiState.result != null) {
                item {
                    CalculationStepsCard(steps = uiState.result.calculationSteps)
                }
            }

            // More Circle Details Card
            if (uiState.settings.showMoreDetails && uiState.result != null) {
                item {
                    CircleDetailsCard(
                        result = uiState.result,
                        resultUnit = uiState.resultUnit,
                        decimals = uiState.settings.decimalPlaces,
                        roundingMode = uiState.settings.roundingMode
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InputCard(
    uiState: ArcFindUiState,
    onRadiusChanged: (String) -> Unit,
    onAngleChanged: (String) -> Unit,
    onArcLengthChanged: (String) -> Unit,
    onRadiusUnitChanged: (LengthUnit) -> Unit,
    onArcLengthUnitChanged: (LengthUnit) -> Unit,
    modifier: Modifier = Modifier
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
                text = "KNOWN VALUES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            when (uiState.solveType) {
                SolveType.FIND_ARC_LENGTH -> {
                    // Inputs: Radius & Central Angle
                    InputFieldWithUnit(
                        label = "RADIUS",
                        helperText = "Circle radius value",
                        value = uiState.radiusInput,
                        onValueChange = onRadiusChanged,
                        selectedUnit = uiState.radiusUnit,
                        onUnitSelected = onRadiusUnitChanged,
                        accessibilityDescription = "Circle radius value."
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    InputFieldDegrees(
                        label = "CENTRAL ANGLE",
                        helperText = "The angle formed at the center of the circle.",
                        value = uiState.angleInput,
                        onValueChange = onAngleChanged,
                        accessibilityDescription = "Central angle in degrees."
                    )
                }
                SolveType.FIND_RADIUS -> {
                    // Inputs: Arc Length & Central Angle
                    InputFieldWithUnit(
                        label = "ARC LENGTH",
                        helperText = "Length of the arc",
                        value = uiState.arcLengthInput,
                        onValueChange = onArcLengthChanged,
                        selectedUnit = uiState.arcLengthUnit,
                        onUnitSelected = onArcLengthUnitChanged,
                        accessibilityDescription = "Arc length value."
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    InputFieldDegrees(
                        label = "CENTRAL ANGLE",
                        helperText = "The angle formed at the center of the circle.",
                        value = uiState.angleInput,
                        onValueChange = onAngleChanged,
                        accessibilityDescription = "Central angle in degrees."
                    )
                }
                SolveType.FIND_ANGLE -> {
                    // Inputs: Arc Length & Radius
                    InputFieldWithUnit(
                        label = "ARC LENGTH",
                        helperText = "Length of the arc",
                        value = uiState.arcLengthInput,
                        onValueChange = onArcLengthChanged,
                        selectedUnit = uiState.arcLengthUnit,
                        onUnitSelected = onArcLengthUnitChanged,
                        accessibilityDescription = "Arc length value."
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    InputFieldWithUnit(
                        label = "RADIUS",
                        helperText = "Circle radius value",
                        value = uiState.radiusInput,
                        onValueChange = onRadiusChanged,
                        selectedUnit = uiState.radiusUnit,
                        onUnitSelected = onRadiusUnitChanged,
                        accessibilityDescription = "Circle radius value."
                    )
                }
            }
        }
    }
}

@Composable
private fun InputFieldWithUnit(
    label: String,
    helperText: String,
    value: String,
    onValueChange: (String) -> Unit,
    selectedUnit: LengthUnit,
    onUnitSelected: (LengthUnit) -> Unit,
    accessibilityDescription: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = helperText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = accessibilityDescription },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            LengthUnitDropdown(
                selectedUnit = selectedUnit,
                onUnitSelected = onUnitSelected
            )
        }
    }
}

@Composable
private fun InputFieldDegrees(
    label: String,
    helperText: String,
    value: String,
    onValueChange: (String) -> Unit,
    accessibilityDescription: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = helperText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = accessibilityDescription },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.height(56.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "°",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultCard(
    uiState: ArcFindUiState,
    onResultUnitChanged: (LengthUnit) -> Unit,
    onSaveCalculation: () -> Unit,
    onCopyValue: () -> Unit,
    onCopyAll: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = uiState.result ?: return
    val label = ArcFindFormatter.formatPrimaryResultLabel(result.solveType)
    val formattedValue = ArcFindFormatter.formatPrimaryResult(
        result = result,
        resultUnit = uiState.resultUnit,
        decimals = uiState.settings.decimalPlaces,
        roundingMode = uiState.settings.roundingMode
    )

    val accessibilityResultDesc = when (result.solveType) {
        SolveType.FIND_ARC_LENGTH -> "Calculated arc length."
        SolveType.FIND_RADIUS -> "Calculated radius."
        SolveType.FIND_ANGLE -> "Calculated central angle."
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = accessibilityResultDesc },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                // If solve type is length (FIND_ARC_LENGTH or FIND_RADIUS), allow choosing output unit!
                if (result.solveType != SolveType.FIND_ANGLE) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Output Unit: ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LengthUnitDropdown(
                            selectedUnit = uiState.resultUnit,
                            onUnitSelected = onResultUnitChanged
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary dominant result value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedValue,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onCopyValue,
                    modifier = Modifier.semantics { contentDescription = "Copy calculated result." }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy result value",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Arc Type badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = result.arcType.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary of known inputs
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Inputs:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    when (result.solveType) {
                        SolveType.FIND_ARC_LENGTH -> {
                            val radUserVal = LengthUnitConverter.fromMeters(result.radius, uiState.radiusUnit)
                            val radStr = ArcFindFormatter.formatLength(radUserVal, uiState.radiusUnit, uiState.settings.decimalPlaces, uiState.settings.roundingMode)
                            val angleStr = ArcFindFormatter.formatAngle(result.angleDegrees, uiState.settings.decimalPlaces, uiState.settings.roundingMode)
                            Text(text = "• Radius: $radStr", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Central Angle: $angleStr", style = MaterialTheme.typography.bodySmall)
                        }
                        SolveType.FIND_RADIUS -> {
                            val arcUserVal = LengthUnitConverter.fromMeters(result.arcLength, uiState.arcLengthUnit)
                            val arcStr = ArcFindFormatter.formatLength(arcUserVal, uiState.arcLengthUnit, uiState.settings.decimalPlaces, uiState.settings.roundingMode)
                            val angleStr = ArcFindFormatter.formatAngle(result.angleDegrees, uiState.settings.decimalPlaces, uiState.settings.roundingMode)
                            Text(text = "• Arc Length: $arcStr", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Central Angle: $angleStr", style = MaterialTheme.typography.bodySmall)
                        }
                        SolveType.FIND_ANGLE -> {
                            val arcUserVal = LengthUnitConverter.fromMeters(result.arcLength, uiState.arcLengthUnit)
                            val radUserVal = LengthUnitConverter.fromMeters(result.radius, uiState.radiusUnit)
                            val arcStr = ArcFindFormatter.formatLength(arcUserVal, uiState.arcLengthUnit, uiState.settings.decimalPlaces, uiState.settings.roundingMode)
                            val radStr = ArcFindFormatter.formatLength(radUserVal, uiState.radiusUnit, uiState.settings.decimalPlaces, uiState.settings.roundingMode)
                            Text(text = "• Arc Length: $arcStr", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Radius: $radStr", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons row: SAVE, COPY, SHARE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSaveCalculation,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SAVE")
                }

                FilledTonalButton(
                    onClick = onCopyAll,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("COPY")
                }

                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SHARE")
                }
            }
        }
    }
}
