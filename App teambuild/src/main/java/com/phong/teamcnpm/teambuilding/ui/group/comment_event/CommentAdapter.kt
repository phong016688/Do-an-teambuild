package com.phong.teamcnpm.teambuilding.ui.group.comment_event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.entities.Comment
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.utils.extensions.convertLongToTime
import com.phong.teamcnpm.teambuilding.utils.extensions.loadFromUrl
import kotlinx.android.synthetic.main.item_comment.view.*
import java.util.*

class CommentAdapter(private val usersInGroup: List<User>) :
  ListAdapter<Comment, RecyclerView.ViewHolder>(EventDiffUtils()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return ItemCommentViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
    )
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    (holder as? ItemCommentViewHolder)?.bind(currentList[position])
  }

  inner class ItemCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mAvatar = itemView.avatarUserCommentTextView
    private val mName = itemView.nameUserCommentTextView
    private val mContent = itemView.contentCommentTextView
    private val mTime = itemView.timeCommentTextView

    fun bind(comment: Comment) {
      usersInGroup.firstOrNull { it.id == comment.idUser }?.apply {
        mAvatar.loadFromUrl(avatar)
        mName.text = name
        mContent.text = comment.content
        mTime.text = Date(comment.createAt).convertLongToTime()
      }
    }
  }
}

class EventDiffUtils : DiffUtil.ItemCallback<Comment>() {
  override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
    return oldItem == newItem
  }
}