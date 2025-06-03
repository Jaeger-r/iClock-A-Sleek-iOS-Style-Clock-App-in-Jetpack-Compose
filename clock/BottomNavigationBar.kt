package com.example.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf("时钟", "闹钟", "秒表", "计时器")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onItemSelected(index) } // 点击切换页面
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime, // 可以自定义图标
                    contentDescription = label,
                    tint = if (selected) Color(0xFFFFA500) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = label,
                    color = if (selected) Color(0xFFFFA500) else Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}