package com.wildonion.taarofbattle.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wildonion.taarofbattle.R
import com.wildonion.taarofbattle.game.GameViewModel
import com.wildonion.taarofbattle.game.Mode

@Composable
fun MenuScreen(vm: GameViewModel) {
    val ui by vm.ui.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_menu),
            contentDescription = "Taarof Battle",
            modifier = Modifier.height(180.dp).clip(RoundedCornerShape(16.dp))
        )
        Spacer(Modifier.height(8.dp))
        Text("نبرد تعارف", fontSize = 36.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Text(
            "Taarof Battle • کی حساب می‌کنه؟ با ضربه بجنگ!",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = ui.player1Name, onValueChange = vm::setP1,
                    label = { Text("بازیکن ۱ (پایین)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = ui.player2Name, onValueChange = vm::setP2,
                    label = { Text("بازیکن ۲ (بالا) یا ربات") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = ui.mode == Mode.TwoPlayers,
                        onClick = { vm.setMode(Mode.TwoPlayers) },
                        label = { Text("دو نفره") }
                    )
                    FilterChip(
                        selected = ui.mode == Mode.VsBot,
                        onClick = { vm.setMode(Mode.VsBot) },
                        label = { Text("ضد ربات") }
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = ui.billInput, onValueChange = vm::setBill,
                    label = { Text("مبلغ صورت‌حساب (تومان، اختیاری)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text("مدت راند", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(10, 15, 20).forEach { s ->
                        FilterChip(
                            selected = ui.durationSec == s,
                            onClick = { vm.setDuration(s) },
                            label = { Text("$s ثانیه") }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { vm.startBattle() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("شروع نبرد  \uD83D\uDD25", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "گوشی رو بین خودتون بذارید. هرکی بیشتر ضربه بزنه می‌بره، بازنده حساب می‌کنه!",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}
