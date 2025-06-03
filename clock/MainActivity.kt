package com.example.clock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clock.ui.theme.ClockTheme

class MainActivity : ComponentActivity() {//基础activate类
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {//compose提供的入口函数
            ClockTheme {
                val selectedIndex = remember { mutableStateOf(0) }//初始显示界面为0代表世界时钟界面

                Surface(//背景容器
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 72.dp)
                        ) {
                            ContentForPage(selectedIndex.value)//界面切换函数
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        ) {
                            BottomNavigationBar(
                                selectedIndex = selectedIndex.value,
                                onItemSelected = { selectedIndex.value = it }//当用户点击不同页面时，更新 selectedIndex，触发重组刷新页面
                            )
                        }
                    }
                }
            }
        }
    }
}
