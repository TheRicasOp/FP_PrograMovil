package com.example.pf_programovil

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class obtieneimagen : AppCompatActivity() {

    private lateinit var btnGaleria : Button
    private lateinit var btnCamara : Button
    private lateinit var imgFoto : ImageView
    private val REQUEST_GALLERY = 1001
    private val REQUEST_CAMERA = 1002
    var foto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.obtieneimagen_activity)
        abreGaleria_Click()
        abreCamara_Click()

        val discard = findViewById(R.id.descartar) as Button
        val continu = findViewById(R.id.continuar) as Button
        discard.setOnClickListener{
            imgFoto = findViewById(R.id.imgFoto)
            imgFoto.setImageResource(R.drawable.nodisp)
        }
        continu.setOnClickListener {
            val intent = Intent(this, filtros::class.java)
            startActivity(intent)
        }
    }

    //Detectamos cuando se pulse el boton para abrir la camara
    private fun abreCamara_Click() {
        btnCamara = findViewById(R.id.btnCamara)
        btnCamara.setOnClickListener() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                        || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //Pedirle permiso al usuario
                    val permisosCamara = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permisosCamara, REQUEST_CAMERA)
                } else {
                    abreCamara()
                }
            } else {
                abreCamara()
            }
        }
    }

    //Detectamos cuando se pulse el boton para abrir la galeria
    private fun abreGaleria_Click() {
        btnGaleria = findViewById(R.id.btnGaleria)
        btnGaleria.setOnClickListener() {
            //Verificamos que version de android esta instalada en el telefono
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //Pedir permiso al usuario
                    val permisoArchivos = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permisoArchivos, REQUEST_GALLERY)
                } else {
                    //Entonces si tiene permisos
                    muestraGaleria()
                }
            } else {
                //Tiene version de Lollipop hacia abajo y por default tiene permiso
                muestraGaleria()
            }
        }
    }

    //Checamos si el usuario dio permiso a la app
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_GALLERY -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    muestraGaleria()
                } else {
                    Toast.makeText(applicationContext, "No puedes acceder a tus imagenes", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CAMERA -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abreCamara()
                } else {
                    Toast.makeText(applicationContext, "No puedes acceder a la camara", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Abre la ventana donde se muestra la galeria de fotos
    private fun muestraGaleria() {
        val intentGaleria = Intent(Intent.ACTION_PICK)
        intentGaleria.type = "image/*"
        startActivityForResult(intentGaleria, REQUEST_GALLERY)
    }

    //Abre la camara del telefono
    private fun abreCamara() {
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE, "Nueva Imagen")
        foto = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
        val camaraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, foto)
        startActivityForResult(camaraIntent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imgFoto = findViewById(R.id.imgFoto)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY) {
            //discardcontinue()
            imgFoto.setImageURI(data?.data)
        }
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            //discardcontinue()
            imgFoto.setImageURI(foto)
        }
    }

    /*private fun discardcontinue() {
        val discard = findViewById(R.id.descartar) as Button
        val continu = findViewById(R.id.continuar) as Button
        discard.setOnClickListener{
            imgFoto.setImageResource(R.drawable.nodisp)
        }
        continu.setOnClickListener {
            val intent = Intent(this, filtros::class.java)
            startActivity(intent)
        }
    }*/
}