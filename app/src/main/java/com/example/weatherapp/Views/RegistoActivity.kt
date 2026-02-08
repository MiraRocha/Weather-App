package com.example.weatherapp.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.R
import com.google.firebase.auth.FirebaseAuth
import com.example.weatherapp.databinding.ActivityRegistoBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.toString

class RegistoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRegistoBinding.inflate(layoutInflater) }

    private val auth by lazy { FirebaseAuth.getInstance() }

    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.txtVoltarLogin.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnRegistar.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()
            val passwordConfirm = binding.txtPasswordConfirm.text.toString()
            if (password==passwordConfirm)
            {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registado com sucesso", Toast.LENGTH_SHORT).show()
                        auth.currentUser?.sendEmailVerification()
                        gravarUserBD(email)
                    }

                    .addOnFailureListener { Toast.makeText(this, "Erro ao registar", Toast.LENGTH_SHORT).show() }


            }
            else {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun gravarUserBD(email: String) {
        val uid = auth.currentUser?.uid
        val dados = mapOf(
            "email" to email,
            "id" to uid,
            "nome" to "",
            "cidade" to "",
            "contacto" to ""
        )
        db.collection("PerfilUser")
            .document(uid.toString())
            .set(dados)
            .addOnSuccessListener {Toast.makeText(this, "Dados gravados com sucesso", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                //mensagem de expect
                    e -> Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}