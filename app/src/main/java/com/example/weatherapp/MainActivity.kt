package com.example.weatherapp
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.Views.MainLoginActivity
import com.example.weatherapp.Views.PerfilActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val auth by lazy { FirebaseAuth.getInstance() }

    private val db by lazy { FirebaseFirestore.getInstance() }

    private val apiKey = "dd57159ed0eb7824d1428eeb69487c7f"


    //Criar um launcher para a Activity de resultado
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        //Este bloco é executado quando a Result Activity retorna um resultado
        //RESULT_OK significa que a Activity retornou o resultado com sucesso
        if (result.resultCode == RESULT_OK) {}
            val data: Intent? = result.data
            val retorno = data?.getStringExtra("retorno_cidade")
            //Exibir uma mensagem com o resultado
            Toast.makeText(this, retorno, Toast.LENGTH_LONG).show()



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        binding.btnEnviar.setOnClickListener {
            // Pega o texto da cidade e remove espaços no início e no fim
            val cidade = binding.editarCidade.text.toString().trim().lowercase()

            //Validar se o campo está vazio.
            if (cidade.isBlank()) {
                Toast.makeText(this, "Por favor, insira uma cidade.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Para a execução da função aqui
            } else {
                val intent = Intent(this, Result::class.java)
                intent.putExtra("cidade_escolhida", cidade)
                resultLauncher.launch(intent)
            }

        }

        binding.btnGoPerfil.setOnClickListener {
            val i = Intent(this, MainLoginActivity::class.java)
            startActivity(i)
        }
    }
}