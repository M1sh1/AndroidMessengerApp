package ge.mino.androidmessengerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.mino.androidmessengerapp.databinding.FragmentHomepageBinding

class HomepageFragment : Fragment() {
    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadUsers()

        binding.plusButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SearchpageFragment())
                .commit()
        }

        binding.profileButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .commit()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatItemAdapter { selectedUser ->
            val chatFragment = ChatFragment().apply {
                arguments = Bundle().apply {
                    putString("userId", selectedUser.uid)
                    putString("nickname", selectedUser.nickname)
                    putString("occupation", selectedUser.occupation)
                    putString("profileImageUrl", selectedUser.profileImageUrl)
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, chatFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.chatItem.apply {
            binding.chatItem.layoutManager = LinearLayoutManager(context)
            binding.chatItem.adapter = chatAdapter
        }
    }

    private fun loadUsers() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val messagesRef = FirebaseDatabase.getInstance().getReference("messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(messageSnapshot: DataSnapshot) {
                val chattedUserIds = mutableSetOf<String>()

                for (messageSnap in messageSnapshot.children) {
                    val msg = messageSnap.getValue(Message::class.java)
                    if (msg != null) {
                        if (msg.sender == currentUserId) {
                            chattedUserIds.add(msg.receiver)
                        } else if (msg.receiver == currentUserId) {
                            chattedUserIds.add(msg.sender)
                        }
                    }
                }

                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        val userList = mutableListOf<User>()
                        for (userSnap in userSnapshot.children) {
                            val uid = userSnap.key ?: continue
                            if (uid != currentUserId && chattedUserIds.contains(uid)) {
                                val user = userSnap.getValue(User::class.java)
                                if (user != null) {
                                    userList.add(user.copy(uid = uid))
                                }
                            }
                        }
                        chatAdapter.submitList(userList)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
