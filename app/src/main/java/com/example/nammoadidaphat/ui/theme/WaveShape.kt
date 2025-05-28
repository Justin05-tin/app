import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Shape

val WaveShape: Shape = GenericShape { size: Size, _: LayoutDirection ->
    moveTo(0f, 0f)
    lineTo(0f, size.height * 0.8f)

    // tạo đường cong lượn sóng
    quadraticBezierTo(
        size.width * 0.25f, size.height,
        size.width * 0.5f, size.height * 0.8f
    )
    quadraticBezierTo(
        size.width * 0.75f, size.height * 0.6f,
        size.width, size.height * 0.8f
    )

    lineTo(size.width, 0f)
    close()
}
