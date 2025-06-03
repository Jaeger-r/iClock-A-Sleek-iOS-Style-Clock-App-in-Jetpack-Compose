package com.example.clock

import androidx.compose.runtime.Composable


@Composable
fun ContentForPage(index: Int) {
    when (index) {
        0 -> AnalogClock()
        1 -> AlarmPage()
        2 -> StopwatchPage()
        3 -> TimerPage()
    }
}