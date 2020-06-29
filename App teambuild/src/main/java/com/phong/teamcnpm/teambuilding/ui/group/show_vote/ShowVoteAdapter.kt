package com.phong.teamcnpm.teambuilding.ui.group.show_vote

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.entities.Vote
import com.phong.teamcnpm.teambuilding.utils.extensions.loadFromUrl
import kotlinx.android.synthetic.main.item_vote.view.*

class ShowVoteAdapter(private val usersInGroup: List<User>) :
  ListAdapter<Vote, RecyclerView.ViewHolder>(EventDiffUtils()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return ItemVoteViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_vote, parent, false)
    )
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    (holder as? ItemVoteViewHolder)?.bind(currentList[position])
  }

  inner class ItemVoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mAvatar = itemView.avatarUserVoteImageView
    private val mName = itemView.nameUserVoteTextView

    fun bind(vote: Vote) {
      Log.d("####", vote.idUser)
      usersInGroup.firstOrNull { it.id == vote.idUser }?.apply {
        mAvatar.loadFromUrl(avatar)
        mName.text = name
      }
    }
  }
}

class EventDiffUtils : DiffUtil.ItemCallback<Vote>() {
  override fun areItemsTheSame(oldItem: Vote, newItem: Vote): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: Vote, newItem: Vote): Boolean {
    return oldItem == newItem
  }
}