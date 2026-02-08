package com.example.weatherapp.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.getValue
import kotlin.toString

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private val auth by lazy { FirebaseAuth.getInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.txtRecPass.setOnClickListener {
            val i = Intent(this, RecPassActivity::class.java)
            startActivity(i)
        }

        binding.txtRegistoLogin.setOnClickListener {
            val i = Intent(this, RegistoActivity::class.java)
            startActivity(i)
        }

        binding.btnLogin.setOnClickListener {

            try {
                val email = binding.txtEmailLogin.editText?.text.toString()
                val password = binding.txtPasswordLogin.editText?.text.toString()

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(this, "O campo não pode estar vazio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener

                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Login efetuado com sucesso", Toast.LENGTH_SHORT)
                            .show()
                        val i = Intent(this, MainActivity::class.java)
                        i.putExtra("email", email)
                        startActivity(i)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
    }
}