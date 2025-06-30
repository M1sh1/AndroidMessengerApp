package ge.mino.androidmessengerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ge.mino.androidmessengerapp.databinding.AccountitemBinding

class AccountItemAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, AccountItemAdapter.ViewHolder>(UserDiffCallback()) {

    class ViewHolder(val binding: AccountitemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AccountitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.binding.accountName.text = user.nickname
        holder.binding.occupation.text = user.occupation
        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.nickname == newItem.nickname
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}