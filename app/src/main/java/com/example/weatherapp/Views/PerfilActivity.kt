package com.example.weatherapp.Views

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPerfilBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    //-----------------------------------------------------------------------------------
    private lateinit var imagem: ImageView
    private lateinit var caminho: Uri
    private val TirarFoto = registerForActivityResult(ActivityResultContracts.TakePicture()){
            success ->
        if (success){
            imagem.setImageURI(caminho)
        }

    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val fotoFicheiro = createImageFile()
        caminho = FileProvider.getUriForFile(
            this,
            "com.example.weatherapp.fileprovider",
            fotoFicheiro
        )
        TirarFoto.launch(
            caminho
        )
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw IllegalStateException("Não foi possível acessar o diretório de imagens")

        // garante que a pasta existe
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    private val EscolhaFoto = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
            uri ->
        if (uri!=null){
            imagem.setImageURI(caminho)
        }

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

        //configurar acesso à camera
        imagem = binding.imgFotoProfile
        val btnTirarFoto = binding.btnTirarFoto
        val btnEscolherImagem = binding.btnEscolherFoto

        binding.btnTirarFoto.setOnClickListener {
            if(checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                launchCamera()
            }else{
                requestPermission.launch(android.Manifest.permission.CAMERA)
            }

        }

        binding.btnEscolherFoto.setOnClickListener {
            EscolhaFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }



        val emailRecebido = intent.getStringExtra("email")
        binding.txtEmail.setText(emailRecebido)
        binding.txtEmail.isEnabled = false

        carregarDadosPerfil()

        // Evento: atualizar perfil
        binding.btnAtualizar.setOnClickListener {
            gravarDadosPerfil()
        }

        // Evento: eliminar perfil
        binding.btnEliminar.setOnClickListener {
            eliminarPerfil()
        }

        // Evento: voltar
        binding.txtVoltar.setOnClickListener {
            val i = Intent(this, MainLoginActivity::class.java)
            startActivity(i)
            finish()
        }

        // Evento: sair
        binding.btnSair.setOnClickListener {
            auth.signOut()
            val i = Intent(this, MainLoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    // Carregar dados do perfil
    private fun carregarDadosPerfil() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("PerfilUser")
            .document(uid)
            .get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    binding.txtNome.setText(documento.getString("nome") ?: "")
                    binding.txtCidade.setText(documento.getString("cidade") ?: "")
                    binding.txtContacto.setText(documento.getString("contacto") ?: "")
                } else {
                    Toast.makeText(this, "Nenhuma informação encontrada", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Gravar ou atualizar dados do perfil
    private fun gravarDadosPerfil() {
        val uid = auth.currentUser?.uid ?: return

        val dados = mapOf(
            "id" to uid,
            "nome" to binding.txtNome.text.toString(),
            "cidade" to binding.txtCidade.text.toString(),
            "contacto" to binding.txtContacto.text.toString(),
            "email" to binding.txtEmail.text.toString()
        )

        db.collection("PerfilUser")
            .document(uid)
            .set(dados) // cria ou atualiza o documento
            .addOnSuccessListener {
                Toast.makeText(this, "Dados guardados com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao guardar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Eliminar perfil completo
    private fun eliminarPerfil() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("PerfilUser")
            .document(uid)
            .delete()
            .addOnSuccessListener {
                auth.currentUser?.delete()?.addOnCompleteListener {
                    Toast.makeText(this, "Conta eliminada com sucesso!", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao eliminar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
