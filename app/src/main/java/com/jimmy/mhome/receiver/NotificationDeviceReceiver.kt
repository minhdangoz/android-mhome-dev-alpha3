/*
 * *
 *  * Created by Nguyen Huu Khoa on 4/22/20 5:46 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 4/22/20 5:46 PM
 *
 */
package com.jimmy.mhome.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager

class NotificationDeviceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pm = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
        val isScreenOn = pm.isInteractive
        if (!isScreenOn) {
            @SuppressLint("InvalidWakeLockTag") val wl = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "MyLock"
            )
            wl.acquire(10000)
            @SuppressLint("InvalidWakeLockTag") val wl_cpu =
                pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyCpuLock")
            wl_cpu.acquire(10000)
        }
    }
}