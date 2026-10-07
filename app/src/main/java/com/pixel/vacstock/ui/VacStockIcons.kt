package com.pixel.vacstock.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Material Symbols (rounded) built in code rather than as Android drawables, so the UI has no
 * platform resources to port and no icon library to pull in.
 */
internal object VacStockIcons {
    val Add: ImageVector by lazy {
        icon(
            "Add",
            "M18,13h-5v5c0,0.55 -0.45,1 -1,1s-1,-0.45 -1,-1v-5H6c-0.55,0 -1,-0.45 -1,-1s0.45,-1 1,-1h5V6" +
                "c0,-0.55 0.45,-1 1,-1s1,0.45 1,1v5h5c0.55,0 1,0.45 1,1s-0.45,1 -1,1z",
        )
    }

    val Remove: ImageVector by lazy {
        icon("Remove", "M18,13H6c-0.55,0 -1,-0.45 -1,-1s0.45,-1 1,-1h12c0.55,0 1,0.45 1,1s-0.45,1 -1,1z")
    }

    val Delete: ImageVector by lazy {
        icon(
            "Delete",
            "M6,19c0,1.1 0.9,2 2,2h8c1.1,0 2,-0.9 2,-2V9c0,-1.1 -0.9,-2 -2,-2H8c-1.1,0 -2,0.9 -2,2v10z" +
                "M18,4h-2.5l-0.71,-0.71c-0.18,-0.18 -0.44,-0.29 -0.7,-0.29H9.91c-0.26,0 -0.52,0.11 -0.7,0.29" +
                "L8.5,4H6c-0.55,0 -1,0.45 -1,1s0.45,1 1,1h12c0.55,0 1,-0.45 1,-1s-0.45,-1 -1,-1z",
        )
    }

    private fun icon(name: String, pathData: String): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(pathData = addPathNodes(pathData), fill = SolidColor(Color.Black)).build()
}
