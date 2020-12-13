package com.example.pf_programovil

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class filtros: AppCompatActivity() {

    private lateinit var imgFoto : ImageView

    private lateinit var btnSave : ImageButton
    private lateinit var btnApply : Button
    private lateinit var slider : SeekBar
    private lateinit var fotillo : ImageView
    private lateinit var filtro: TextView
    private lateinit var seekText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cp_filtros_activity)

        btnSave = findViewById(R.id.save)
        btnApply = findViewById(R.id.aplicar)
        fotillo = findViewById(R.id.fotillo)
        filtro = findViewById(R.id.tv_filtro)

        seekText = findViewById(R.id.textView2)

        slider = findViewById(R.id.seekBar) as SeekBar
        slider.max = 10
        slider.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekText.text = "Seeking to:" + progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekText.text = "Started at:" + seekBar.progress
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekText.text = "Selected:" + seekBar.progress
            }
        })

        val intent = getIntent()
        var foto = Uri.parse(intent.extras?.getString("Foto"))
        imgFoto = findViewById(R.id.fotillo)
        imgFoto.setImageURI(foto)

        //spinners
        val datos1 = arrayOf("Basicos", "Inversion o Negativo", "Escala de Grises", "Brillo", "Contraste", "Gamma", "Separacion de canal rojo", "Separacion de canal verde", "Separacion de canal azul")
        val adaptador1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos1)
        val cmbOpciones1 : Spinner = findViewById(R.id.cmbOpciones1)
        adaptador1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones1.adapter = adaptador1

        val datos2 = arrayOf("Convolucion", "Smoothing", "Gaussian Blur", "Sharpen", "Mean Removal", "Embossing", "Edge Detection")
        val adaptador2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos2)
        val cmbOpciones2 : Spinner = findViewById(R.id.cmbOpciones2)
        adaptador2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones2.adapter = adaptador2

        val datos3 = arrayOf("Otros", "F1", "F2", "F3", "F4", "F5")
        val adaptador3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos3)
        val cmbOpciones3 : Spinner = findViewById(R.id.cmbOpciones3)
        adaptador3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones3.adapter = adaptador3

        val datos4 = arrayOf("Zoom", "25%", "50%", "100%", "150%", "200%", "300%", "500%")
        val adaptador4= ArrayAdapter(this, android.R.layout.simple_spinner_item, datos4)
        val cmbOpciones4 : Spinner = findViewById(R.id.cmbOpciones4)
        adaptador4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones4.adapter = adaptador4

        cmbOpciones1.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(
                        applicationContext,
                        "Sin seleccion",
                        Toast.LENGTH_SHORT
                ).show()
            }
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val pos = parent.getItemAtPosition(position)
                when(position) {
                    0 -> {
                    }
                    1 -> {
                        filtro.text = "$pos"
                    }
                    2 -> {
                        filtro.text = "$pos"
                    }
                    3 -> {
                        filtro.text = "$pos"
                    }
                    4 -> {
                        filtro.text = "$pos"
                    }
                    5 -> {
                        filtro.text = "$pos"
                    }
                    6 -> {
                        filtro.text = "$pos"
                    }
                    7 -> {
                        filtro.text = "$pos"
                    }
                    8 -> {
                        filtro.text = "$pos"
                    }
                }
                when(position) {
                    1, 2, 3, 4, 5, 6, 7, 8 ->
                        Toast.makeText(
                                applicationContext,
                                "Basico: $pos",
                                Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        cmbOpciones2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(
                        applicationContext,
                        "Sin seleccion",
                        Toast.LENGTH_SHORT
                ).show()
            }
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val pos = parent.getItemAtPosition(position)
                when(position) {
                    0 -> {
                    }
                    1 -> {
                        filtro.text = "$pos"
                    }
                    2 -> {
                        filtro.text = "$pos"
                    }
                    3 -> {
                        filtro.text = "$pos"
                    }
                    4 -> {
                        filtro.text = "$pos"
                    }
                    5 -> {
                        filtro.text = "$pos"
                    }
                    6 -> {
                        filtro.text = "$pos"
                    }
                }
                when(position) {
                    1, 2, 3, 4, 5, 6 ->
                        Toast.makeText(
                                applicationContext,
                                "Convolucion: $pos",
                                Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        cmbOpciones3.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(
                        applicationContext,
                        "Sin seleccion",
                        Toast.LENGTH_SHORT
                ).show()
            }
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val pos = parent.getItemAtPosition(position)
                when(position) {
                    0 -> {
                    }
                    1 -> {
                        filtro.text = "$pos"
                    }
                    2 -> {
                        filtro.text = "$pos"
                    }
                    3 -> {
                        filtro.text = "$pos"
                    }
                    4 -> {
                        filtro.text = "$pos"
                    }
                    5 -> {
                        filtro.text = "$pos"
                    }
                }
                when(position) {
                    1, 2, 3, 4, 5 ->
                        Toast.makeText(
                                applicationContext,
                                "Otros: $pos",
                                Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        cmbOpciones4.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(
                        applicationContext,
                        "Sin seleccion",
                        Toast.LENGTH_SHORT
                ).show()
            }
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val pos = parent.getItemAtPosition(position)
                when(position) {
                    0 -> {
                    }
                    1 -> {
                    }
                    2 -> {
                    }
                    3 -> {
                    }
                    4 -> {
                    }
                    5 -> {
                    }
                    6 -> {
                    }
                    7 -> {
                    }
                }
                when(position) {
                    1, 2, 3, 4, 5, 6, 7 ->
                        Toast.makeText(
                                applicationContext,
                                "Zoom: $pos",
                                Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        btnSave.setOnClickListener{
            val bitmap = fotillo.getDrawable().toBitmap()
            saveMediaToStorage(bitmap)
        }

    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(
                    applicationContext,
                    "Saved to photos",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }


}