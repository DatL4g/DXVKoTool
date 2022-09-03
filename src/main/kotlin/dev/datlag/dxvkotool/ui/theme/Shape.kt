package dev.datlag.dxvkotool.ui.theme

import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Shapes

object Shape {

    private val FullRoundedShape = Shapes.Full

    val LeftRoundedShape = AbsoluteRoundedCornerShape(
        FullRoundedShape.topStart,
        CornerSize(0),
        CornerSize(0),
        FullRoundedShape.bottomStart
    )

    val RightRoundedShape = AbsoluteRoundedCornerShape(
        CornerSize(0),
        FullRoundedShape.topEnd,
        FullRoundedShape.bottomEnd,
        CornerSize(0)
    )

}