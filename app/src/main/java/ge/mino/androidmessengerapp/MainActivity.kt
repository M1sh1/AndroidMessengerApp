package ge.mino.androidmessengerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ge.mino.androidmessengerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


