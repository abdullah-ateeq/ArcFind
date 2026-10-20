package com.example.arcfind.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arcfind.model.ArcType
import java.math.BigDecimal
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcCircleDiagram(
    angleDegrees: BigDecimal,
    arcType: ArcType,
    animated: Boolean = true,
    modifier: Modifier = Modifier
) {
    val targetAngle = angleDegrees.toFloat().coerceIn(0.1f, 360f)
    val displayAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = if (animated) tween(durationMillis = 400, easing = FastOutSlowInEasing) else tween(0),
        label = "arc_sweep_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Circle showing the central angle, radius, and highlighted arc."
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GEOMETRY DIAGRAM",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = arcType.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val circleColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            val arcHighlightColor = MaterialTheme.colorScheme.tertiary
            val radiusLineColor = MaterialTheme.colorScheme.primary
            val centerDotColor = MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(180.dp)) {
                    val diameter = size.minDimension
                    val radius = diameter / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Draw outer reference circle
                    drawCircle(
                        color = circleColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    val startAngle = 0f
                    val sweep = displayAngle

                    // Highlighted Arc
                    drawArc(
                        color = arcHighlightColor,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(diameter, diameter),
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // First radius line (at start angle 0°)
                    val startRadRad = Math.toRadians(startAngle.toDouble())
                    val startPoint = Offset(
                        x = (center.x + radius * cos(startRadRad)).toFloat(),
                        y = (center.y + radius * sin(startRadRad)).toFloat()
                    )
                    drawLine(
                        color = radiusLineColor,
                        start = center,
                        end = startPoint,
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = radiusLineColor,
                        radius = 4.dp.toPx(),
                        center = startPoint
                    )

                    // Second radius line (at sweep angle)
                    if (sweep < 360f) {
                        val endRadRad = Math.toRadians((startAngle + sweep).toDouble())
                        val endPoint = Offset(
                            x = (center.x + radius * cos(endRadRad)).toFloat(),
                            y = (center.y + radius * sin(endRadRad)).toFloat()
                        )
                        drawLine(
                            color = radiusLineColor,
                            start = center,
                            end = endPoint,
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(
                            color = radiusLineColor,
                            radius = 4.dp.toPx(),
                            center = endPoint
                        )
                    }

                    // Central angle indicator arc near center
                    val innerRadius = radius * 0.35f
                    if (sweep in 5f..359.9f) {
                        drawArc(
                            color = radiusLineColor.copy(alpha = 0.7f),
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true,
                            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                            size = Size(innerRadius * 2, innerRadius * 2),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    // Center point marker
                    drawCircle(
                        color = centerDotColor,
                        radius = 5.dp.toPx(),
                        center = center
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DiagramLegendItem(color = arcHighlightColor, label = "Arc (s)")
                DiagramLegendItem(color = radiusLineColor, label = "Radius (r)")
                DiagramLegendItem(color = centerDotColor, label = "Center (•)")
            }
        }
    }
}

@Composable
private fun DiagramLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(8.dp),
            shape = RoundedCornerShape(4.dp),
            color = color
        ) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
