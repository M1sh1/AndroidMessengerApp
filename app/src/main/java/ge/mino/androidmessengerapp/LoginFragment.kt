package ge.mino.androidmessengerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import ge.mino.androidmessengerapp.databinding.FragmentLoginBinding
import kotlin.text.isNotEmpty

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }


        binding.btnSignIn.setOnClickListener{
            logIn()
        }

        return binding.root
    }

    private fun logIn(){
        val nickname = binding.etNickname.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        auth = FirebaseAuth.getInstance()
        val fakeEmail = "$nickname@myapp.fake"

        if(nickname.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(fakeEmail, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HomepageFragment())
                        .commit()

                } else {
                    val error = task.exception?.message ?: "Unknown error"
                    Toast.makeText(requireContext(), "Login failed: $error", Toast.LENGTH_SHORT).show()
                }
            }


        }else{
            Toast.makeText(requireContext(), "Please Fill all required fields", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
