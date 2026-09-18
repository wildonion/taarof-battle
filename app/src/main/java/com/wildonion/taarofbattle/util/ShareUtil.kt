package com.wildonion.taarofbattle.util

import android.content.Context
import android.content.Intent

fun shareResult(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری نبرد تعارف"))
}

fun buildShareText(winner: String, loser: String, bill: String, line: String): String {
    val billLine = if (bill.isNotBlank()) "\n$loser باید $bill تومان پرداخت کنه" else "\n$loser مهمون می‌کنه!"
    return "🎉 نبرد تعارف 🎉\n$winner برد $loser رو!$billLine\n$line\nبازی کن: نبرد تعارف در بازار"
}
