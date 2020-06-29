package com.phong.teamcnpm.teambuilding.ui.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.ui.BaseView
import com.phong.teamcnpm.teambuilding.ui.main.event.EventView
import com.phong.teamcnpm.teambuilding.utils.extensions.gone
import com.phong.teamcnpm.teambuilding.utils.extensions.handleToStateEvent
import com.phong.teamcnpm.teambuilding.utils.extensions.show
import kotlinx.android.synthetic.main.item_button_add_event.view.*
import kotlinx.android.synthetic.main.item_event.view.*
import kotlinx.android.synthetic.main.item_progress_recycler.view.*

class EventAdapter(
  private val fromView: BaseView,
  private val listener: ItemEventListener,
  sharedPrefsApi: SharedPrefsApi
) : ListAdapter<Event, RecyclerView.ViewHolder>(EventDiffUtils()) {

  private val mRole = sharedPrefsApi.get(SharedPrefsKey.ROLE_CURRENT_USER, String::class.java)
  private var mIsLoading = false

  override fun submitList(list: MutableList<Event>?) {
    list?.add(0, Event())
    list?.add(Event())
    super.submitList(list)
  }

  override fun getCurrentList(): MutableList<Event> {
    return super.getCurrentList().filter { it.hasId() }.toMutableList()
  }

  override fun getItemViewType(position: Int): Int {
    return when (position) {
      0 -> BUTTON_ADD_TYPE
      currentList.size + 1 -> LOADING_TYPE
      else -> EVENT_ITEM_TYPE
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      BUTTON_ADD_TYPE -> ButtonAddEventViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_button_add_event, parent, false),
        listener.onButtonAddEventClickListener
      )
      EVENT_ITEM_TYPE -> EventViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false),
        listener.onItemClickListener,
        listener.onActionEventClickListener,
        listener.onStateClickListener,
        listener.onCommentClickListener
      )
      else -> LoadingEventViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_progress_recycler, parent, false)
      )
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (fromView is EventView) {
      (holder as? EventViewHolder)?.bind(currentList[position - 1])
      (holder as? ButtonAddEventViewHolder)?.bind(false, mRole)
      (holder as? LoadingEventViewHolder)?.bind(false)
    } else {
      (holder as? EventViewHolder)?.bindDetail(currentList[position - 1], mRole)
      (holder as? ButtonAddEventViewHolder)?.bind(true, mRole)
      (holder as? LoadingEventViewHolder)?.bind(mIsLoading)
    }
  }

  class EventViewHolder(
    itemView: View,
    private val onItemClickListener: (eventId: String) -> Unit,
    private val onActionEventClickListener: (actionName: String, eventId: String) -> Unit,
    private val onStateClickListener: (state: String) -> Unit,
    private val onCommentClickListener: (eventId: String) -> Unit
  ) : RecyclerView.ViewHolder(itemView) {
    private val mTitle: TextView = itemView.titleTextView
    private val mState: TextView = itemView.stateTextView
    private val mWriteComment: TextView = itemView.commentTextView
    private val mLike: MaterialButton = itemView.numberLikeTextView
    private val mDisLike: MaterialButton = itemView.numberDisLikeTextView
    private val mVote: MaterialButton = itemView.numberVoteTextView
    private val mLikeButton: ImageView = itemView.likeImageView
    private val mContainerAction: ConstraintLayout = itemView.containerAction
    private val mDisLikeButton: ImageView = itemView.dislikeImageView
    private val mContents: Map<String, TextView> = mapOf(
      "Time:" to itemView.content1TextView,
      "Address: " to itemView.content2TextView,
      "Number of participants: " to itemView.content3TextView,
      "Message: " to itemView.content4TextView
    )
    private var mEvent: Event? = null
    private var mRole: String = ""
    private var mCurrentLike: String = ""

    init {
      handleEvents()
    }

    fun bind(event: Event) {
      mEvent = event
      mContainerAction.gone()
      mTitle.text = event.name
      event.description.split("<tb>").forEachIndexed { index, s ->
        if (index >= 4) return@forEachIndexed
        mContents.values.toList()[index].text = "${mContents.keys.toList()[index]} $s"
      }
      mState.handleToStateEvent(event.state)
      mLike.text = event.likeCount.toString()
      mDisLike.text = event.dislikeCount.toString()
      mVote.text = event.feedbackCount.toString()
      mVote.isClickable = false
      mLike.isClickable = false
      mDisLike.isClickable = false
    }

    fun bindDetail(event: Event, role: String) {
      mEvent = event
      mRole = role
      mCurrentLike = event.likeCurrent?.type ?: ""
      handleVoteButton()
      mContainerAction.show()
      mVote.isClickable = true
      mLike.isClickable = true
      mDisLike.isClickable = true
      mTitle.text = event.name
      event.description.split("<tb>").forEachIndexed { index, s ->
        if (index >= 4) return@forEachIndexed
        mContents.values.toList()[index].text = "${mContents.keys.toList()[index]} $s"
      }
      mLike.text = event.likeCount.toString()
      mDisLike.text = event.dislikeCount.toString()
      mVote.text = event.feedbackCount.toString()
      mState.handleToStateEvent(event.state)
    }

    private fun handleVoteButton() {
      when (mCurrentLike) {
        "DISLIKE" -> {
          mLikeButton.setImageDrawable(
            ContextCompat.getDrawable(
              itemView.context,
              R.drawable.ic_like_none
            )
          )
          mDisLikeButton.setImageDrawable(
            ContextCompat.getDrawable(
              itemView.context,
              R.drawable.ic_dislike
            )
          )
        }
        "LIKE" -> {
          mLikeButton.setImageDrawable(
            ContextCompat.getDrawable(
              itemView.context,
              R.drawable.ic_like
            )
          )
          mDisLikeButton.setImageDrawable(
            ContextCompat.getDrawable(
              itemView.context,
              R.drawable.ic_dislike_none
            )
          )
        }
        else -> {
          mLikeButton.setImageDrawable(
            ContextCompat.getDrawable(
              itemView.context,
              R.drawable.ic_like_none
            )
          )
          mDisLikeButton.setImageDrawable(
            ContextCompat.getDrawable(
              itemView.context,
              R.drawable.ic_dislike_none
            )
          )
        }
      }
    }

    private fun handleEvents() {
      itemView.setOnClickListener {
        if (mRole == "2" || mRole.isEmpty())
          mEvent?.apply { onItemClickListener.invoke(id) }
      }
      mState.setOnClickListener {
        onStateClickListener(mState.text.toString())
      }
      mLike.setOnClickListener {
        onActionEventClickListener.invoke("LIKE", mEvent?.id ?: "")
      }
      mDisLike.setOnClickListener {
        onActionEventClickListener.invoke("DISLIKE", mEvent?.id ?: "")
      }
      mWriteComment.setOnClickListener {
        mEvent?.apply { onCommentClickListener.invoke(id) }
      }
      mLikeButton.setOnClickListener {
        if (mRole.isEmpty()) return@setOnClickListener
        if (mCurrentLike == "LIKE") {
          onActionEventClickListener.invoke("NONE", mEvent?.id ?: "")
          mCurrentLike = "NONE"
          handleVoteButton()
        } else {
          onActionEventClickListener.invoke("LIKED", mEvent?.id ?: "")
          mCurrentLike = "LIKE"
          handleVoteButton()
        }
      }
      mDisLikeButton.setOnClickListener {
        if (mRole.isEmpty()) return@setOnClickListener
        if (mCurrentLike == "DISLIKE") {
          onActionEventClickListener.invoke("NONE", mEvent?.id ?: "")
          mCurrentLike = "NONE"
          handleVoteButton()
        } else {
          onActionEventClickListener.invoke("DISLIKED", mEvent?.id ?: "")
          mCurrentLike = "DISLIKE"
          handleVoteButton()
        }
      }
    }
  }

  class ButtonAddEventViewHolder(
    itemView: View,
    private val onButtonAddEventClickListener: () -> Unit
  ) : RecyclerView.ViewHolder(itemView) {
    private val mButtonAddEvent: MaterialButton = itemView.addEventButton

    init {
      mButtonAddEvent.setOnClickListener {
        onButtonAddEventClickListener()
      }
    }

    fun bind(isScreenDetail: Boolean, role: String) {
      if (role == "2" && isScreenDetail) {
        mButtonAddEvent.show()
      } else {
        mButtonAddEvent.gone()
      }
    }
  }

  class LoadingEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mProgress: ProgressBar = itemView.loadMoreProgressBar
    fun bind(isLoading: Boolean) {
      if (isLoading) {
        mProgress.show()
      } else {
        mProgress.gone()
      }
    }
  }

  companion object {
    const val BUTTON_ADD_TYPE = 0
    const val EVENT_ITEM_TYPE = 1
    const val LOADING_TYPE = 2
  }
}

class EventDiffUtils : DiffUtil.ItemCallback<Event>() {
  override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
    return !(oldItem.hasId() && newItem.hasId() && oldItem.id != newItem.id)
  }

  override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
    return !(oldItem.hasId() && newItem.hasId() && oldItem != newItem)
  }
}

interface ItemEventListener {
  val onItemClickListener: (eventId: String) -> Unit
  val onActionEventClickListener: (actionName: String, eventId: String) -> Unit
  val onStateClickListener: (state: String) -> Unit
  val onButtonAddEventClickListener: () -> Unit
  val onCommentClickListener: (eventId: String) -> Unit
}