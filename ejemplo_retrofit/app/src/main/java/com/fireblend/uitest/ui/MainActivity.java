package com.fireblend.uitest.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fireblend.uitest.R;
import com.fireblend.uitest.entities.Post;
import com.fireblend.uitest.service.GestorServicio;
import com.fireblend.uitest.service.ServicioPosts;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    GridView list;
    List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (GridView)findViewById(R.id.lista_contactos);
        Button btn = (Button)findViewById(R.id.boton);
        btn.setOnClickListener(this);

        //Se llama al metodo de obtenerPosts para llamar al servicio.
        obtenerPosts();
    }


    private void obtenerPosts(){
        //Se obtiene la referencia singleton desde el gestor.
        ServicioPosts servicio = GestorServicio.obtenerServicio();

        //Se llama al metodo definido en el servicio para obtener los posts.
        servicio.obtenerTodosLosPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                //Si es exitosa, recuperamos la lista recibida de response.body()
                posts = response.body();
                //y llamamos al metodo para mostrar la lista
                setupList();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                //Si no, se muestra un error
                Toast.makeText(MainActivity.this,
                        "Error al interactuar con el servicio",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupList() {
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
        //Se obtiene la referencia singleton desde el gestor.
        ServicioPosts servicio = GestorServicio.obtenerServicio();

        //Se llama al metodo definido en el servicio para obtener los detalles de un post en particular
        servicio.obtenerDetallesDePost(posts.get(position).postId+"").enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                //Si es exitosa, recuperamos la lista recibida de response.body()
                Post resultado = response.body();
                //Mostramos un mensaje para probar que fue exitosa la recuperacion de informacion
                Toast.makeText(MainActivity.this,
                        resultado.title,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                //Si no, se muestra un error
                Toast.makeText(MainActivity.this,
                        "Error al interactuar con el servicio",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        //Se obtiene la referencia singleton desde el gestor.
        ServicioPosts servicio = GestorServicio.obtenerServicio();

        Post post = new Post("Titulo", "Cuerpo", 1001, 4);

        //Se llama al metodo definido en el servicio para crear un nuevo objeto Post
        servicio.crearNuevoPost(post).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                //Si es exitoso, recuperamos la copia del post creado
                Post resultado = response.body();
                //Mostramos un mensaje para probar que fue exitosa la creacion del elemento
                Toast.makeText(MainActivity.this,
                        resultado.title,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                //Si no, se muestra un error
                Toast.makeText(MainActivity.this,
                        "Error al interactuar con el servicio",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
