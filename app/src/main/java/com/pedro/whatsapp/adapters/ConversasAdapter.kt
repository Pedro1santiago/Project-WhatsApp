package com.pedro.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.pedro.whatsapp.R
import com.pedro.whatsapp.databinding.ItemConversasBinding
import com.pedro.whatsapp.model.Conversa
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback

class ConversasAdapter(
    private val onClick: (Conversa) -> Unit
) : Adapter<ConversasAdapter.ConversasViewHolder>() {

    private var listaConversas = emptyList<Conversa>()
    fun adicionarLista( lista: List<Conversa> ){
        listaConversas = lista
        notifyDataSetChanged()
    }

    inner class ConversasViewHolder(
        private val binding: ItemConversasBinding
    ) : RecyclerView.ViewHolder( binding.root ){

        fun bind(conversa: Conversa) {
            binding.textConversaNome.text = conversa.nome
            binding.textConversaMensagem.text = conversa.ultimaMensagem

            Picasso.get()
                .load(conversa.foto)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(binding.imageConversaFoto, object : Callback {
                    override fun onSuccess() {
                        // Imagem carregada do cache
                    }

                    override fun onError(e: Exception?) {
                        // Se falhar, busca online
                        Picasso.get()
                            .load(conversa.foto)
                            .into(binding.imageConversaFoto)
                    }
                })

            binding.clItemConversa.setOnClickListener {
                onClick(conversa)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {

        val inflater = LayoutInflater.from( parent.context )
        val itemView = ItemConversasBinding.inflate(
            inflater, parent, false
        )
        return ConversasViewHolder( itemView )

    }

    override fun onBindViewHolder(holder: ConversasViewHolder, position: Int) {
        val conversa = listaConversas[position]
        holder.bind( conversa )
    }

    override fun getItemCount(): Int {
        return listaConversas.size
    }

}