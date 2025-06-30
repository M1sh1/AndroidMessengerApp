package ge.mino.androidmessengerapp

import android.os.Bundle
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getString("userId") ?: return
        val nickname = arguments?.getString("nickname") ?: "Unknown"
        val occupation = arguments?.getString("occupation") ?: ""
        val profileImageUrl = arguments?.getString("profileImageUrl") ?: ""

        // Set nickname and occupation in toolbar
        binding.accountName.text = nickname
        binding.lastMessage.text = occupation

        // Load profile image if available
        if (profileImageUrl.isNotEmpty()) {
            Glide.with(requireContext())
                .load(profileImageUrl)
                .placeholder(R.drawable.avatar_image_placeholder)
                .into(binding.profileImage)
        }

        // Back button functionality
        binding.backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Send button stub
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageField.text.toString()
            if (messageText.isNotBlank()) {
                sendMessageToUser(userId, messageText)
                binding.messageField.text.clear()
            }
        }

        // TODO: Load messages with userId
    }

    private fun sendMessageToUser(receiverId: String, messageText: String) {
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