package com.pedro.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pedro.whatsapp.R
import com.pedro.whatsapp.databinding.ItemContatosBinding
import com.pedro.whatsapp.model.Usuario
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback

class ContatosAdapter(
    private val onClick: (Usuario) -> Unit
) : Adapter<ContatosAdapter.ContatosViewHolder>() {

    private var listaContatos = emptyList<Usuario>()
    fun adicionarLista(lista: List<Usuario>) {
        listaContatos = lista
        notifyDataSetChanged()
    }

    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : ViewHolder(binding.root) {

        fun bind(usuario: Usuario){

            binding.textContatoNome.text = usuario.nome
            if (!usuario.foto.isNullOrEmpty()) {
                Picasso.get()
                    .load(usuario.foto)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(binding.imageContatoFoto, object : Callback {
                        override fun onSuccess() {
                            // carregou do cache
                        }

                        override fun onError(e: Exception?) {
                            // tentou cache e falhou, então baixa da internet
                            Picasso.get()
                                .load(usuario.foto)
                                .into(binding.imageContatoFoto)
                        }
                    })
            } else {
                binding.imageContatoFoto.setImageResource(R.drawable.perfil)
            }

            binding.clItemContato.setOnClickListener {
                onClick(usuario)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContatosBinding.inflate(inflater, parent, false)
        return ContatosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val usuario = listaContatos[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }
}
