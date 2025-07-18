package ge.mino.androidmessengerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.mino.androidmessengerapp.databinding.ChatitemBinding

class ChatItemAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, ChatItemAdapter.ViewHolder>(UserDiffCallback()) {

    class ViewHolder(val binding: ChatitemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)

        holder.binding.accountName.text = user.nickname
        holder.binding.lastMessage.text = user.lastMessage ?: "No messages yet"

        Glide.with(holder.itemView.context)
            .load(user.profileImageUrl)
            .into(holder.binding.profileImage)

        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
