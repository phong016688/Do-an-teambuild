package com.phong.teamcnpm.teambuilding.utils.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.phong.teamcnpm.teambuilding.R

fun Context.toast(text: String?) = Toast.makeText(this, text ?: "null", Toast.LENGTH_LONG).show()

fun Context.saveMessageToClipBoard(message: String) {
  val clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  clipboardManager.setPrimaryClip(ClipData.newPlainText(this.getString(R.string.app_name), message))
}

fun Context.showSoftKeyboard() {
  val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
  inputMethodManager?.toggleSoftInput(
    InputMethodManager.SHOW_FORCED,
    InputMethodManager.RESULT_UNCHANGED_HIDDEN
  )
}

fun Context.hideSoftKeyboardFromWindow(view: View) {
  val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.getAttrId(attrId: Int): Int {
  TypedValue().run {
    return when {
      !theme.resolveAttribute(attrId, this, true) -> -1
      else -> resourceId
    }
  }
}