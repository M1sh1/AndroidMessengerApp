package ge.mino.androidmessengerapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import ge.mino.androidmessengerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)?.let {
            Log.d("Firebase", "Initialized with name: ${it.name}")
        } ?: Log.e("Firebase", "FirebaseApp failed to initialize!")

        val auth = FirebaseAuth.getInstance()

        auth.currentUser?.let { user ->
            Log.d("Auth", "User already logged in: ${user.uid}")
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, HomepageFragment())
                .commit()
        } ?: run {
            Log.d("Auth", "No user logged in")
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, LoginFragment())
                .commit()
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, LoginFragment())
            .commit()
    }

    fun switchToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    fun switchToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, LoginFragment())
            .commit()
    }
}


