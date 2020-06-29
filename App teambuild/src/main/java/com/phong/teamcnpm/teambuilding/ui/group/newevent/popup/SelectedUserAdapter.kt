package com.phong.teamcnpm.teambuilding.ui.group.newevent.popup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.utils.extensions.loadFromUrl
import kotlinx.android.synthetic.main.item_select_user.view.*

class SelectedUserAdapter :
  ListAdapter<Pair<Boolean, User>, SelectedUserAdapter.ItemSelectUser>(
    DiffUtilSelectedUser()
  ) {

  private val onItemCLick: (userId: String) -> Unit = { userId ->
    submitList(currentList.map {
      if (it.second.id == userId)
        !it.first to it.second
      else
        it
    })
  }

  override fun submitList(list: List<Pair<Boolean, User>>?) {
    super.submitList(list?.sortedBy { it.second.id })
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSelectUser {
    return ItemSelectUser(
      LayoutInflater.from(parent.context).inflate(R.layout.item_select_user, parent, false),
      onItemCLick
    )
  }

  override fun onBindViewHolder(holder: ItemSelectUser, position: Int) {
    (holder as? ItemSelectUser)?.bind(currentList[position])
  }

  class ItemSelectUser(itemView: View, onItemCLick: (userId: String) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val mAvatar = itemView.avatarImageView
    private val mName = itemView.nameTextView
    private val mContainer = itemView.container
    private var mUserId: String = ""

    init {
      itemView.setOnClickListener {
        if (mUserId.isNotEmpty())
          onItemCLick(mUserId)
      }
    }

    fun bind(pair: Pair<Boolean, User>) {
      mUserId = pair.second.id
      mAvatar.loadFromUrl(pair.second.avatar)
      mName.text = pair.second.name
      updateBackground(pair.first)
    }

    private fun updateBackground(isSelected: Boolean) {
      if (isSelected) {
        setBackgroundForItemView(R.drawable.blue_stroke_bg)
      } else {
        setBackgroundForItemView(R.drawable.white_stroke_bg)
      }
    }

    private fun setBackgroundForItemView(@DrawableRes id: Int) {
      mContainer.background = ContextCompat.getDrawable(itemView.context, id)
    }
  }

}

class DiffUtilSelectedUser : DiffUtil.ItemCallback<Pair<Boolean, User>>() {

  override fun areItemsTheSame(
    oldItem: Pair<Boolean, User>,
    newItem: Pair<Boolean, User>
  ): Boolean {
    return oldItem.first == newItem.first && oldItem.second.id == newItem.second.id
  }

  override fun areContentsTheSame(
    oldItem: Pair<Boolean, User>,
    newItem: Pair<Boolean, User>
  ): Boolean {
    return oldItem == newItem
  }
}