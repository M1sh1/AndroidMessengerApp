package ge.mino.androidmessengerapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ge.mino.androidmessengerapp.databinding.FragmentHomepageBinding
import ge.mino.androidmessengerapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Display the selected image immediately
            Glide.with(this)
                .load(it)
                .into(binding.imgAvatar)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        binding.etNickname.setText(currentUser?.email?.substringBefore("@"))
        FirebaseDatabase.getInstance("https://androidmessengerapp-73903-default-rtdb.firebaseio.com/")
            .getReference("users/$uid")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val occupation = snapshot.child("occupation").getValue(String::class.java)
                    binding.occupationProfile.setText(occupation ?: "")
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            )

                    if (selectedImageUri != null) {
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.imgAvatar)
        } else {
            binding.imgAvatar.setImageResource(R.drawable.avatar_image_placeholder)
        }



        binding.imgAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.homeButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomepageFragment())
                .commit()
        }

        binding.btnUpdate.setOnClickListener{

            val newNickname = binding.etNickname.text.toString()
            val newOccupation = binding.occupationProfile.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser

            val userData = hashMapOf<String, Any>(
                "nickname" to newNickname,
                "nicknameLowercase" to newNickname.lowercase(),
                "occupation" to newOccupation,
                "profileImageUrl" to ""
            )
            if(uid != null)
            FirebaseDatabase.getInstance("https://androidmessengerapp-73903-default-rtdb.firebaseio.com/")
                .getReference("users")
                .child(uid)
                .updateChildren(userData)


        }

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}