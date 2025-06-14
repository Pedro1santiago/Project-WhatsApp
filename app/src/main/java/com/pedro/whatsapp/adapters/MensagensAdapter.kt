package com.pedro.whatsapp.adapters

import Mensagem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.pedro.whatsapp.databinding.ItemMensagensDestinatarioBinding
import com.pedro.whatsapp.databinding.ItemMensagensRemetenteBinding
import com.pedro.whatsapp.utils.Constantes
import java.text.SimpleDateFormat
import java.util.*

class MensagensAdapter(
    val onScrollParaUltimaMensagem: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listaMensagens = emptyList<Mensagem>()
    private var mensagemSelecionada: Mensagem? = null

    var onMensagemSelecionada: ((Mensagem?) -> Unit)? = null

    fun adicionarLista(lista: List<Mensagem>) {
        listaMensagens = lista
        notifyDataSetChanged()
        onScrollParaUltimaMensagem?.invoke(listaMensagens.size - 1)
    }

    fun getMensagemSelecionada(): Mensagem? = mensagemSelecionada

    fun limparSelecao() {
        mensagemSelecionada = null
        notifyDataSetChanged()
        onMensagemSelecionada?.invoke(null)
    }

    class MensagensRemetenteViewHolder(
        private val binding: ItemMensagensRemetenteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mensagem: Mensagem, selecionada: Boolean) {
            binding.textMensagemRemetente.text = mensagem.mensagem
            binding.root.setBackgroundResource(
                if (selecionada) android.R.color.darker_gray else android.R.color.transparent
            )
        }

        companion object {
            fun inflarLayout(parent: ViewGroup): MensagensRemetenteViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensRemetenteBinding.inflate(inflater, parent, false)
                return MensagensRemetenteViewHolder(itemView)
            }
        }
    }

    class MensagensDestinatarioViewHolder(
        private val binding: ItemMensagensDestinatarioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mensagem: Mensagem, selecionada: Boolean) {
            binding.textMensagemDestinatario.text = mensagem.mensagem

            val formato = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
            formato.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
            val hora = formato.format(mensagem.data ?: Date())

            binding.textHoraMensagem.text = hora
            binding.root.setBackgroundResource(
                if (selecionada) android.R.color.darker_gray else android.R.color.transparent
            )
        }

        companion object {
            fun inflarLayout(parent: ViewGroup): MensagensDestinatarioViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensDestinatarioBinding.inflate(inflater, parent, false)
                return MensagensDestinatarioViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val mensagem = listaMensagens[position]
        val idUsuarioLogado = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return if (idUsuarioLogado == mensagem.idUsuario) {
            Constantes.TIPO_REMETENTE
        } else {
            Constantes.TIPO_DESTINATARIO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constantes.TIPO_REMETENTE)
            MensagensRemetenteViewHolder.inflarLayout(parent)
        else
            MensagensDestinatarioViewHolder.inflarLayout(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]
        val selecionada = mensagem == mensagemSelecionada

        holder.itemView.setOnLongClickListener {
            mensagemSelecionada = if (selecionada) null else mensagem
            onMensagemSelecionada?.invoke(mensagemSelecionada)
            notifyDataSetChanged()
            true
        }

        when (holder) {
            is MensagensRemetenteViewHolder -> holder.bind(mensagem, selecionada)
            is MensagensDestinatarioViewHolder -> holder.bind(mensagem, selecionada)
        }
    }

    override fun getItemCount(): Int = listaMensagens.size
}
