// AlarmPage.kt
package com.example.clock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import android.os.Build
import android.provider.Settings
import android.net.Uri
import android.widget.Toast

data class AlarmItem(val time: String, val repeat: String, val note: String)

object AlarmStorage {
    private const val PREF_NAME = "alarm_prefs"
    private const val KEY = "alarms"
    private val gson = Gson()

    fun save(context: Context, list: List<AlarmItem>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY, gson.toJson(list)).apply()
    }

    fun load(context: Context): List<AlarmItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return try {
            val raw = prefs.getString(KEY, null) ?: return emptyList()
            val type = object : TypeToken<List<AlarmItem>>() {}.type
            gson.fromJson(raw, type) ?: emptyList()
        } catch (e: Exception) {
            prefs.edit().remove(KEY).apply()
            emptyList()
        }
    }
}

@Composable
fun AlarmPage() {
    val context = LocalContext.current
    val alarms = remember { mutableStateListOf<AlarmItem>() }
    val enabledMap = remember { mutableStateMapOf<AlarmItem, Boolean>() }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val saved = AlarmStorage.load(context)
        alarms.addAll(saved)
        saved.forEach { enabledMap[it] = true }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("闹钟", fontSize = 32.sp, color = Color.White)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(alarms) { alarm ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(alarm.time, color = Color.White, fontSize = 20.sp)
                            Text(alarm.repeat, color = Color.LightGray, fontSize = 14.sp)
                            if (alarm.note.isNotBlank())
                                Text("备注: ${alarm.note}", color = Color.Gray, fontSize = 12.sp)
                        }
                        Switch(checked = enabledMap[alarm] == true, onCheckedChange = { enabledMap[alarm] = it })
                        IconButton(onClick = {
                            alarms.remove(alarm)
                            enabledMap.remove(alarm)
                            AlarmStorage.save(context, alarms)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }

        Button(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("添加闹钟")
        }
    }

    if (showDialog) {
        AddAlarmDialog(onDismiss = { showDialog = false }) { time, repeat, note, calendar ->
            val newAlarm = AlarmItem(time, repeat, note)
            alarms.add(newAlarm)
            enabledMap[newAlarm] = true
            AlarmStorage.save(context, alarms)
            showDialog = false
            setSystemAlarm(context, calendar, repeat)
        }
    }
}

@Composable
fun AddAlarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Calendar) -> Unit
) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var repeat by remember { mutableStateOf("仅一次") }
    val repeatOptions = listOf("仅一次", "每天", "工作日", "周末")
    var repeatMenuExpanded by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            color = Color.DarkGray,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("添加闹钟", color = Color.White, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                AndroidView(
                    factory = {
                        TimePicker(it).apply {
                            setIs24HourView(true)
                            hour = calendar.get(Calendar.HOUR_OF_DAY)
                            minute = calendar.get(Calendar.MINUTE)
                            setOnTimeChangedListener { _, h, m ->
                                calendar.set(Calendar.HOUR_OF_DAY, h)
                                calendar.set(Calendar.MINUTE, m)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("重复", color = Color.White)
                Box {
                    OutlinedTextField(
                        value = repeat,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { repeatMenuExpanded = !repeatMenuExpanded }) {
                                Icon(
                                    imageVector = if (repeatMenuExpanded)
                                        Icons.Default.KeyboardArrowUp
                                    else
                                        Icons.Default.KeyboardArrowDown,
                                    contentDescription = "展开菜单",
                                    tint = Color.White
                                )
                            }
                        },
                        singleLine = true,
                        label = { Text("重复", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )
                    DropdownMenu(
                        expanded = repeatMenuExpanded,
                        onDismissRequest = { repeatMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth().background(Color.DarkGray)
                    ) {
                        repeatOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = Color.White) },
                                onClick = {
                                    repeat = option
                                    repeatMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (note.isEmpty()) {
                                Text("备注", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text("取消", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        val time = String.format(
                            "%02d:%02d",
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE)
                        )
                        // 调整触发时间为未来
                        if (calendar.before(Calendar.getInstance())) {
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                        }
                        onConfirm(time, repeat, note, calendar)
                    }) {
                        Text("确定", color = Color.White)
                    }
                }
            }
        }
    }
}

fun setSystemAlarm(context: Context, calendar: Calendar, repeat: String) {
    // Android 12+ 权限检查
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(context, "请授予精确闹钟权限", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:" + context.packageName)
            }
            context.startActivity(intent)
            return
        }
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("repeat", repeat)
    }

    val requestCode = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerTime = calendar.timeInMillis

    when (repeat) {
        "仅一次" -> {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
        "每天", "工作日", "周末" -> {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }
}