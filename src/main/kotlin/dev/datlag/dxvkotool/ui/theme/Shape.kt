package dev.datlag.dxvkotool.ui.theme

import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object Shape {

    val FullRoundedShape = Shapes.Full

    val LeftRoundedShape = AbsoluteRoundedCornerShape(
        FullRoundedShape.topStart,
        CornerSize(2.dp),
        CornerSize(2.dp),
        FullRoundedShape.bottomStart
    )

    val RightRoundedShape = AbsoluteRoundedCornerShape(
        CornerSize(2.dp),
        FullRoundedShape.topEnd,
        FullRoundedShape.bottomEnd,
        CornerSize(2.dp)
    )

}
