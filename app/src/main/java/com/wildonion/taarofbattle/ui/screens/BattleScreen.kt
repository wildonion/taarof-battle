package com.wildonion.taarofbattle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wildonion.taarofbattle.game.GameViewModel
import com.wildonion.taarofbattle.game.Mode
import com.wildonion.taarofbattle.game.POWER_UPS
import kotlin.math.hypot

private val TargetSize: Dp = 96.dp
private val HitRadius: Dp = 60.dp

@Composable
private fun TapHalf(
    bg: Color,
    name: String,
    score: Int,
    combo: Int,
    target: Pair<Float, Float>,
    frozen: Boolean,
    onTap: (hit: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
            .pointerInput(target) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.pressed && !change.isConsumed) {
                                change.consume()
                                val w = size.width.toFloat().coerceAtLeast(1f)
                                val h = size.height.toFloat().coerceAtLeast(1f)
                                val tx = target.first * w
                                val ty = target.second * h
                                val d = hypot(change.position.x - tx, change.position.y - ty)
                                val radiusPx = HitRadius.toPx()
                                onTap(d <= radiusPx)
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // moving target button (positioned by fraction of half size)
        val halfW = maxWidth
        val halfH = maxHeight
        Box(modifier = Modifier.fillMaxSize()) {
            val xOff = halfW * target.first - TargetSize / 2
            val yOff = halfH * target.second - TargetSize / 2
            Box(
                modifier = Modifier
                    .offset(x = xOff, y = yOff)
                    .size(TargetSize)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, Color(0xFFE9C46A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("بزن!", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFFE76F51))
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("$score", color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Black)
            if (combo >= 3) Text("🔥 کمبو x$combo", color = Color.White, fontWeight = FontWeight.Bold)
            if (frozen) Text("🧊 فریز!", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PowerRow(
    label: String,
    used: Set<String>,
    multActive: Boolean,
    onUse: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.width(6.dp))
        POWER_UPS.forEach { p ->
            AssistChip(
                enabled = !used.contains(p.id),
                onClick = { onUse(p.id) },
                label = { Text("${p.emoji} ${p.title}") }
            )
            Spacer(Modifier.width(4.dp))
        }
        if (multActive) Text("✌️ x2", fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun BattleScreen(vm: GameViewModel) {
    val ui by vm.ui.collectAsState()
    val now = System.currentTimeMillis()
    val total = (ui.p1Score + ui.p2Score).coerceAtLeast(1)
    val p1Frac = ui.p1Score.toFloat() / total

    Column(Modifier.fillMaxSize()) {
        TapHalf(
            bg = Color(0xFF2A9D8F),
            name = ui.player2Name,
            score = ui.p2Score,
            combo = ui.p2Combo,
            target = ui.p2Target,
            frozen = now < ui.p2FrozenUntil,
            onTap = { vm.tapP2(it) },
            modifier = Modifier.weight(1f)
        )

        Surface(shadowElevation = 8.dp) {
            Column(Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (ui.countdown > 0) {
                    Text("${ui.countdown}", fontSize = 36.sp, fontWeight = FontWeight.Black)
                } else if (ui.overtime) {
                    Text("⚡ ضربه طلایی! اولین برخورد می‌بره", fontWeight = FontWeight.Black, color = Color(0xFFE76F51))
                    Text("${ui.p1Score} : ${ui.p2Score}", fontWeight = FontWeight.Bold)
                } else {
                    val sec = (ui.timeLeftMs / 1000).toInt() + 1
                    LinearProgressIndicator(
                        progress = { ui.timeLeftMs / (ui.durationSec * 1000f) },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("$sec ثانیه  •  ${ui.p1Score} : ${ui.p2Score}", fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp))) {
                        Box(Modifier.weight(p1Frac.coerceAtLeast(0.02f)).fillMaxHeight().background(Color(0xFFE76F51)))
                        Box(Modifier.weight((1 - p1Frac).coerceAtLeast(0.02f)).fillMaxHeight().background(Color(0xFF2A9D8F)))
                    }
                    Spacer(Modifier.height(4.dp))
                    PowerRow(label = "۲:", used = ui.p2Used, multActive = now < ui.p2MultiplierUntil, onUse = { vm.usePowerUp(2, it) })
                    PowerRow(label = "۱:", used = ui.p1Used, multActive = now < ui.p1MultiplierUntil, onUse = { vm.usePowerUp(1, it) })
                    if (ui.mode == Mode.VsBot) {
                        Text("حالت ضد ربات", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        TapHalf(
            bg = Color(0xFFE76F51),
            name = ui.player1Name,
            score = ui.p1Score,
            combo = ui.p1Combo,
            target = ui.p1Target,
            frozen = now < ui.p1FrozenUntil,
            onTap = { vm.tapP1(it) },
            modifier = Modifier.weight(1f)
        )
    }
}
