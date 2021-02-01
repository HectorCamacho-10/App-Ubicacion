package com.project.imhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.FontRequest
import androidx.appcompat.app.AlertDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private  val callbackManager = CallbackManager.Factory.create() //integracion de Facebook dentro del activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) //Se nombra el Layout al que esta conectado.

        //Todas las variables y funciones iran aqui dentro de la Actividad principal que es el LoginActivity


        //Setup
        setup()
        showAlert()
        showHome(email = String(), provider = ProviderType.BASIC)
    }

    private fun setup(){

        title = "Authentication" //Nombre de la pantalla

        //Recoger el evento de el boton.
        button_Registrar.setOnClickListener{
            if(edit_text_Email.text.isNotEmpty() && edit_text_Password.text.isNotEmpty()){ //Nos aseguramos que no este vacia la casilla.
                //Se llama a Firebase para enviar dos String. (tostring para convertirlos) //Comprobar si la operacion se a acompletado correctamente (.addcompletelistener)
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(edit_text_Email.text.toString(),
                    edit_text_Password.text.toString()).addOnCompleteListener(){
                   if(it.isSuccessful){ //Se crea usuario, El tipo de email es el provedor que tendra la autentificacion.
                  //Enviar a Home
                 showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                   }else{
                 //Mostar un alert error de autentificacion.
                 showAlert()
                   }
                }
            }
        }

        button_Singin.setOnClickListener{
            if(edit_text_Email.text.isNotEmpty() && edit_text_Password.text.isNotEmpty()){
                FirebaseAuth.getInstance().
                signInWithEmailAndPassword(edit_text_Email.text.toString(),
                edit_text_Password.text.toString()).addOnCompleteListener(){
                    if(it.isSuccessful){
                        //Enviar a Home
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    }else{
                        //Mostar un alert error de autentificacion.
                        showAlert()
                    }
                }
            }
        }
        //Error puede ser el hash de autentificaion(https://www.flipandroid.com/error-de-inicio-de-sesin-hay-un-error-al-iniciar-sesin-en-esta-aplicacin-por-favor-intntelo-de-nuevo-ms-tarde.html)
        button_facebook.setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>{

                override fun onSuccess(result: LoginResult?) {//si el login fue correcto //FACEBOOK
                    result?.let{
                        val token = it.accessToken

                        val credential =  FacebookAuthProvider.getCredential(token.token)

                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                            if (it.isSuccessful){
                                showHome(it.result?.user?.email?: "", ProviderType.FACEBOOK)
                            }else{
                                showAlert()
                            }
                        }

                    }

                }

                override fun onCancel(){

                }

                override fun onError(error: FacebookException?){
                    showAlert()
                }
            })
        }

    }




    private fun showAlert(){ //Funcion para mostrar alerta de dialogo en la pantalla. Error de autentificaion.

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun showHome(email: String, provider: ProviderType){ //Funcion para enviar a home, pasar los datos y mostrarlos.

        val homeIntent:Intent = Intent(this,HomeActivity::class.java).apply{
            putExtra("email",email)
            putExtra("provider",provider.name)

            }
         startActivity(homeIntent)

    }

    override fun  onActivityResult(requestCode:Int, resultCode:Int, data: Intent?)
    {
        callbackManager.onActivityResult(requestCode,resultCode,data)//Integracion de fecabook, desencadena una llamada a las operaciones de facebbok.

        super.onActivityResult(requestCode,resultCode, data)
    }


}

