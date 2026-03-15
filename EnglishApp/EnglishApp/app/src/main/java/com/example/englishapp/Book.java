package com.example.englishapp;
import java.io.Serializable;
public class Book implements Serializable {

    String title;
    int image;
    String file;

    public Book(String title, int image, String file){
        this.title = title;
        this.image = image;
        this.file = file;
    }

    public String getTitle(){
        return title;
    }

    public int getImage(){
        return image;
    }

    public String getFile(){
        return file;
    }
}