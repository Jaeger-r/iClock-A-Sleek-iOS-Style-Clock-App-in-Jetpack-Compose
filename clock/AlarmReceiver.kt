package com.example.clock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=周日, 2=周一...

        val repeat = intent.getStringExtra("repeat") ?: "仅一次"

        val isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
        val isWeekday = (dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY)

        if (
            repeat == "仅一次" ||
            (repeat == "工作日" && isWeekday) ||
            (repeat == "周末" && isWeekend) ||
            repeat == "每天"
        ) {
            val alarmIntent = Intent(context, AlarmAlertActivity::class.java)
            alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(alarmIntent)
        }
    }
}