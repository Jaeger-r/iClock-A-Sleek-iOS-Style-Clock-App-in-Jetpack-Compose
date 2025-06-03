package com.example.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun StopwatchPage() {
    var isRunning by remember { mutableStateOf(false) }
    var timeInMillis by remember { mutableStateOf(0L) }
    val lapTimes = remember { mutableStateListOf<String>() }
    var lapCount by remember { mutableStateOf(1) }

    val coroutineScope = rememberCoroutineScope()

    // 启动计时器协程
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                delay(10L)  // 每 10 毫秒刷新
                timeInMillis += 10
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("秒表", fontSize = 32.sp, color = Color.White)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 时间显示
        Text(
            text = formatTime(timeInMillis),
            fontSize = 48.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        // 计次列表
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(lapTimes.reversed()) { lap ->
                Text(
                    text = lap,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // 按钮行
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            // 计次 / 复位 按钮
            Button(
                onClick = {
                    if (isRunning) {
                        lapTimes.add("计次 $lapCount - ${formatTime(timeInMillis)}")
                        lapCount++
                    } else {
                        timeInMillis = 0L
                        lapTimes.clear()
                        lapCount = 1
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray
                )
            ) {
                Text(if (isRunning) "计次" else "复位")
            }

            // 开始 / 暂停 按钮
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color.Red else Color(0xFF00C853)
                )
            ) {
                Text(if (isRunning) "暂停" else "开始")
            }
        }
    }
}

// 时间格式化为 00:00.00
fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val centiseconds = (ms % 1000) / 10
    return "%02d:%02d.%02d".format(minutes, seconds, centiseconds)
}

