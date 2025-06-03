package com.example.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun TimerPage() {
    var totalSeconds by remember { mutableStateOf(0) }
    var remainingSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isSet by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
        if (remainingSeconds == 0 && isRunning) {
            isRunning = false
            // 可在此处添加响铃提示等逻辑
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("计时器", fontSize = 32.sp, color = Color.White)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = formatTime(remainingSeconds),
            fontSize = 64.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 时间选择器（仅在未设置或点击重新设置时显示）
        if (!isRunning && !isSet) {
            TimePicker(
                onTimeSelected = { minutes ->
                    totalSeconds = minutes * 60
                    remainingSeconds = totalSeconds
                    isSet = true
                }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (!isRunning && isSet) {
                Button(onClick = { isRunning = true }) {
                    Text("开始")
                }
            } else if (isRunning) {
                Button(onClick = { isRunning = false }) {
                    Text("暂停")
                }
            }
            if (isSet) {
                Button(onClick = {
                    isRunning = false
                    remainingSeconds = totalSeconds
                }) {
                    Text("重置")
                }
                Button(onClick = {
                    isRunning = false
                    isSet = false
                    totalSeconds = 0
                    remainingSeconds = 0
                }) {
                    Text("重新设置")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TimePicker(onTimeSelected: (Int) -> Unit) {
    var minutes by remember { mutableStateOf(1) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("选择时间", fontSize = 20.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { if (minutes > 1) minutes-- }) {
                Text("-")
            }

            Text(
                text = "$minutes 分钟",
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.White
            )

            Button(onClick = { if (minutes < 60) minutes++ }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onTimeSelected(minutes) }) {
            Text("设置")
        }
    }
}

fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}