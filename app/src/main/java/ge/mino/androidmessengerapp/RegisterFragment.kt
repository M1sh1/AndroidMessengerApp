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
                            saveUserToDatabase(nickname, occupation)

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, LoginFragment())
                                .commit()

                        } else {
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

        binding.LogIn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }
    }

    private fun saveUserToDatabase(nickname: String, occupation: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("Registration", "No authenticated user - cannot save to DB")
            return
        }

        val uid = currentUser.uid
        Log.d("Registration", "Saving user to DB with UID: $uid")

        val userData = hashMapOf(
            "nickname" to nickname,
            "nicknameLowercase" to nickname.lowercase(),
            "occupation" to occupation,
            "profileImageUrl" to ""
        )

        val dbUrl = "https://androidmessengerapp-73903-default-rtdb.firebaseio.com/"
        val dbRef = FirebaseDatabase.getInstance(dbUrl)
            .getReference("users")
            .child(uid)

        Log.d("Registration", "Database URL: $dbUrl")
        Log.d("Registration", "Database path: users/$uid")

        dbRef.setValue(userData)
            .addOnSuccessListener {
                Log.d("Registration", "User data saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Registration", "Error saving user to DB: ${e.message}", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
