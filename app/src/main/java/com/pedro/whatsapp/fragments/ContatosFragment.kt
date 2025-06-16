package com.pedro.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.pedro.whatsapp.activities.MensagensActivity
import com.pedro.whatsapp.adapters.ContatosAdapter
import com.pedro.whatsapp.databinding.FragmentContatosBinding

import com.pedro.whatsapp.model.Usuario
import com.pedro.whatsapp.utils.Constantes

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContatosBinding.inflate(
            inflater, container, false
        )

        contatosAdapter = ContatosAdapter { usuario ->
            val intent = Intent(context, MensagensActivity::class.java)
            intent.putExtra("dadosDestinatario", usuario)
            //intent.putExtra("origem", Constantes.ORIGEM_CONTATO)
            startActivity( intent )
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)


        return binding.root

    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {

        eventoSnapshot = firestore
            .collection( Constantes.USUARIOS )
            .addSnapshotListener { querySnapshot, erro ->

                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents

                documentos?.forEach { documentSnapshot ->

                    val idUsuarioLogado = firebaseAuth.currentUser?.uid
                    val usuario = documentSnapshot.toObject( Usuario::class.java )

                    if( usuario != null && idUsuarioLogado != null ){
                        if( idUsuarioLogado != usuario.id ){
                            listaContatos.add( usuario )
                        }
                    }

                }

                //Lista de contatos (atualizar o RecyclerView)
                if ( listaContatos.isNotEmpty() ){
                    contatosAdapter.adicionarLista( listaContatos )
                }

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

}