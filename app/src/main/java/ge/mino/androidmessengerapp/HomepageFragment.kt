package ge.mino.androidmessengerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private var originalList: List<User> = emptyList()

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

        binding.searchTool.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim()
                if (query.isNullOrEmpty()) {
                    return
                }
                if (query.length >= 3) {
                    searchUsers(query)
                } else {
                    chatAdapter.submitList(originalList)
                }
            }
        })

        return binding.root
    }

    private fun searchUsers(query: String) {

        val currList = chatAdapter.currentList
        if (currList.isEmpty()) {
            return
        }
        val filtered = currList.filter { user ->
            user.nickname.lowercase().contains(query.lowercase()) == true
        }

        chatAdapter.submitList(filtered)

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
                val lastMessagesMap = mutableMapOf<String, Pair<Long, String>>() // uid -> (timestamp, message)

                for (messageSnap in messageSnapshot.children) {
                    val msg = messageSnap.getValue(Message::class.java) ?: continue

                    val isReceived = msg.receiver == currentUserId
                    val otherUserId = if (isReceived) msg.sender else msg.receiver

                    // Only consider messages related to current user
                    if (msg.sender == currentUserId || msg.receiver == currentUserId) {
                        chattedUserIds.add(otherUserId)

                        if (isReceived) {
                            val existing = lastMessagesMap[otherUserId]
                            if (existing == null || msg.timestamp > existing.first) {
                                lastMessagesMap[otherUserId] = Pair(msg.timestamp, msg.message)
                            }
                        }
                    }
                }

                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        val userList = mutableListOf<User>()

                        for (userSnap in userSnapshot.children) {
                            val uid = userSnap.key ?: continue
                            if (uid != currentUserId && chattedUserIds.contains(uid)) {
                                val user = userSnap.getValue(User::class.java)?.copy(uid = uid)
                                if (user != null) {
                                    user.lastMessage = lastMessagesMap[uid]?.second
                                    userList.add(user)
                                }
                            }
                        }

                        originalList = userList
                        chatAdapter.submitList(userList)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
