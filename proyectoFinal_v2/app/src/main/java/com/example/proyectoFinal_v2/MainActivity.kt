package com.example.proyectoFinal_v2

import android.os.Bundle

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.LectorFoco


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Amplify.addPlugin(AWSApiPlugin()) // UNCOMMENT this line once backend is deployed
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.configure(applicationContext)
            Log.i("Amplify", "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e("Amplify", "Could not initialize Amplify", e)
        }

        setContent {
            botones()
        }

    }
}

@Composable
fun botones(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val padding = Modifier.padding(vertical = 8.dp)

            //CREATE
            Button(
                onClick = { create() },
                modifier = padding
            ) {
                Text(text = "Create")
            }

            //Read All
            Button(
                onClick = { readAll() },
                modifier = padding
            ) {
                Text(text = "Listar")
            }
        }
    }
}

//aqui se le esta mandando valores para almacenar en el modelo que se creo en Amplify
//son estaticos no son ingresados por el usuario, aunque eso simplmente se haria con un box
private fun create(){
    val item: LectorFoco = LectorFoco.builder()
        .intensidad(5)
        .hora(Temporal.Time("22:30"))
        .fecha(Temporal.Date("1910-01-01"))
        .build()
    Amplify.DataStore.save(
        item,
        { success -> Log.i("Amplify", "Saved item: " + success.item().intensidad) },
        { error -> Log.e("Amplify", "Could not save item to DataStore", error) }
    )
}

//este metodo lista los id y la intensidad respectiva
//solo se muestra en consola aun ya que hubo un problema con el paging
private fun readAll(){
    Amplify.DataStore.query(
        LectorFoco::class.java,
        { items ->
            while (items.hasNext()) {
                val item = items.next()
                Log.i("Amplify", "Queried item: " + item.id + "INTENSIDAD DEL FOCO" + item.intensidad)
            }
        },
        { failure -> Log.e("Tutorial", "Could not query DataStore", failure) }
    )
}