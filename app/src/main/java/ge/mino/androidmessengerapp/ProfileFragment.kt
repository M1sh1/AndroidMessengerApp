package ge.mino.androidmessengerapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ge.mino.androidmessengerapp.databinding.FragmentHomepageBinding
import ge.mino.androidmessengerapp.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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

        // Set nickname based on email prefix
        binding.etNickname.setText(currentUser?.email?.substringBefore("@"))

        // Set default avatar
        binding.imgAvatar.setImageResource(R.drawable.avatar_image_placeholder)

        // Load occupation from Firebase
        if (uid != null) {
            val dbRef = FirebaseDatabase
                .getInstance("https://messengerapp-73fa0-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(uid)

            dbRef.child("occupation").get().addOnSuccessListener { dataSnapshot ->
                val occupation = dataSnapshot.getValue(String::class.java)
                binding.occupationProfile.setText(occupation ?: "")
            }.addOnFailureListener { exception ->
                Log.e("Firebase", "Failed to load occupation", exception)
            }
        }

        binding.imgAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*") // Open gallery
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
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImageToFirebase(it)
        }
    }
    private fun uploadImageToFirebase(imageUri: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance()
            .getReference("profile_images/$uid.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Save URL to Firebase Realtime Database
                    FirebaseDatabase.getInstance("https://androidmessengerapp-73903-default-rtdb.firebaseio.com/")
                        .getReference("users")
                        .child(uid)
                        .child("profileImageUrl")
                        .setValue(downloadUri.toString())

                    // Update avatar on screen
                    Glide.with(this)
                        .load(downloadUri)
                        .into(binding.imgAvatar)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Upload failed", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}