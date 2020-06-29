package com.phong.teamcnpm.teambuilding.utils.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.convertLongToTime(): String {
  val format = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale("vi"))
  return format.format(this)
}

fun String.convertDateToLong(): Long {
  val df = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale("vi"))
  return df.parse(this)?.time ?: System.currentTimeMillis()
}