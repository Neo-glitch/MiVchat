package com.neo.mivchat.util

import android.content.Context
import android.widget.Toast

object Helper {

    fun showMessage(context: Context, message: String, isLong: Boolean = false){
        Toast.makeText(context, message, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}