package com.phong.teamcnpm.teambuilding.utils.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.phong.teamcnpm.teambuilding.R

fun View.show() {
  this.visibility = View.VISIBLE
}

fun View.hide() {
  this.visibility = View.INVISIBLE
}

fun View.gone() {
  this.visibility = View.GONE
}

fun ImageView.loadFromUrl(
  url: String,
  context: Context? = null,
  width: Int = 100,
  height: Int = 100
) {
  val urlFinal = if (url.isEmpty()) "https://i.stack.imgur.com/l60Hf.png" else url
  Glide.with(context ?: this.context)
    .load(urlFinal)
    .centerCrop()
    .override(width, height)
    .placeholder(R.drawable.ic_people)
    .into(this)
}

fun TextView.handleToStateEvent(state: String) {
  when (state) {
    "COMPLETED" -> {
      this.text = "COMPLETED"
      this.backgroundTintList =
        ColorStateList.valueOf(
          ContextCompat.getColor(
            this.context,
            R.color.state_completed
          )
        )
    }
    "PROCESSING" -> {
      this.text = "PROCESSING"
      this.backgroundTintList =
        ColorStateList.valueOf(
          ContextCompat.getColor(
            this.context,
            R.color.state_processing
          )
        )
    }
    "CANCELLED" -> {
      this.text = "CANCELLED"
      this.backgroundTintList =
        ColorStateList.valueOf(
          ContextCompat.getColor(
            this.context,
            R.color.state_cancelled
          )
        )
    }
    else -> {
      this.text = ""
      this.backgroundTintList =
        ColorStateList.valueOf(
          ContextCompat.getColor(
            this.context,
            R.color.white1
          )
        )
    }
  }
}