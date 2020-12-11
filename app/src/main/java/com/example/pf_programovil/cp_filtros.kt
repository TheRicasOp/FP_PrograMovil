package com.example.pf_programovil

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout

class cp_filtros : ConstraintLayout {


    constructor(ctx: Context) : super(ctx) {
        inicializar()
    }
    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs) {
        inicializar();
    }
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int): super(ctx, attrs, defStyleAttr) {
        inicializar();
    }

    fun inicializar() {
        //utilizamos el layour 'login_control' como interfaz de control
        val li = LayoutInflater.from(context)
        li.inflate(R.layout.cp_filtros_activity, this, true)

        //Obtenemos las referencias a los distintos controles

        //asignarEventos()
    }

    //
    /*interface OnLoginListener {
        fun onLogin(usuario: String, password: String)
    }*/

    /*var listener: OnLoginListener? = null*/

    /*fun setOnLoginListener(login:(String, String) -> Unit) {
        listener = object: OnLoginListener {
            override fun onLogin(usuario: String, password: String) {
                login(usuario, password)
            }
        }
    }*/

    /*fun asignarEventos() {
        btnLogin.setOnClickListener {
            listener?.onLogin(txtUsuario.text.toString(), txtPassword.text.toString())
        }
    }*/

}