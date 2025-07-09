package ge.mino.androidmessengerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mino.androidmessengerapp.databinding.ReceivedBinding
import ge.mino.androidmessengerapp.databinding.SentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val messageList: List<Message>,
    private val currentUserId: String,
    private val VIEW_TYPE_SENT: Int = 1,
    private val VIEW_TYPE_RECEIVED: Int = 2

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender == currentUserId) VIEW_TYPE_SENT
        else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = SentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentViewHolder(binding)
        } else {
            val binding = ReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SentViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messageList.size

    inner class SentViewHolder(private val binding: SentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.lastMessage.text = message.message
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
            binding.sentTime.text = formattedTime
        }
    }

    inner class ReceivedViewHolder(private val binding: ReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.lastMessage.text = message.message
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
            binding.sentTime.text = formattedTime
        }
    }
}
