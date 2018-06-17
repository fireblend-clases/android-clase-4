package com.fireblend.uitest.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergio on 8/20/2017.
 */

public class Post {
    //Clase entidad para contener cada elemento de la lista, el cual representa un Contacto.
    public String title;
    public int userId;

    //En caso de que el nombre de un atributo en la estructura JSON sea diferente en el servicio
    //de como se llama en el objeto entidad, se puede asociar usando el annotation @SerializedName
    @SerializedName("id")
    public int postId;

    public String body;
    
    public Post(String title, String body, int id, int userId){
        this.title = title;
        this.body = body;
        this.postId = id;
        this.userId = userId;
    }
}
