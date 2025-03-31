package com.noam.happybirthday.utils

import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.materialcore.toRadians
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

//internal fun circularMeasurePolicy(
//    overrideRadius: (() -> Dp)?,
//    startAngle: () -> Float,
//    spanAngle: Float
//) = MultiContentMeasurePolicy { (centerMeasurables: List<Measurable>,
//                                    contentMeasurables: List<Measurable>),
//                                constraints: Constraints
//    ->
//require(centerMeasurables.size == 1) { "Center composable can have only one child" }
//
//// Measure children with modified constraints
//val modifiedConstraints = constraints.copy(
//    minWidth = 0,
//    minHeight = 0,
//)
//val centerPlaceable: Placeable = centerMeasurables.first().measure(modifiedConstraints)
//val contentPlaceables: List<Placeable> = contentMeasurables.map { it.measure(modifiedConstraints) }
//
//// Calculate the overall radius and layout size
//val overallRadius = overrideRadius?.invoke()?.roundToPx() ?: (centerPlaceable.height / 2)
//val maxExtraRadius = contentPlaceables.mapNotNull { placeable ->
//    (placeable.parentData as? CircularParentData)?.extraRadius?.roundToPx()
//}.maxOrNull() ?: 0
//val totalRadius = overallRadius + maxExtraRadius
//val biggestChildSize = contentPlaceables.maxOfOrNull { it.height } ?: 0
//val centerSize = centerPlaceable.height
//val layoutSize = max(centerSize, 2 * totalRadius + biggestChildSize)
//
//layout(layoutSize, layoutSize) {
//    // Place the center and content children in the circular layout
//    val middle = layoutSize / 2
//    var angle = startAngle()
//
//    // Adjust the angle increment based on the clockwise direction
//    val angleIncrement = if (contentPlaceables.size > 1) {
//        spanAngle / (contentPlaceables.size - 1)
//    } else {
//        0f
//    }
//
//    contentPlaceables.forEachIndexed { index, placeable ->
//        val finalAngle = (placeable.parentData as? CircularParentData)?.exactAngle ?: angle
//        val angleRadian = finalAngle.toRadians()
//        val radius =
//            overallRadius + ((placeable.parentData as? CircularParentData)?.extraRadius?.roundToPx()
//                ?: 0)
//        placeable.place(
//            x = (middle + radius * sin(angleRadian) - placeable.height / 2).toInt(),
//            y = (middle - radius * cos(angleRadian) - placeable.height / 2).toInt(),
//        )
//        if (index < contentPlaceables.size - 1) {
//            angle += angleIncrement
//        }
//    }
//    centerPlaceable.place(middle - centerSize / 2, middle - centerSize / 2)
//}
//}

internal fun circularMeasurePolicy(
    overrideRadius: (() -> Dp)?,
    startAngle: () -> Float
) = MultiContentMeasurePolicy { (centerMeasurables: List<Measurable>, contentMeasurables: List<Measurable>),
                                constraints: Constraints
    ->

    val centerMeasurable = centerMeasurables.first()
    val contentMeasurable = contentMeasurables.first()
    val modifiedConstraints = constraints.copy(
        minWidth = 0,
        minHeight = 0,
    )
    val centerPlaceable: Placeable = centerMeasurable.measure(modifiedConstraints)
    val contentPlaceable: Placeable = contentMeasurable.measure(modifiedConstraints)

// Calculate the overall radius and layout size
    val overallRadius = overrideRadius?.invoke()?.roundToPx() ?: (centerPlaceable.height / 2)
    val biggestChildSize = contentPlaceable.height
    val centerSize = centerPlaceable.height
    val layoutSize = max(centerSize, 2 * overallRadius + biggestChildSize)

    layout(layoutSize, layoutSize) {
        // Place the center and child in the correct angle
        val middle = layoutSize / 2
        val angle = startAngle()

        val angleRadian = angle.toRadians()
        centerPlaceable.place(middle - centerSize / 2, middle - centerSize / 2)
        contentPlaceable.place(
            x = (middle + overallRadius * sin(angleRadian) - contentPlaceable.height / 2).toInt(),
            y = (middle - overallRadius * cos(angleRadian) - contentPlaceable.height / 2).toInt()
        )
    }
}