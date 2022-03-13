package com.example.netflexe;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class CinemaCollection {
    private ArrayList<Cinema> collection = new ArrayList<Cinema>();

    public Image getImage(String name)
    {
        int resultat = 0;
        for(int i = 0 ; i < collection.size(); i++)
        {
            if (collection.get(i).getName() == name)
            {
                resultat = i ;
            }

        }
        return collection.get(resultat).getImage();
    }

    public void addCinema(Cinema cinema)
    {
        collection.add(cinema);
    }

    public String getName(int i)
    {
        return collection.get(i).getName();
    }

    public int getSize()
    {
        return collection.size();
    }
}
