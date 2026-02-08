package com.example.weatherapp.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.weatherapp.Model.GuardarCidades

class MainLoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainLoginBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    val listaCidades = mutableListOf<GuardarCidades>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar RecyclerView
        binding.recyclerViewCidades.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCidades.setHasFixedSize(true)

        val email = intent.getStringExtra("email")
        carregarCidades()

        // Botões
        binding.btnPerfil.setOnClickListener {
            val i = Intent(this, PerfilActivity::class.java)
            i.putExtra("email", email)
            startActivity(i)
        }

        binding.btnSair.setOnClickListener {
            auth.signOut()
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

        binding.btnVoltarCidade.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        // Lottie Animation
        val animationView = findViewById<LottieAnimationView>(R.id.animationView)
        animationView.setAnimation(R.raw.weatherlottie)
        animationView.playAnimation()
    }

    private fun carregarCidades() {
        val userUid = auth.currentUser?.uid
        if (userUid != null) {
            db.collection("usuarios").document(userUid)
                .collection("cidades")
                .get()
                .addOnSuccessListener { resultado ->
                    listaCidades.clear()
                    for (documento in resultado) {
                        val nome = documento.getString("cidade") ?: ""
                        val temperatura = documento.getString("temperatura") ?: ""
                        val idDocumento = documento.id
                        listaCidades.add(
                            GuardarCidades(
                                id = idDocumento,
                                cidade = nome,
                                temperatura = temperatura
                            )
                        )
                    }
                    val adapter = CidadesAdapter(listaCidades) { cidade ->
                        apagarCidade(cidade)
                    }
                    binding.recyclerViewCidades.adapter = adapter
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar cidades!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun apagarCidade(cidade: GuardarCidades) {
        val userUid = auth.currentUser?.uid
        if (userUid != null) {
            db.collection("usuarios")
                .document(userUid)
                .collection("cidades")
                .document(cidade.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Cidade eliminada!", Toast.LENGTH_SHORT).show()
                    listaCidades.remove(cidade)
                    binding.recyclerViewCidades.adapter = CidadesAdapter(listaCidades) { c -> apagarCidade(c) }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao eliminar cidade!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
