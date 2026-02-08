package com.example.weatherapp.Views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.GuardarCidades
import com.example.weatherapp.R
import com.google.android.material.button.MaterialButton

class CidadesAdapter(
    private val listaCidades: List<GuardarCidades>,
    private val onApagarClick: (GuardarCidades) -> Unit
) : RecyclerView.Adapter<CidadesAdapter.CidadeViewHolder>() {

    inner class CidadeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNome: TextView = view.findViewById(R.id.txtNome)
        val txtTemperatura: TextView = view.findViewById(R.id.txtTemperatura)
        val btnApagar: MaterialButton = view.findViewById(R.id.btnApagar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CidadeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cidade, parent, false)
        return CidadeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CidadeViewHolder, position: Int) {
        val cidade = listaCidades[position]
        holder.txtNome.text = cidade.cidade
        holder.txtTemperatura.text = cidade.temperatura

        // Configura o botão de apagar com callback
        holder.btnApagar.setOnClickListener {
            onApagarClick(cidade)
        }
    }

    override fun getItemCount(): Int = listaCidades.size
}
