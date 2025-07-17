package ge.mino.androidmessengerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ge.mino.androidmessengerapp.databinding.FragmentChatBinding
import ge.mino.androidmessengerapp.databinding.FragmentHomepageBinding
import ge.mino.androidmessengerapp.databinding.FragmentLoginBinding

class ChatFragment: Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<Message>()
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var databaseRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        receiverId = arguments?.getString("userId") ?: return
        val nickname = arguments?.getString("nickname") ?: "Unknown"
        val occupation = arguments?.getString("occupation") ?: ""
        val profileImageUrl = arguments?.getString("profileImageUrl") ?: ""
        databaseRef = FirebaseDatabase.getInstance().getReference("messages")

        binding.accountName.text = nickname
        binding.lastMessage.text = occupation
        if (profileImageUrl.isNotEmpty()) {
            Glide.with(requireContext())
                .load(profileImageUrl)
                .placeholder(R.drawable.avatar_image_placeholder)
                .into(binding.profileImage)
        }



        binding.backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        messageAdapter = MessageAdapter(messageList, senderId)
        binding.messageItem.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        displayMessages()

        binding.sendButton.setOnClickListener {
            val messageText = binding.messageField.text.toString()
            if (messageText.isNotBlank()) {
                sendMessageToUser(receiverId, messageText)
                binding.messageField.text.clear()
            }
        }

    }

    private fun sendMessageToUser(receiverId: String, messageText: String) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val messageRef = FirebaseDatabase.getInstance().getReference("messages").push()
        val messageId = messageRef.key ?: return

        val message = Message(
            sender = senderId,
            receiver = receiverId,
            id = messageId,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )

        messageRef.setValue(message)
    }


    private fun displayMessages(){

            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (messageSnap in snapshot.children) {
                        val msg = messageSnap.getValue(Message::class.java)
                        if (msg != null &&
                            ((msg.sender == senderId && msg.receiver == receiverId) ||
                                    (msg.sender == receiverId && msg.receiver == senderId))
                        ) {
                            messageList.add(msg)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                    binding.messageItem.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}