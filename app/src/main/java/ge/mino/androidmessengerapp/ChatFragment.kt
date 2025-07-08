package ge.mino.androidmessengerapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ge.mino.androidmessengerapp.databinding.FragmentChatBinding
import ge.mino.androidmessengerapp.databinding.FragmentHomepageBinding
import ge.mino.androidmessengerapp.databinding.FragmentLoginBinding

class ChatFragment: Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

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
        val userId = arguments?.getString("userId") ?: return
        val nickname = arguments?.getString("nickname") ?: "Unknown"
        val occupation = arguments?.getString("occupation") ?: ""
        val profileImageUrl = arguments?.getString("profileImageUrl") ?: ""

        Log.d("LOG UserId: ", userId)
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

        binding.sendButton.setOnClickListener {
            val messageText = binding.messageField.text.toString()
            if (messageText.isNotBlank()) {
                sendMessageToUser(userId, messageText)
                binding.messageField.text.clear()
            }
        }

    }

    fun sendMessageToUser(receiverId: String, messageText: String) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val messageRef = FirebaseDatabase.getInstance()
            .getReference("messages")
            .push()

        val message = mapOf(
            "sender" to senderId,
            "receiver" to receiverId,
            "message" to messageText,
            "date" to System.currentTimeMillis()
        )

        messageRef.setValue(message)
    }


}