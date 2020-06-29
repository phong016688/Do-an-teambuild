package com.phong.teamcnpm.teambuilding.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.domain.entities.Notification
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import com.phong.teamcnpm.teambuilding.utils.extensions.loadFromUrl
import com.phong.teamcnpm.teambuilding.utils.extensions.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_notification.view.*
import kotlinx.android.synthetic.main.item_statistical.view.*
import java.util.concurrent.TimeUnit

class HomeAdapter(
  private val compositeDisposable: CompositeDisposable,
  private val prefsApi: SharedPrefsApi
) :
  ListAdapter<Notification, RecyclerView.ViewHolder>(HomeDiffUtil()) {

  override fun getItemCount(): Int {
    return super.getItemCount() + 1
  }

  override fun getItemViewType(position: Int): Int {
    return when (position) {
      0 -> ITEM_STATISTICAL_TYPE
      else -> ITEM_NOTIFICATION_TYPE
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      ITEM_STATISTICAL_TYPE -> {
        StatisticalViewHolder(
          LayoutInflater.from(parent.context).inflate(R.layout.item_statistical, parent, false),
          compositeDisposable,
          prefsApi
        )
      }
      else -> {
        NotificationViewHolder(
          LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false), compositeDisposable
        )
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is NotificationViewHolder) {
      holder.bind(currentList[position - 1])
    }
  }

  class StatisticalViewHolder(
    itemView: View,
    compositeDisposable: CompositeDisposable,
    prefsApi: SharedPrefsApi
  ) : RecyclerView.ViewHolder(itemView) {
    private val progressBar: ProgressBar = itemView.progressBarBackGround
    private val textProgress: TextView = itemView.textProgress
    private var progressValue = 0

    init {
      Observable.just(Unit)
        .delay(1200, TimeUnit.MILLISECONDS, Schedulers.io())
        .flatMap {
          val value1 = prefsApi.get(SharedPrefsKey.HOME_FRAGMENT_1, Int::class.java)
          val value2 = prefsApi.get(SharedPrefsKey.HOME_FRAGMENT_2, Int::class.java)
          progressValue = if(value1 != 0 && value2 != 0) value1*100/value2 else 0
          val toValue1 = if (progressValue + 30 < 100) progressValue + 30 else 100
          val toValue2 = if (progressValue - 20 > 0) progressValue - 20 else 0
          val toValue3 = if (progressValue + 10 < 100) progressValue + 10 else 100
          val toValue4 = if (progressValue - 5 > 0) progressValue - 5 else 0
          Observable.range(0, progressValue + 2 * (toValue1 + toValue3 - toValue2 - toValue4))
            .concatMap { state ->
              val timeDelay: Long = if (state > 120) state.toLong() - 120L else 0L
              Observable.timer(timeDelay + 12L, TimeUnit.MILLISECONDS)
                .map { state }
            }
            .map {
              when {
                it < toValue1 -> {
                  1
                }
                it < (2 * toValue1 - toValue2) -> {
                  -1
                }
                it < (2 * toValue1 - 2 * toValue2 + toValue3) -> {
                  1
                }
                it < (2 * toValue1 - 2 * toValue2 + 2 * toValue3 - toValue4) -> {
                  -1
                }
                else -> {
                  1
                }
              }
            }
        }
        .doOnSubscribe { progressBar.progress = 0 }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          progressBar.progress += it
          textProgress.text = progressBar.progress.toString() + " %"
        }.addTo(compositeDisposable)
    }
  }

  class NotificationViewHolder(
    itemView: View,
    compositeDisposable: CompositeDisposable
  ) :
    RecyclerView.ViewHolder(itemView) {
    private val image: ImageView = itemView.imageNotificationView
    private val title: TextView = itemView.titleNotificationTextView
    private val content: TextView = itemView.contentNotificationTextView

    init {
      itemView.clicks().map { title.text.toString() }.subscribe {
        itemView.context.toast(it)
      }.addTo(compositeDisposable)
    }

    fun bind(notification: Notification) {
      image.loadFromUrl(notification.image, itemView.context, 60, 60)
      title.text = notification.title
      content.text = notification.content
    }
  }

  companion object {
    const val ITEM_STATISTICAL_TYPE = 0
    const val ITEM_NOTIFICATION_TYPE = 1
  }

}

class HomeDiffUtil : DiffUtil.ItemCallback<Notification>() {
  override fun areItemsTheSame(
    oldItem: Notification,
    newItem: Notification
  ): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(
    oldItem: Notification,
    newItem: Notification
  ): Boolean = oldItem.id == newItem.id
}
