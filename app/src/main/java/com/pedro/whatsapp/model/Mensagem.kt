import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Mensagem(
    val idUsuario: String = "",
    val mensagem: String = "",
    val urlImagem: String? = null,
    @ServerTimestamp
    val data: Date? = null
)