package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Result : AppCompatActivity() {

    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
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

        val apiKey = BuildConfig.API_KEY

        val cidadeRecebida = intent.getStringExtra("cidade_escolhida")
        if (cidadeRecebida.isNullOrEmpty()) {
            Toast.makeText(this, "Nenhuma cidade recebida!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.txtCidadeRecebida.text = "Cidade: $cidadeRecebida"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val servicoTempo = retrofit.create(ServicoTempo::class.java)

        servicoTempo.getTempoCidade(cidadeRecebida, apiKey, "metric", "pt")
            .enqueue(object : Callback<RespostaTempo> {
                override fun onResponse(call: Call<RespostaTempo>, response: Response<RespostaTempo>) {
                    if (response.isSuccessful) {
                        val tempo = response.body()
                        val temperatura = tempo?.main?.temp?.toInt() ?: 0

                        // Guarda cidade e temperatura no Firebase usando UID
                        val userUid = auth.currentUser?.uid
                        if (userUid != null) {
                            val dadosCidade = hashMapOf(
                                "cidade" to cidadeRecebida,
                                "temperatura" to "${temperatura}ºC"
                            )

                            db.collection("usuarios").document(userUid)
                                .collection("cidades")
                                .add(dadosCidade)
                                .addOnSuccessListener {
                                    Toast.makeText(this@Result, "Cidade guardada!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@Result, "Erro ao guardar cidade!", Toast.LENGTH_SHORT).show()
                                }
                        }


                        // Ícone e mensagem
                        val climaIcone = when (temperatura) {
                            in 10..14 -> R.drawable.cloud3
                            in 15..19 -> R.drawable.cloud2
                            in 20..22 -> R.drawable.nublado
                            in 23..24 -> R.drawable.cloud1
                            in 25..35 -> R.drawable.sol
                            else -> R.drawable.cloud5
                        }

                        val climaMensagem = when (climaIcone) {
                            R.drawable.sol -> "Tempo ensolarado, aproveite o dia!"
                            R.drawable.nublado -> "Tempo nublado, ideal para um passeio."
                            R.drawable.cloud3 -> "Previsão de tempestade nas próximas horas."
                            R.drawable.cloud2 -> "Tempo chuvoso, leve o seu guarda-chuva."
                            R.drawable.cloud1 -> "Vento forte nas próximas horas."
                            else -> "Previsão de noite nublada."
                        }

                        binding.txtTemperatura.text = "${temperatura}ºC"
                        binding.imgClima.setImageResource(climaIcone)
                        binding.txtMensagem.text = climaMensagem
                    } else {
                        Toast.makeText(this@Result, "Cidade não encontrada! Verifica o nome e tenta novamente.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<RespostaTempo>, t: Throwable) {
                    Toast.makeText(this@Result, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        // Botão voltar
        binding.btnVoltar.setOnClickListener {
            val retornoIntent = Intent()
            retornoIntent.putExtra("retorno_cidade", "Pesquisa para '$cidadeRecebida' concluída.")
            setResult(RESULT_OK, retornoIntent)
            finish()
        }
    }
}
