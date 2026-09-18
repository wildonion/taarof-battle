package com.wildonion.taarofbattle.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wildonion.taarofbattle.game.GameViewModel
import com.wildonion.taarofbattle.util.buildShareText
import com.wildonion.taarofbattle.util.shareResult

@Composable
fun ResultScreen(vm: GameViewModel) {
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("\uD83C\uDFC6", fontSize = 72.sp)
        Text(ui.winnerName, fontSize = 32.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Text("برنده نبرد تعارف شد!", fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (ui.billAmount.isNotBlank()) {
                    Text("😭 ${ui.loserName} باید پرداخت کنه", color = MaterialTheme.colorScheme.onPrimary)
                    Text("${ui.billAmount} تومان", fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("😭 ${ui.loserName} مهمون می‌کنه!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimary)
                }
                Text(ui.funnyLine, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("${ui.p1Score} : ${ui.p2Score} ضربه", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                shareResult(ctx, buildShareText(ui.winnerName, ui.loserName, ui.billAmount, ui.funnyLine))
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) { Text("اشتراک‌گذاری نتیجه") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { vm.startBattle() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) { Text("بازی مجدد \uD83D\uDD25") }
        TextButton(onClick = { vm.goMenu() }) { Text("بازگشت به منو") }
    }
}
