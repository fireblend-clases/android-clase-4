package com.fireblend.uitest.entities;

public class Post {
    //Clase entidad para contener cada elemento de la lista, el cual representa un objeto Post.
    public String title;
    public int userId;
    public int id;
    public String body;

    public Post(String title, String body, int id, int userId){
        this.title = title;
        this.body = body;
        this.id = id;
        this.userId = userId;
    }
}
