package com.fireblend.uitest.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fireblend.uitest.R;
import com.fireblend.uitest.entities.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    GridView list;
    List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (GridView)findViewById(R.id.lista_contactos);

        //Se ejecuta el request usando un AsyncTask apenas se abre la aplicacion
        new APITask(this).execute("https://jsonplaceholder.typicode.com/posts/");
    }

    //Se declara una clase estatica que extiende AsyncTask. El AsyncTask se declara en este
    //caso como uno que recibe Strings como entrada, usa enteros para actualizar el progreso (no usado en este ejemplo)
    //y finalmente retorna un String (la respuesta del servicio web)
    private static class APITask extends AsyncTask<String, Integer, String> {
        //Puesto que la clase es estatica, necesita recibir un Activity (Context) para funcionar
        //Se utiliza un Weak Reference para no arriesgar leaks de memoria.
        WeakReference<MainActivity> act;

        //En el constructor se recibe el Activity y asocia a un WeakReference
        APITask(MainActivity act){
            this.act = new WeakReference<>(act);
        }

        @Override
        protected String doInBackground(String[] params) {
            try {
                //Se crea el URL a contactar
                URL endpoint = new URL(params[0]);

                //Se abre la conexion
                HttpsURLConnection connection =
                        (HttpsURLConnection) endpoint.openConnection();

                //Se ejecuta el request. Un codigo 200 indica que fue exitoso.
                if (connection.getResponseCode() == 200) {
                    //Se interpreta la respuesta del servicio usando connection.getInputStream()
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(connection.getInputStream(), "UTF-8");

                    BufferedReader r = new BufferedReader(responseBodyReader);
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null){
                        builder.append(line);
                    }

                    //Se retorna el String (JSON, en este caso)
                    return builder.toString();
                } else {
                    //Si se recibio algun codigo de error, se retorna null.
                    return null;
                }
            } catch (Exception e) {
                //Si ocurre alguna excepcion, se retorna null.
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String respuesta) {
            //En el onPostExecute, llamamos a setupList dentro del activity
            //para seguir con el setup del ListView.
            super.onPostExecute(respuesta);
            act.get().setupList(respuesta);
        }
    };

    private void setupList(String respuesta) {
        posts = new ArrayList();

        try {
            //La respuesta del endpoint es una lista en formato JSON, por lo que
            //cargamos un objeto JSONArray con el string recibido
            JSONArray listaPrincipal = new JSONArray(respuesta);
            //Para cada elemento en la lista recibido...
            for (int i =0; i < listaPrincipal.length(); i++){
                //Convertimos el elemento en una instancia de JSONObject
                JSONObject objeto = listaPrincipal.getJSONObject(i);

                //Y lo convertimos en un objeto Post que va a ser agregado a la
                //lista asociada al adapter
                posts.add(new Post(objeto.getString("title"),
                        objeto.getString("body"),
                        objeto.getInt("id"),
                        objeto.getInt("userId")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error leyendo respuesta!", Toast.LENGTH_SHORT).show();
        }

        //Le asignamos a la lista un nuevo BaseAdapter, implementado a continuación
        list.setAdapter(new BaseAdapter() {
            @Override
            //Retorna el numero de elementos en la lista.
            public int getCount() {
                return posts.size();
            }

            @Override
            //Retorna el elemento que pertenece a la posición especificada.
            public Object getItem(int position) {
                return posts.get(position);
            }

            @Override
            //Devuelve un identificador único para cada elemento de la lista.
            //En nuestro caso, basta con devolver la posición del elemento en la lista.
            public long getItemId(int position) {
                return position;
            }

            @Override
            //Devuelve la vista que corresponde a cada elemento de la lista
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View row = inflater.inflate(R.layout.contact_item, parent, false);

                TextView name, age;

                name = (TextView) row.findViewById(R.id.name);
                age = (TextView) row.findViewById(R.id.age);

                name.setText(posts.get(position).title);
                age.setText(posts.get(position).body);

                return row;
            }
        });

        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
