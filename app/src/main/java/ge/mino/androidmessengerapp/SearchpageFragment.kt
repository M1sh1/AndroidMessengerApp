package ge.mino.androidmessengerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.mino.androidmessengerapp.databinding.FragmentSearchpageBinding

class SearchpageFragment : Fragment() {
    private var _binding: FragmentSearchpageBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AccountItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AccountItemAdapter { user ->
            val bundle = Bundle().apply {
                putString("userId", user.uid)
                putString("nickname", user.nickname)
                putString("occupation", user.occupation)
                putString("profileImageUrl", user.profileImageUrl)
            }

            val chatFragment = ChatFragment().apply {
                arguments = bundle
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, chatFragment)
                .addToBackStack(null)
                .commit()
        }

        debugCheckAllUsers()
        binding.accountItem.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchpageFragment.adapter
            setHasFixedSize(true)
        }

        binding.backicon.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomepageFragment())
                .commit()
        }

        binding.searchTool.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim()
                if (query.isNullOrEmpty()) {
                    adapter.submitList(emptyList())
                    return
                }

                if (query.length >= 3) {
                    searchUsers(query)
                } else {
                    adapter.submitList(emptyList())
                }
            }
        })
    }

    private fun searchUsers(query: String) {
        binding.loadingBar.visibility = View.VISIBLE

        FirebaseDatabase.getInstance("https://androidmessengerapp-73903-default-rtdb.firebaseio.com")
            .getReference("users")
            .orderByChild("nicknameLowercase")
            .startAt(query.lowercase())
            .endAt("${query.lowercase()}\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.children.mapNotNull { userSnapshot ->
                        userSnapshot.getValue(User::class.java)?.copy(
                            uid = userSnapshot.key ?: ""
                        )
                    }
                    adapter.submitList(users)
                    binding.loadingBar.visibility = View.GONE
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun debugCheckAllUsers() {

        FirebaseDatabase.getInstance("https://androidmessengerapp-73903-default-rtdb.firebaseio.com/")
            .getReference("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (!snapshot.exists()) {
                        return
                    }

                    snapshot.children.forEach { userSnapshot ->
                        Log.d("DEBUG", """
                    User ID: ${userSnapshot.key}
                    Data: ${userSnapshot.value}
                    Nickname: ${userSnapshot.child("nickname").value}
                    Lowercase: ${userSnapshot.child("nicknameLowercase").value}
                    """.trimIndent())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DEBUG", "Failed to read users: ${error.message}")
                }
            })
    }
}