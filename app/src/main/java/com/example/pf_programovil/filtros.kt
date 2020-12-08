package com.example.pf_programovil

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class filtros: AppCompatActivity() {

    private lateinit var imgFoto : ImageView
    private lateinit var dis: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filtros_activity)

        dis = this

        val intent = getIntent()
        var foto = Uri.parse(intent.extras?.getString("Foto"))

        imgFoto = findViewById(R.id.fotillo)
        imgFoto.setImageURI(foto)

        val datos1 = arrayOf("Tipo de filtro", "Basicos", "De Convolucion", "Otros")
        val adaptador1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos1)
        val cmbOpciones1 : Spinner = findViewById(R.id.cmbOpciones1)
        adaptador1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones1.adapter = adaptador1

        val datos2 = arrayOf("")
        val adaptador2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos2)
        val cmbOpciones2 : Spinner = findViewById(R.id.cmbOpciones2)
        adaptador2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones2.adapter = adaptador2

        val datos3 = arrayOf("Filtro", "Inversión o negativo", "Escala de grises", "Brillo", "Contraste", "Gamma", "Separación de canales (rojo, verde, azul)")
        val adaptador3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos3)
        val cmbOpciones3 : Spinner = findViewById(R.id.cmbOpciones3)
        adaptador3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones3.adapter = adaptador3

        val datos4 = arrayOf("Filtro", "Smoothing", "Gaussian Blur", "Sharpen", "Mean Removal", "Embossing", "Edge Detection")
        val adaptador4 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos4)
        val cmbOpciones4 : Spinner = findViewById(R.id.cmbOpciones4)
        adaptador4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones4.adapter = adaptador4

        val datos5 = arrayOf("Filtro", "F1", "F2", "F3", "F4", "F5", "F6")
        val adaptador5 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos5)
        val cmbOpciones5 : Spinner = findViewById(R.id.cmbOpciones5)
        adaptador5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones5.adapter = adaptador5

        var spinner1 = findViewById<LinearLayout>(R.id.ll1).layoutParams as LinearLayout.LayoutParams
        var spinner2 = findViewById<LinearLayout>(R.id.ll2).layoutParams as LinearLayout.LayoutParams
        var spinner3 = findViewById<LinearLayout>(R.id.ll3).layoutParams as LinearLayout.LayoutParams
        var spinner4 = findViewById<LinearLayout>(R.id.ll4).layoutParams as LinearLayout.LayoutParams

        var width = spinner1.width

        /*val datos2 = arrayOf("")
        val datos3 = arrayOf("Filtro", "Inversión o negativo", "Escala de grises", "Brillo", "Contraste", "Gamma", "Separación de canales (rojo, verde, azul)")
        val datos4 = arrayOf("Filtro", "Smoothing", "Gaussian Blur", "Sharpen", "Mean Removal", "Embossing", "Edge Detection")
        val datos5 = arrayOf("Filtro", "F1", "F2", "F3", "F4", "F5", "F6")
        var adaptador2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos2)
        val cmbOpciones2 : Spinner = findViewById(R.id.cmbOpciones2)
        adaptador2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones2.adapter = adaptador2*/

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
                when(position)
                {
                    0 -> {
                        //datos2 = arrayOf("Filtro", "", "", "", "", "", "")
                        //adaptador2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos2)
                        spinner1.width = width
                        spinner2.width = 1
                        spinner3.width = 1
                        spinner4.width = 1
                    }
                    1 -> {
                        spinner1.width = 1
                        spinner2.width = width
                        spinner3.width = 1
                        spinner4.width = 1
                        /*adaptador2.add("Filtro")
                        adaptador2.add("Inversión o negativo")
                        adaptador2.add("Escala de grises")
                        adaptador2.add("Brillo")
                        adaptador2.add("Contraste")
                        adaptador2.add("Gamma")
                        adaptador2.add("Separación de canales (rojo, verde, azul)")*/
                        //arrayOf("Filtro", "Inversión o negativo", "Escala de grises", "Brillo", "Contraste", "Gamma", "Separación de canales (rojo, verde, azul)")
                    }
                    2 -> {
                        spinner1.width = 1
                        spinner2.width = 1
                        spinner3.width = width
                        spinner4.width = 1
                        /*adaptador2.add("Filtro")
                        adaptador2.add("Smoothing")
                        adaptador2.add("Gaussian Blur")
                        adaptador2.add("Sharpen")
                        adaptador2.add("Mean Removal")
                        adaptador2.add("Embossing")
                        adaptador2.add("Edge Detection")*/
                        //arrayOf("Filtro", "Smoothing", "Gaussian Blur", "Sharpen", "Mean Removal", "Embossing", "Edge Detection")
                    }
                    3 -> {
                        spinner1.width = 1
                        spinner2.width = 1
                        spinner3.width = 1
                        spinner4.width = width
                        /*adaptador2.add("Filtro")
                        adaptador2.add("F1")
                        adaptador2.add("F2")
                        adaptador2.add("F3")
                        adaptador2.add("F4")
                        adaptador2.add("F5")*/
                        //adaptador2 = arrayOf("Filtro", "F1", "F2", "F3", "F4", "F5", "F6")
                    }
                    else -> null
                }
                Toast.makeText(
                        applicationContext,
                        "Seleccionado: $pos",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}