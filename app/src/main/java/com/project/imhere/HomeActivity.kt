package com.project.imhere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

//Clases de Enumeracion. BASIC (La autentificaicon mas basica)
enum class ProviderType{ //Cuando para al layoute fun se autentifia.
    BASIC,
    FACEBOOK
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //Val constantes
        val bundle: Bundle? = intent.extras //Bundle es paquete vacio, al que se le pasa atraves del intent los datos.
        val email: String? = bundle?.getString("email")
        val provider: String? =bundle?.getString("provider")

        setup(email?:"", provider?:"" )
    }

    private fun setup(email: String, provider: String){
      title = "Inicio"
      //Se actualizan los datos en los Textvew.
      text_Email_Home.text= email
      text_Provedor.text= provider

      button_Cerrar_Secion.setOnClickListener{

          if(provider == ProviderType.FACEBOOK.name){
              LoginManager.getInstance().logOut()
          }

          FirebaseAuth.getInstance().signOut()
          onBackPressed() // Regresar a la pantalla anterior
      }
    }


}