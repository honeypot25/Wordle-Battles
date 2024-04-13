package com.honeyapps.wordlebattles.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.honeyapps.wordlebattles.ui.theme.dimens
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

object Konfetti {
    val parties = listOf(
        Party(
//            angle = 0,
//            spread = 360,
            colors = listOf(Color.Yellow.toArgb(), Color.Green.toArgb(), Color.Red.toArgb()),
//            damping = 0.9f,
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(300),
//            fadeOutEnabled = true,
//            shapes = listOf(Shape.Circle, Shape.Square),
            size = listOf(Size(dimens.konfetti)),
//            speed = 0f,
//            maxSpeed = 30f,
//            position = Position.Relative(0.0, 1.0).between(Position.Relative(0.5, 0.5)),
            position = Position.Relative(0.0, 1.0),
//            timeToLive = 2000L,
            rotation = Rotation()
        ),
        Party(
            colors = listOf(Color.Yellow.toArgb(), Color.Green.toArgb(), Color.Red.toArgb()),
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(300),
            size = listOf(Size(dimens.konfetti)),
            position = Position.Relative(1.0, 1.0),
            rotation = Rotation()
        )
    )
}

sealed class KonfettiState {
    data object Started : KonfettiState() { val parties = Konfetti.parties }
    data object Idle : KonfettiState()
}