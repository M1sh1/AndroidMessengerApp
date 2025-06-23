package ge.mino.androidmessengerapp


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import ge.mino.androidmessengerapp.databinding.FragmentRegisterBinding

class RegisterFragment: Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnSignIn.setOnClickListener {

            val nickname = binding.etNickname.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val occupation = binding.etWhatIDo.text.toString().trim()
            val fakeEmail = "$nickname@myapp.fake"

            if (nickname.isNotEmpty() && password.isNotEmpty() && occupation.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(fakeEmail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FirebaseAuth", "User created: ${auth.currentUser?.uid}")
                            saveUserToDatabase(nickname, occupation)
                        } else {
                            Log.e("FirebaseAuth", "Registration failed", task.exception)
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(requireContext(), "Nickname already used", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToDatabase(nickname: String, occupation: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("FirebaseDB", "User UID is null!")
            return
        }

        val userMap = mapOf(
            "nickname" to nickname,
            "occupation" to occupation
        )

        FirebaseDatabase.getInstance().getReference("users")
            .child(uid)
            .setValue(userMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "User registered!", Toast.LENGTH_SHORT).show()
                Log.d("FirebaseDB", "User data saved for UID: $uid")
                (activity as? MainActivity)?.switchToLogin()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save user data", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseDB", "Database error: ${it.message}")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}