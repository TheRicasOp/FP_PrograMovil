package com.example.pf_programovil

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.argb
import android.graphics.Color.rgb
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@Suppress("DEPRECATION")
class filtros: AppCompatActivity() {

    private lateinit var imgFoto : ImageView
    private lateinit var btnSave : ImageButton
    private lateinit var btnApply : Button
    private lateinit var seekBar : SeekBar
    private lateinit var filtro: TextView
    private lateinit var bitmap: Bitmap
    private lateinit var image: BitmapDrawable
    private lateinit var seekText: TextView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cp_filtros_activity)

        btnSave = findViewById(R.id.save)
        btnApply = findViewById(R.id.aplicar)
        seekBar = findViewById(R.id.seekBar)
        seekText = findViewById(R.id.textView2)

        filtro = findViewById(R.id.tv_filtro)

        val intent = getIntent()
        var foto = Uri.parse(intent.extras?.getString("Foto"))
        imgFoto = findViewById(R.id.fotillo)

        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, foto)
        var orientation = 0

        try {
            this.getContentResolver().openInputStream(foto).use { inputStream ->
                val exif = inputStream?.let { ExifInterface(it) }
                orientation =
                        exif!!.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                        )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotate(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotate(bitmap, 180f)
            ExifInterface.ORIENTATION_TRANSVERSE -> bitmap = rotate(bitmap, 270f)
            ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotate(bitmap, 270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> bitmap = flip(bitmap, true, vertical = false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> bitmap = flip(bitmap, false, vertical = true)
            else -> bitmap = bitmap
        }

        var xx = 1000.0/bitmap.width.toDouble()
        var yy = 1200.0/bitmap.height.toDouble()

        if(xx < 1 || yy < 1){
            if(xx >= yy){
                bitmap = zoom(bitmap,(bitmap.width*yy).toInt(),(bitmap.height*yy).toInt())
            }
            else{
                bitmap = zoom(bitmap,(bitmap.width*xx).toInt(),(bitmap.height*xx).toInt())
            }
        }

        var bitmapaux = Bitmap.createBitmap(bitmap)
        image = BitmapDrawable(this.resources, bitmap)
        imgFoto.setImageDrawable(image)

        var fop = -1
        seekBar.max = 0
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekText.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekText.text = seekBar.progress.toString()
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekText.text = seekBar.progress.toString()
                when(fop){
                    0 -> bitmap = brightness(bitmapaux,seekBar.progress)
                    1 -> bitmap = contrast(bitmapaux,seekBar.progress-100)
                    2 -> bitmap = gamma(bitmapaux,(seekBar.progress.toFloat()/20.0).toFloat())
                    3 -> if(seekBar.progress != 0)
                        bitmap = randomJitter(bitmapaux,seekBar.progress.toShort())
                    4 -> bitmap = timeWarp(bitmapaux,seekBar.progress)
                    5 -> bitmap = moire(bitmapaux,seekBar.progress/10.0)
                    6 -> bitmap = water(bitmapaux,(seekBar.progress-30).toShort())
                    7 -> if(seekBar.progress != 0)
                        bitmap = pixelate(bitmapaux,seekBar.progress,false)
                    8 -> if(seekBar.progress != 0)
                        bitmap = pixelate(bitmapaux,seekBar.progress,true)
                }
                update()
            }
        })

        val datos1 = arrayOf("Basicos","Inversion o Negativo","Escala de Grises","Brillo","Contraste",
                "Gamma","Separacion de canal rojo","Separacion de canal verde","Separacion de canal azul")

        val adaptador1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos1)
        val cmbOpciones1 : Spinner = findViewById(R.id.cmbOpciones1)
        adaptador1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones1.adapter = adaptador1

        val datos2 = arrayOf("Convolucion","Smoothing","Gaussian Blur","Sharpen","Mean Removal",
                "Embossing","Edge Detection")
        val adaptador2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos2)
        val cmbOpciones2 : Spinner = findViewById(R.id.cmbOpciones2)
        adaptador2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones2.adapter = adaptador2

        val datos3 = arrayOf("Otros","Random Jitter","Swirl","Time Warp","Moire","Water","Pixelate","Pixelate Grid")
        val adaptador3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos3)
        val cmbOpciones3 : Spinner = findViewById(R.id.cmbOpciones3)
        adaptador3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbOpciones3.adapter = adaptador3

        val datos4 = arrayOf("Zoom","25%","50%","100%","150%","200%","300%")
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
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val pos = parent.getItemAtPosition(position)
                when(position){
                    0 -> {
                        bitmap = Bitmap.createBitmap(bitmapaux)
                        filtro.text = "filtro"
                        fop = -1
                        seekBar.max = 0
                    }
                    1 -> {
                        bitmap = invert(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    2 -> {
                        bitmap = grayScale(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    3 -> {
                        bitmap = brightness(bitmapaux,50)
                        filtro.text = "$pos"
                        fop = 0
                        seekBar.max = 0
                        seekBar.max = 255
                        seekBar.progress = 50
                    }
                    4 -> {
                        bitmap = contrast(bitmapaux,90)
                        filtro.text = "$pos"
                        fop = 1
                        seekBar.max = 0
                        seekBar.max = 200
                        seekBar.progress = 90
                    }
                    5 -> {
                        bitmap = gamma(bitmapaux,3f)
                        filtro.text = "$pos"
                        fop = 2
                        seekBar.max = 0
                        seekBar.max = 100
                        seekBar.progress = 60
                    }
                    6 -> {
                        bitmap = colorFilter(bitmapaux,0)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    7 -> {
                        bitmap = colorFilter(bitmapaux,1)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    8 -> {
                        bitmap = colorFilter(bitmapaux,2)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                }
                when(position){
                    1, 2, 3, 4, 5, 6, 7, 8 ->
                        Toast.makeText(
                                applicationContext,
                                "Basico: $pos",
                                Toast.LENGTH_SHORT
                        ).show()
                }
                update()
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
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
            ) {
                val pos = parent.getItemAtPosition(position)
                when(position) {
                    0 -> {
                        bitmap = Bitmap.createBitmap(bitmapaux)
                        filtro.text = "filtro"
                        fop = -1
                        seekBar.max = 0
                    }
                    1 -> {
                        bitmap = smooth(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    2 -> {
                        bitmap = gaussianBlur(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    3 -> {
                        bitmap = sharpen(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    4 -> {
                        bitmap = meanRemoval(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    5 -> {
                        bitmap = embossing(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    6 -> {
                        bitmap = edgeDetection(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                }
                when(position){
                    1, 2, 3, 4, 5, 6 -> {
                        Toast.makeText(
                                applicationContext,
                                "Convolucion: $pos",
                                Toast.LENGTH_SHORT
                        ).show()
                        fop = -1
                        seekBar.max = 0
                    }
                }
                update()
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
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
            ) {
                val pos = parent.getItemAtPosition(position)
                when(position) {
                    0 -> {
                        bitmap = Bitmap.createBitmap(bitmapaux)
                        filtro.text = "filtro"
                        fop = -1
                        seekBar.max = 0
                    }
                    1 -> {
                        bitmap = randomJitter(bitmapaux,20)
                        filtro.text = "$pos"
                        fop = 3
                        seekBar.max = 0
                        seekBar.max = 255
                        seekBar.progress = 20
                    }
                    2 -> {
                        bitmap = swirl(bitmapaux)
                        filtro.text = "$pos"
                        fop = -1
                        seekBar.max = 0
                    }
                    3 -> {
                        bitmap = timeWarp(bitmapaux,50)
                        filtro.text = "$pos"
                        fop = 4
                        seekBar.max = 0
                        seekBar.max = 100
                        seekBar.progress = 50
                    }
                    4 -> {
                        bitmap = moire(bitmapaux,2.0)
                        filtro.text = "$pos"
                        fop = 5
                        seekBar.max = 0
                        seekBar.max = 30
                        seekBar.progress = 20
                    }
                    5 -> {
                        bitmap = water(bitmapaux,10)
                        filtro.text = "$pos"
                        fop = 6
                        seekBar.max = 0
                        seekBar.max = 60
                        seekBar.progress = 40
                    }
                    6 -> {
                        bitmap = pixelate(bitmapaux,10,false)
                        filtro.text = "$pos"
                        fop = 7
                        seekBar.max = 0
                        seekBar.max = 50
                        seekBar.progress = 10
                    }
                    7 -> {
                        bitmap = pixelate(bitmapaux,10,true)
                        filtro.text = "$pos"
                        fop = 8
                        seekBar.max = 50
                        seekBar.progress = 10
                    }
                }
                when(position) {
                    1,2,3,4,5,6,7 ->
                        Toast.makeText(
                                applicationContext,
                                "Otros: $pos",
                                Toast.LENGTH_SHORT
                        ).show()
                }
                update()
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
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
            ) {
                val pos = parent.getItemAtPosition(position)
                when(position) {
                    0 -> {
                        bitmap = Bitmap.createBitmap(bitmapaux)
                        filtro.text = "filtro"
                    }
                    1 -> {
                        bitmap = zoom(bitmapaux,(bitmapaux.width/4).toInt(),(bitmapaux.height/4).toInt())

                        filtro.text = "$pos"
                    }
                    2 -> {
                        bitmap = zoom(bitmapaux,(bitmapaux.width/2).toInt(),(bitmapaux.height/2).toInt())
                        filtro.text = "$pos"
                    }
                    3 -> {
                        bitmap = zoom(bitmapaux,bitmapaux.width,bitmapaux.height)
                        filtro.text = "$pos"
                    }
                    4 -> {
                        bitmap = zoom(bitmapaux,(bitmapaux.width*1.5).toInt(),(bitmapaux.height*1.5).toInt())
                        filtro.text = "$pos"
                    }
                    5 -> {
                        bitmap = zoom(bitmapaux,bitmapaux.width*2,bitmapaux.height*2)
                        filtro.text = "$pos"
                    }
                    6 -> {
                        bitmap = zoom(bitmapaux,bitmapaux.width*3,bitmapaux.height*3)
                        filtro.text = "$pos"
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
                update()
            }
        }

        btnApply.setOnClickListener{
            filtro.text = "Filtro"
            Toast.makeText(
                    applicationContext,
                    "Filtro Guardado",
                    Toast.LENGTH_SHORT
            ).show()

            bitmapaux = Bitmap.createBitmap(bitmap)
        }

        btnSave.setOnClickListener{
            //val bitmap = fotillo.getDrawable().toBitmap()
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

    fun invert(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var pixels = IntArray(height * width)
        var color: Int

        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                color = bitmap.getPixel(x, y)
                pixels[width * y + x] = argb(Color.alpha(color),255 - Color.red(color),
                        255 - Color.green(color), 255 - Color.blue(color))
            }
        }
        return Bitmap.createBitmap(pixels, 0, width, width, height, bitmap.config)
    }

    fun grayScale(bitmap: Bitmap): Bitmap{
        val width = bitmap.width
        val height = bitmap.height
        var pixels = IntArray(height * width)
        var color: Int
        var gray: Int

        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                color = bitmap.getPixel(x, y)
                gray = (Color.red(color) * .299f + Color.green(color) * .587f + Color.blue(color) * .114f).toInt()
                pixels[width * y + x] = argb(Color.alpha(color), gray, gray, gray)
            }
        }
        return Bitmap.createBitmap(pixels, 0, width, width, height, bitmap.config)
    }

    fun colorFilter(bitmap: Bitmap, colorF: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var pixels = IntArray(height * width)
        var color: Int

        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                color = bitmap.getPixel(x, y)
                when (colorF) {
                    0 -> pixels[width * y + x] = argb(Color.alpha(color), Color.red(color), 0, 0)
                    1 -> pixels[width * y + x] = argb(Color.alpha(color),0, Color.green(color), 0)
                    2 -> pixels[width * y + x] = argb(Color.alpha(color),0, 0, Color.blue(color))
                }
            }
        }
        return Bitmap.createBitmap(pixels, 0, width, width, height, bitmap.config)
    }

    fun brightness(bitmap: Bitmap, nBrightness: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var pixels = IntArray(height * width)
        var color: Int
        var red: Int
        var blue: Int
        var green: Int

        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                color = bitmap.getPixel(x, y)
                red = Color.red(color) + nBrightness
                blue = Color.blue(color) + nBrightness
                green = Color.green(color) + nBrightness

                if (red < 0) red = 0;
                if (red > 255) red = 255;
                if (blue < 0) blue = 0;
                if (blue > 255) blue = 255;
                if (green < 0) green = 0;
                if (green > 255) green = 255;

                pixels[width * y + x] = argb(Color.alpha(color), red, green, blue)
            }
        }
        return Bitmap.createBitmap(pixels, 0, width, width, height, bitmap.config)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun contrast(bitmap: Bitmap, nContrast: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var pixels = IntArray(height * width)
        var color: Int
        var red: Float
        var blue: Float
        var green: Float
        var contrast = (100f + nContrast) / 100f
        contrast *= contrast

        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                color = bitmap.getPixel(x, y)
                red = Color.red(color)/255f
                red -= 0.5f
                red *= contrast
                red += 0.5f
                green = Color.green(color)/255f
                green -= 0.5f
                green *= contrast
                green += 0.5f
                blue = Color.blue(color)/255f
                blue -= 0.5f
                blue *= contrast
                blue += 0.5f

                if (red < 0) red = 0f;
                if (red > 1) red = 1f;
                if (blue < 0) blue = 0f;
                if (blue > 1) blue = 1f;
                if (green < 0) green = 0f;
                if (green > 1) green = 1f;

                pixels[width * y + x] = Color.rgb(red,green,blue)
            }
        }
        return Bitmap.createBitmap(pixels, 0, width, width, height, bitmap.config)
    }

    fun gamma(bitmap: Bitmap, nGamma: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var pixels = IntArray(height * width)
        var rgbGamma = IntArray(256)
        var color: Int
        var red: Int
        var blue: Int
        var green: Int
        var invGamma = 1f / nGamma

        for (i in 0..255)
            rgbGamma[i] = Math.min(255,(Math.pow(i/255.0, invGamma.toDouble())*255).toInt())

        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                color = bitmap.getPixel(x, y)
                red = rgbGamma[Color.red(color)]
                blue = rgbGamma[Color.blue(color)]
                green = rgbGamma[Color.green(color)]

                pixels[width * y + x] = Color.rgb(red, green, blue)
            }
        }
        return Bitmap.createBitmap(pixels, 0, width, width, height, bitmap.config)
    }

    fun conv3X3(bitmap: Bitmap, m: ConvMatrix): Bitmap{
        if (0==m.factor)
            return bitmap

        val bSrc = bitmap.copy(bitmap.config,true)
        val nwidth = bitmap.width-2
        val nheight = bitmap.height-2
        var pixel1: Int
        var pixel2: Int
        var pixel3: Int
        var pixel4: Int
        var pixel5: Int
        var pixel6: Int
        var pixel7: Int
        var pixel8: Int
        var pixel9: Int
        var nPixelRed: Int
        var nPixelBlue: Int
        var nPixelGreen: Int

        for(y in 1..nheight){
            for(x in 1..nwidth){
                pixel1 = bitmap.getPixel(x-1,y-1);
                pixel2 = bitmap.getPixel(x,y-1);
                pixel3 = bitmap.getPixel(x+1,y-1);
                pixel4 = bitmap.getPixel(x-1,y);
                pixel5 = bitmap.getPixel(x,y);
                pixel6 = bitmap.getPixel(x+1,y);
                pixel7 = bitmap.getPixel(x-1,y+1);
                pixel8 = bitmap.getPixel(x,y+1);
                pixel9 = bitmap.getPixel(x+1,y+1);

                nPixelRed = (((Color.red(pixel1)*m.topLeft)+(Color.red(pixel2)*m.topMid)+
                        (Color.red(pixel3)*m.topRight)+(Color.red(pixel4)*m.midpLeft)+
                        (Color.red(pixel5)*m.pixel)+(Color.red(pixel6)*m.midRight)+
                        (Color.red(pixel7)*m.bottomLeft)+(Color.red(pixel8)*m.bottomMid)+
                        (Color.red(pixel9)*m.bottomRight))/m.factor)+m.offset
                nPixelBlue = (((Color.blue(pixel1)*m.topLeft)+(Color.blue(pixel2)*m.topMid)+
                        (Color.blue(pixel3)*m.topRight)+(Color.blue(pixel4)*m.midpLeft)+
                        (Color.blue(pixel5)*m.pixel)+(Color.blue(pixel6)*m.midRight)+
                        (Color.blue(pixel7)*m.bottomLeft)+(Color.blue(pixel8)*m.bottomMid)+
                        (Color.blue(pixel9)*m.bottomRight))/m.factor)+m.offset
                nPixelGreen = (((Color.green(pixel1)*m.topLeft)+(Color.green(pixel2)*m.topMid)+
                        (Color.green(pixel3)*m.topRight)+(Color.green(pixel4)*m.midpLeft)+
                        (Color.green(pixel5)*m.pixel)+(Color.green(pixel6)*m.midRight)+
                        (Color.green(pixel7)*m.bottomLeft)+(Color.green(pixel8)*m.bottomMid)+
                        (Color.green(pixel9)*m.bottomRight))/m.factor)+m.offset

                if(nPixelRed<0) nPixelRed = 0;
                if(nPixelRed>255) nPixelRed = 255;
                if(nPixelBlue<0) nPixelBlue = 0;
                if(nPixelBlue>255) nPixelBlue = 255;
                if(nPixelGreen<0) nPixelGreen = 0;
                if(nPixelGreen>255) nPixelGreen = 255;

                bSrc.setPixel(x,y,Color.rgb(nPixelRed,nPixelGreen,nPixelBlue))
            }
        }

        return bSrc
    }

    fun smooth(bitmap: Bitmap,nWeight: Int = 1): Bitmap{
        val m = ConvMatrix()
        m.setAll(1)
        m.pixel = nWeight
        m.factor = nWeight+8

        return conv3X3(bitmap,m)
    }

    fun gaussianBlur(bitmap: Bitmap,nWeight: Int = 4): Bitmap{
        val m = ConvMatrix()
        m.setAll(1)
        m.topMid = 2
        m.midRight = 2
        m.midpLeft = 2
        m.bottomMid = 2
        m.pixel = nWeight
        m.factor = nWeight+12
        m.offset = 0

        return conv3X3(bitmap,m)
    }

    fun sharpen(bitmap: Bitmap,nWeight: Int = 11): Bitmap{
        val m = ConvMatrix()
        m.topLeft = 0
        m.topMid = -2
        m.topRight = 0
        m.midpLeft = -2
        m.midRight = -2
        m.bottomLeft = 0
        m.bottomMid = -2
        m.bottomRight = 0
        m.pixel = nWeight
        m.factor = nWeight-8

        return  conv3X3(bitmap,m)
    }

    fun meanRemoval(bitmap: Bitmap,nWeight: Int = 9): Bitmap{
        val m = ConvMatrix()
        m.setAll(-1)
        m.pixel = nWeight
        m.factor = nWeight-8
        m.offset = 0

        return conv3X3(bitmap,m)
    }

    fun embossing(bitmap: Bitmap,nWeight: Int = 4): Bitmap{
        val m = ConvMatrix()
        m.topLeft = -1
        m.topRight = -1
        m.bottomLeft = -1
        m.bottomRight = -1
        m.pixel = nWeight
        m.offset = 127

        return conv3X3(bitmap,m)
    }

    fun edgeDetection(bitmap: Bitmap, nWeight: Int = 0): Bitmap{
        val m = ConvMatrix()
        m.topLeft = 1
        m.topMid = 1
        m.topRight = 1
        m.bottomLeft = -1
        m.bottomMid = -1
        m.bottomRight = -1
        m.pixel = nWeight
        m.offset = 127

        return conv3X3(bitmap,m)
    }

    fun offsetFilterAntiAlias(bitmap: Bitmap, fp: Array<FloatPoint>): Bitmap{   //Linea 1053
        val bSrc = bitmap.copy(bitmap.config,true)
        val nWidth = bSrc.width
        val nHeight = bSrc.height

        var xoffset: Double
        var yoffset: Double
        var fraction_x: Double
        var fraction_y: Double
        var one_minus_x: Double
        var one_minus_y: Double

        var ceil_x: Int
        var ceil_y: Int
        var floor_x: Int
        var floor_y: Int

        var p1: Int
        var p2: Int
        var p3: Int
        var p4: Int

        for (x in 0..nWidth - 1){
            for (y in 0..nHeight - 1) {
                xoffset = fp[y*nWidth + x].x
                yoffset = fp[y*nWidth + x].y

                //Setup
                floor_x = floor(xoffset).toInt()
                floor_y = floor(yoffset).toInt()
                ceil_x = floor_x + 1
                ceil_y = floor_y + 1
                fraction_x = xoffset - floor_x
                fraction_y = yoffset - floor_y
                one_minus_x = 1.0 - fraction_x
                one_minus_y = 1.0 - fraction_y

                if(floor_y >= 0 && ceil_y < nHeight && floor_x >= 0 && ceil_x < nWidth){
                    p1 = bitmap.getPixel(floor_x,floor_y)
                    p3 = bitmap.getPixel(ceil_x,floor_y)
                    p2 = bitmap.getPixel(floor_x,ceil_y)
                    p4 = bitmap.getPixel(ceil_x,ceil_y)

                    bSrc.setPixel(x,y,rgb(
                            ((one_minus_y * (one_minus_x * (p1.red.toDouble()) + fraction_x * (p3.red.toDouble())))
                                    + (fraction_y * (one_minus_x * (p2.red.toDouble()) + fraction_x * (p4.red.toDouble())))).toInt(),
                            ((one_minus_y * (one_minus_x * (p1.green.toDouble()) + fraction_x * (p3.green.toDouble())))
                                    + (fraction_y * (one_minus_x * (p2.green.toDouble()) + fraction_x * (p4.green.toDouble())))).toInt(),
                            ((one_minus_y * (one_minus_x * (p1.blue.toDouble()) + fraction_x * (p3.blue.toDouble())))
                                    + (fraction_y * (one_minus_x * (p2.blue.toDouble()) + fraction_x * (p4.blue.toDouble())))).toInt()
                    ))
                }
            }
        }

        return bSrc;
    }

    fun offsetFilterAbs(bitmap: Bitmap, offset: Array<Point>): Bitmap{   //Linea 953
        val bSrc = bitmap.copy(bitmap.config,true)
        val nWidth = bSrc.width
        val nHeight = bSrc.height

        var xoffset: Int
        var yoffset: Int

        var pixel: Int
        var pR: Int
        var pG: Int
        var pB: Int

        for (x in 0..nWidth - 1){
            for (y in 0..nHeight - 1) {
                xoffset = offset[y*nWidth + x].x
                yoffset = offset[y*nWidth + x].y

                if(yoffset >= 0 && yoffset < nHeight && xoffset >= 0 && xoffset < nWidth){
                    pixel = bitmap.getPixel(xoffset,yoffset)
                    pR = pixel.red
                    pG = pixel.green
                    pB = pixel.blue

                    bSrc.setPixel(x,y,rgb(pR,pG,pB))
                }
            }
        }

        return bSrc;
    }

    fun offsetFilter(bitmap: Bitmap, offset: Array<Point>): Bitmap{   //Linea 1003
        val bSrc = bitmap.copy(bitmap.config,true)
        val nWidth = bSrc.width
        val nHeight = bSrc.height

        var xoffset: Int
        var yoffset: Int

        var pixel: Int
        var pR: Int
        var pG: Int
        var pB: Int

        for (x in 0..nWidth - 1){
            for (y in 0..nHeight - 1) {
                xoffset = offset[y*nWidth + x].x
                yoffset = offset[y*nWidth + x].y

                if(y + yoffset >= 0 && y + yoffset < nHeight && x + xoffset >= 0 && x + xoffset < nWidth){
                    pixel = bitmap.getPixel(x + xoffset,y + yoffset)
                    pR = pixel.red
                    pG = pixel.green
                    pB = pixel.blue

                    bSrc.setPixel(x,y,rgb(pR,pG,pB))
                }
            }
        }

        return bSrc;
    }

    fun swirl(bitmap: Bitmap, fDegree: Double = 0.05, bsmoothing: Boolean = true): Bitmap{
        val nWidth = bitmap.width
        val nHeight = bitmap.height

        var fp: Array<FloatPoint> = Array(nWidth*nHeight, {i -> FloatPoint()})
        var pt: Array<Point> = Array(nWidth*nHeight, {i -> Point()})
        var mid = Point(nWidth/2,nHeight/2)
        var theta: Double
        var radius: Double
        var newX: Double
        var newY: Double

        for (y in 0..nHeight - 1){
            for (x in 0..nWidth - 1) {
                var trueX = x - mid.x
                var trueY = y -mid.y
                theta = Math.atan2(trueY.toDouble(),trueX.toDouble())
                radius = Math.sqrt((trueX*trueX) + (trueY*trueY).toDouble())

                newX = mid.x + (radius * Math.cos(theta+fDegree*radius))
                newY = mid.y + (radius * Math.sin(theta+fDegree*radius))

                var p = Point()
                val pf = FloatPoint()

                if(newX  > 0 && newX < nWidth){
                    pf.x = newX
                    p.x = newX.toInt()
                }
                else{
                    pf.x = x.toDouble()
                    p.x = x
                }
                if(newY  > 0 && newY < nHeight){
                    pf.y = newY
                    p.y = newY.toInt()
                }
                else{
                    pf.y = y.toDouble()
                    p.y = y
                }
                fp.set(y*nWidth + x, pf)
                pt.set(y*nWidth + x, p)
            }
        }
        if(bsmoothing)
            return  offsetFilterAntiAlias(bitmap,fp)
        else
            return offsetFilterAbs(bitmap,pt)
    }

    fun timeWarp(bitmap: Bitmap, factor: Int, bsmoothing: Boolean = true): Bitmap{
        val nWidth = bitmap.width
        val nHeight = bitmap.height

        var fp: Array<FloatPoint> = Array(nWidth*nHeight, {i -> FloatPoint()})
        var pt: Array<Point> = Array(nWidth*nHeight, {i -> Point()})
        var mid = Point(nWidth/2,nHeight/2)
        var theta: Double
        var radius: Double
        var newX: Double
        var newY: Double

        for (y in 0..nHeight - 1){
            for (x in 0..nWidth - 1) {
                var trueX = x - mid.x
                var trueY = y -mid.y
                theta = Math.atan2(trueY.toDouble(),trueX.toDouble())
                radius = Math.sqrt((trueX*trueX) + (trueY*trueY).toDouble())

                var newRadius = Math.sqrt(radius)*factor

                newX = mid.x + (newRadius * Math.cos(theta))
                newY = mid.y + (newRadius * Math.sin(theta))

                var p = Point()
                val pf = FloatPoint()

                if(newX  > 0 && newX < nWidth){
                    pf.x = newX
                    p.x = newX.toInt()
                }
                else{
                    pf.x = 0.0
                    p.x = 0
                }
                if(newY  > 0 && newY < nHeight){
                    pf.y = newY
                    p.y = newY.toInt()
                }
                else{
                    pf.y = 0.0
                    p.y = 0
                }
                fp.set(y*nWidth + x, pf)
                pt.set(y*nWidth + x, p)
            }
        }
        if(bsmoothing)
            return  offsetFilterAbs(bitmap,pt)
        else
            return offsetFilterAntiAlias(bitmap,fp)
    }

    fun moire(bitmap: Bitmap, fDegree: Double): Bitmap{
        val nWidth = bitmap.width
        val nHeight = bitmap.height

        var pt: Array<Point> = Array(nWidth*nHeight, {i -> Point()})
        var mid = Point(nWidth/2,nHeight/2)
        var theta: Double
        var radius: Double
        var newX: Double
        var newY: Double

        for (y in 0..nHeight - 1){
            for (x in 0..nWidth - 1) {
                var trueX = x - mid.x
                var trueY = y -mid.y

                theta = Math.atan2(trueX.toDouble(),trueY.toDouble())
                radius = Math.sqrt((trueX*trueX) + (trueY*trueY).toDouble())

                newX = (radius * Math.sin(theta + fDegree*radius))
                newY = (radius * Math.sin(theta + fDegree*radius))

                var p = Point()

                if(newX  > 0 && newX < nWidth){
                    p.x = newX.toInt()
                }
                else{
                    p.x = 0
                }
                if(newY  > 0 && newY < nHeight){
                    p.y = newY.toInt()
                }
                else{
                    p.y = 0
                }
                pt.set(y*nWidth + x, p)
            }
        }

        return  offsetFilterAbs(bitmap,pt)
    }

    fun water(bitmap: Bitmap, nWave: Short, bsmoothing: Boolean = true): Bitmap{
        val nWidth = bitmap.width
        val nHeight = bitmap.height

        var fp: Array<FloatPoint> = Array(nWidth*nHeight, {i -> FloatPoint()})
        var pt: Array<Point> = Array(nWidth*nHeight, {i -> Point()})

        var xo: Double
        var yo: Double
        var newX: Double
        var newY: Double

        for (y in 0..nHeight - 1){
            for (x in 0..nWidth - 1) {
                xo = nWave * Math.sin(2.0 * Math.PI * y/128.0)
                yo = nWave * Math.cos(2.0 * Math.PI * x/128.0)

                newX = x + xo
                newY = y + yo

                var p = Point()
                val pf = FloatPoint()

                if(newX  > 0 && newX < nWidth){
                    pf.x = newX
                    p.x = newX.toInt()
                }
                else{
                    pf.x = 0.0
                    p.x = 0
                }
                if(newY  > 0 && newY < nHeight){
                    pf.y = newY
                    p.y = newY.toInt()
                }
                else{
                    pf.y = 0.0
                    p.y = 0
                }
                fp.set(y*nWidth + x, pf)
                pt.set(y*nWidth + x, p)
            }
        }
        if(bsmoothing)
            return  offsetFilterAbs(bitmap,pt)
        else
            return offsetFilterAntiAlias(bitmap,fp)
    }

    fun pixelate(bitmap: Bitmap, pixel: Int, bGrid: Boolean): Bitmap{
        val nWidth = bitmap.width
        val nHeight = bitmap.height

        var pt: Array<Point> = Array(nWidth*nHeight, {i -> Point()})
        var newX: Int
        var newY: Int

        for (y in 0..nHeight - 1){
            for (x in 0..nWidth - 1) {

                newX = pixel - x%pixel
                newY = pixel - y%pixel

                var p = Point()

                if(bGrid && newX == pixel){
                    p.x = -x;
                }
                else if(x + newX > 0 && x + newX < nWidth){
                    p.x = newX
                }
                else{
                    p.x = 0
                }

                if(bGrid && newY == pixel){
                    p.y = -y
                }
                else if(y + newY  > 0 && y + newY < nHeight){
                    p.y = newY
                }
                else{
                    p.y = 0
                }
                pt.set(y*nWidth + x, p)
            }
        }

        return  offsetFilter(bitmap,pt)
    }

    fun randomJitter(bitmap: Bitmap, nDegree: Short): Bitmap{
        val nWidth = bitmap.width
        val nHeight = bitmap.height

        var pt: Array<Point> = Array(nWidth*nHeight, {i -> Point()})
        var newX: Int
        var newY: Int
        var nHalf: Short = Math.floor(nDegree/2.0).toShort()
        var rnd = Random()

        for (y in 0..nHeight - 1){
            for (x in 0..nWidth - 1) {

                newX = rnd.nextInt(nDegree.toInt()) - nHalf
                newY = rnd.nextInt(nDegree.toInt()) - nHalf

                var p = Point()

                if(x + newX > 0 && x + newX < nWidth){
                    p.x = newX
                }
                else{
                    p.x = 0
                }

                if(y + newY  > 0 && y + newY < nHeight){
                    p.y = newY
                }
                else{
                    p.y = 0
                }
                pt.set(y*nWidth + x, p)
            }
        }

        return  offsetFilter(bitmap,pt)
    }

    fun zoom(bitmap: Bitmap,nWidth: Int, nHeight: Int): Bitmap{
        var b = Bitmap.createBitmap(nWidth,nHeight,bitmap.config)
        b = b.copy(b.config,true)

        var nXFactor = bitmap.width/nWidth.toDouble()
        var nYFactor = bitmap.height/nHeight.toDouble()

        var fraction_x: Double
        var fraction_y: Double
        var one_minus_x: Double
        var one_minus_y: Double
        var ceil_x: Int
        var ceil_y: Int
        var floor_x: Int
        var floor_y: Int
        var c1: Int
        var c2: Int
        var c3: Int
        var c4: Int
        var red: Int
        var green: Int
        var blue: Int
        var b1: Int
        var b2: Int

        for(x in 0..b.width - 1){
            for(y in 0..b.height - 1) {
                floor_x = Math.floor(x*nXFactor).toInt()
                floor_y = Math.floor(y*nYFactor).toInt()
                ceil_x = floor_x + 1
                if(ceil_x >= bitmap.width)
                    ceil_x = floor_x
                ceil_y = floor_y + 1
                if(ceil_y >= bitmap.height)
                    ceil_y = floor_y
                fraction_x = x * nXFactor - floor_x
                fraction_y = y * nYFactor - floor_y
                one_minus_x = 1.0 - fraction_x
                one_minus_y = 1.0 - fraction_y

                c1 = bitmap.getPixel(floor_x,floor_y)
                c2 = bitmap.getPixel(ceil_x,floor_y)
                c3 = bitmap.getPixel(floor_x,ceil_y)
                c4 = bitmap.getPixel(ceil_x,ceil_y)

                b1 = (one_minus_x * c1.blue + fraction_x * c2.blue).toInt()
                b2 = (one_minus_x * c3.blue + fraction_x * c4.blue).toInt()
                blue = (one_minus_y * b1 + fraction_y * b2).toInt()

                b1 = (one_minus_x * c1.green + fraction_x * c2.green).toInt()
                b2 = (one_minus_x * c3.green + fraction_x * c4.green).toInt()
                green = (one_minus_y * b1 + fraction_y * b2).toInt()

                b1 = (one_minus_x * c1.red + fraction_x * c2.red).toInt()
                b2 = (one_minus_x * c3.red + fraction_x * c4.red).toInt()
                red = (one_minus_y * b1 + fraction_y * b2).toInt()

                b.setPixel(x,y, argb(255,red,green,blue))
            }
        }
        return b
    }

    fun update(){
        this.image = BitmapDrawable(this.resources, this.bitmap)
        this.imgFoto.setImageDrawable(image)
    }
    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap{
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) (-1f) else 1f, if (vertical) (-1f) else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true);
    }
}

class ConvMatrix{
    var topLeft = 0
    var topMid = 0
    var topRight = 0
    var midpLeft = 0
    var pixel = 0
    var midRight = 0
    var bottomLeft = 0
    var bottomMid = 0
    var bottomRight = 0
    var factor = 1
    var offset = 0

    fun setAll(nVal:Int){
        topLeft = nVal
        topMid = nVal
        topRight = nVal
        midpLeft = nVal
        pixel = nVal
        midRight = nVal
        bottomLeft = nVal
        bottomMid = nVal
        bottomRight = nVal
    }
}

class FloatPoint{
    var x: Double = 0.0
    var y: Double = 0.0
}