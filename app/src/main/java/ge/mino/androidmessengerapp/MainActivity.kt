package ge.mino.androidmessengerapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
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


