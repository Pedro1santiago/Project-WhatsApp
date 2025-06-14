package com.pedro.whatsapp.activities

import Mensagem
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.pedro.whatsapp.R
import com.pedro.whatsapp.adapters.MensagensAdapter
import com.pedro.whatsapp.databinding.ActivityMensagensBinding
import com.pedro.whatsapp.model.Conversa
import com.pedro.whatsapp.model.Usuario
import com.pedro.whatsapp.utils.Constantes
import com.pedro.whatsapp.utils.exibirMensagem
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val binding by lazy { ActivityMensagensBinding.inflate(layoutInflater) }

    private lateinit var listenerRegistration: ListenerRegistration
    private var dadosDestinatario: Usuario? = null
    private var dadosUsuarioRementente: Usuario? = null
    private lateinit var conversasAdapter: MensagensAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUsuarios()
        inicializarToolbar()
        inicializarEventoClique()
        inicializarRecyclerview()
        inicializarListeners()
    }

    private fun inicializarRecyclerview() {
        layoutManager = LinearLayoutManager(applicationContext).apply {
            stackFromEnd = true
        }

        with(binding) {
            conversasAdapter = MensagensAdapter()
            conversasAdapter.onMensagemSelecionada = { invalidateOptionsMenu() }
            rvMensagens.layoutManager = layoutManager
            rvMensagens.adapter = conversasAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun inicializarListeners() {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id

        if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
            listenerRegistration = firestore
                .collection(Constantes.MENSAGENS)
                .document(idUsuarioRemetente)
                .collection(idUsuarioDestinatario)
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, erro ->
                    if (erro != null) {
                        exibirMensagem("Erro ao recuperar mensagens")
                    }

                    val listaMensagens = mutableListOf<Mensagem>()
                    querySnapshot?.documents?.forEach { doc ->
                        doc.toObject(Mensagem::class.java)?.let { listaMensagens.add(it) }
                    }

                    if (listaMensagens.isNotEmpty()) {
                        conversasAdapter.adicionarLista(listaMensagens)
                        layoutManager.scrollToPositionWithOffset(listaMensagens.size - 1, 0)
                    }
                }
        }
    }

    private fun inicializarEventoClique() {
        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem(mensagem)
        }
    }

    private fun salvarMensagem(textoMensagem: String) {
        if (textoMensagem.isNotEmpty()) {
            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
                val mensagem = Mensagem(idUsuarioRemetente, textoMensagem)

                salvarMensagemFirestore(idUsuarioRemetente, idUsuarioDestinatario, mensagem)
                val conversaRemetente = Conversa(idUsuarioRemetente, idUsuarioDestinatario, dadosDestinatario!!.foto, dadosDestinatario!!.nome, textoMensagem)
                salvarConversaFirestore(conversaRemetente)

                salvarMensagemFirestore(idUsuarioDestinatario, idUsuarioRemetente, mensagem)
                val conversaDestinatario = Conversa(idUsuarioDestinatario, idUsuarioRemetente, dadosUsuarioRementente!!.foto, dadosUsuarioRementente!!.nome, textoMensagem)
                salvarConversaFirestore(conversaDestinatario)

                binding.editMensagem.setText("")
            }
        }
    }

    private fun salvarConversaFirestore(conversa: Conversa) {
        firestore
            .collection(Constantes.CONVERSAS)
            .document(conversa.idUsuarioRementente)
            .collection(Constantes.ULTIMAS_CONVERSAS)
            .document(conversa.idUsuarioDestinatario)
            .set(conversa)
            .addOnFailureListener { exibirMensagem("Erro ao salvar conversa") }
    }

    private fun salvarMensagemFirestore(idUsuarioRemetente: String, idUsuarioDestinatario: String, mensagem: Mensagem) {
        firestore
            .collection(Constantes.MENSAGENS)
            .document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario)
            .add(mensagem)
            .addOnFailureListener { exibirMensagem("Erro ao enviar mensagem") }
    }

    private fun excluirMensagemSelecionada() {
        val mensagem = conversasAdapter.getMensagemSelecionada() ?: return

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Excluir mensagem")
            .setMessage("Tem certeza que deseja excluir esta mensagem?")
            .setPositiveButton("Excluir") { _, _ ->
                val idUsuarioRemetente = firebaseAuth.currentUser?.uid
                val idUsuarioDestinatario = dadosDestinatario?.id

                if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
                    val refRemetente = firestore
                        .collection(Constantes.MENSAGENS)
                        .document(idUsuarioRemetente)
                        .collection(idUsuarioDestinatario)

                    refRemetente.whereEqualTo("mensagem", mensagem.mensagem)
                        .get()
                        .addOnSuccessListener { docs ->
                            for (doc in docs) {
                                doc.reference.delete()
                            }
                            conversasAdapter.limparSelecao()
                            invalidateOptionsMenu()
                        }
                        .addOnFailureListener {
                            exibirMensagem("Erro ao excluir mensagem")
                        }
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }
    private fun inicializarToolbar() {
        val toolbar = binding.tbMensagens
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario != null) {
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get().load(dadosDestinatario!!.foto).into(binding.imageFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
            toolbar.navigationIcon?.setTint(getColor(R.color.white))
        }
    }

    private fun recuperarDadosUsuarios() {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if (idUsuarioRemetente != null) {
            firestore
                .collection(Constantes.USUARIOS)
                .document(idUsuarioRemetente)
                .get()
                .addOnSuccessListener {
                    it.toObject(Usuario::class.java)?.let { user -> dadosUsuarioRementente = user }
                }
        }

        val extras = intent.extras
        if (extras != null) {
            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("dadosDestinatario", Usuario::class.java)
            } else {
                extras.getParcelable("dadosDestinatario")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_mensagens, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val itemExcluir = menu?.findItem(R.id.menuExcluir)
        itemExcluir?.isVisible = conversasAdapter.getMensagemSelecionada() != null
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (conversasAdapter.getMensagemSelecionada() != null) {
                    conversasAdapter.limparSelecao()
                    invalidateOptionsMenu()
                } else {
                    finish()
                }
                true
            }
            R.id.menuExcluir -> {
                excluirMensagemSelecionada()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
