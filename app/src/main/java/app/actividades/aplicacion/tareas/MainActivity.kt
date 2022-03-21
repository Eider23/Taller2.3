package app.actividades.aplicacion.tareas

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    var idT : EditText?=null
    var tarea : EditText?=null
    var buscar : Button?=null
    var agreagar : Button?=null
    var eliminar : Button?=null
    var etiqueta : TextView?=null
    var basedatos = BaseDatos(this,"ejercicio1",null,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        idT = findViewById(R.id.numeracion)
        tarea = findViewById(R.id.tareas)
        buscar = findViewById(R.id.buscar)
        agreagar= findViewById(R.id.agregar)
        eliminar = findViewById(R.id.eliminar)
        etiqueta = findViewById(R.id.etiqueta)

        agreagar?.setOnClickListener {
            insertar()
        }
        buscar?.setOnClickListener {
            pedirId(buscar?.text.toString())
        }
        eliminar?.setOnClickListener {
            pedirId(eliminar?.text.toString())
        }
    }

    fun pedirId(etiqueta: String){
        var campo = EditText(this)
        campo.inputType = InputType.TYPE_CLASS_NUMBER
        AlertDialog.Builder(this).setTitle("ATENCIÓN")
            .setMessage("Escriba la numeracion de la tarea a ${etiqueta}: ").setView(campo)
            .setNeutralButton("CANCELAR"){dialog,which->
                return@setNeutralButton
            }
            .setPositiveButton("BUSCAR") { dialog, which ->
                if (validarCampo(campo) == false) {
                    Toast.makeText(this@MainActivity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(campo.text.toString(), etiqueta)

            }.show()

    }
    fun buscar(id: String, botonEtiqueta: String){
        try{
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM PERSONA WHERE ID="+id
            var resultado = transaccion.rawQuery(SQL,null)
            if(resultado.moveToFirst()==true){
                var cadena ="TAREA: "+resultado.getString(1)
                if(botonEtiqueta.startsWith("Buscar")){
                    etiqueta?.setText(cadena)
                }
                if(botonEtiqueta.startsWith("Eliminar")){
                    var alerta = AlertDialog.Builder(this)
                    alerta.setTitle("ATENCIÓN").setMessage(cadena)
                        .setNeutralButton("No"){dialog,which->
                            return@setNeutralButton
                        }
                        .setPositiveButton("Sí"){dialog,which->
                            eliminar(id)
                        }
                        .show()
                }

            }else{
                mensaje("ATENCIÓN","AL PARECER NO ENCONTRÉ LA TAREA.")
            }
            transaccion.close()
        }catch(err:SQLiteException){
            mensaje("ERROR","NO SE PUDO REALIZAR EL SELECT.")
        }
    }

    fun eliminar(id:String){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "DELETE FROM PERSONA WHERE ID="+id
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("ÉXITO","SE ELIMINÓ CORRECTAMENTE LA TAREA.")

        }catch (err:SQLiteException){
            mensaje("ERROR","NO SE PUDO ELIMINAR LA TAREA.")
        }
    }


    fun insertar(){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO PERSONA VALUES(ID,'TAREA')"

            if(validarCampos()==false){
                mensaje("ERROR","ALGÚN CAMPO ESTÁ VACÍO.")
                return
            }
            SQL = SQL.replace("ID",idT?.text.toString())
            SQL = SQL.replace("TAREA",tarea?.text.toString())
            transaccion.execSQL(SQL)
            transaccion.close()
            limpiarCampos()
            mensaje("ÉXITO","SE AGREGO CORRECTAMENTE LA TAREA.")
        }catch(err: SQLiteException){
            mensaje("ERROR","NO SE PUDO AGREGAR LA TAREA.")
        }
    }

    fun mensaje(titulo:String,texto:String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(texto).setPositiveButton("Ok"){dialog,which->}.show()
    }
    fun  validarCampos():Boolean{
        if((idT?.text.toString().toString().isEmpty()) || (tarea?.text.toString().isEmpty())){
            return false
        }else{
            return true
        }
    }
    fun validarCampo(campo: EditText): Boolean{
        if(campo.text.toString().isEmpty()){
            return false
        }else{
            return true
        }
    }
    fun limpiarCampos(){
        idT?.setText("")
        tarea?.setText("")
    }
}


