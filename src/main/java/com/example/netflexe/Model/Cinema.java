package com.example.netflexe.Model;

import javafx.scene.image.Image;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cinema {

    private int id_cine;
    private String nom;
    private MovieCollection filmP = new MovieCollection();
    private Image image = null;
    private String imageString = "";
    private List<Salle> salles = new ArrayList<>();
    private ArrayList<String> promoUtilisees = new ArrayList<String>();
    private ArrayList<String> filmVendus = new ArrayList<String>();

    public Cinema()
    {

    }

    public Cinema(int id_cine,String nom,String image)
    {
        this.id_cine = id_cine;
        this.nom = nom;
        this.imageString = image;
    }

    public boolean checkMovie(String name)
    {
        return filmP.checkBoolean(name);
    }

    public void ajoutFilm(Movie movie)
    {
        filmP.addMovie(movie);
    }

    public void setImage()
    {
        image = new Image(imageString);

    }
    public void set_id_cine(int id_cine)
    {
        this.id_cine = id_cine;
    }
    public int get_id_cine()
    {
        return this.id_cine;
    }

    public Image getImage()
    {
        return image;
    }

    public String getImageString()
    {
        return imageString;
    }

    public String getName()
    {
        return this.nom;
    }

    public MovieCollection getFilmP()
    {
        return filmP;
    }

    public void setFilmP(MovieCollection filmP)
    {
        this.filmP = filmP;
    }

    public List<Salle> getSalles()
    {
        return salles;
    }

    public void addSalles(Salle salle)
    {
        salles.add(salle);
    }

    public void addSeance(int salle, Seance seance)
    {
        for (var elem : salles)
        {
            if (elem.getNumero() == salle)
            {
                elem.addSeance(seance);
            }
        }
    }

    public void updateStatsPromo(String promo)
    {
        promoUtilisees.add(promo);
    }
    public void updateStatsFilm(String title)
    {
        filmVendus.add(title);
    }

    public ArrayList<String> getPromoUtilisees()
    {
        return promoUtilisees;
    }

    public ArrayList<String> getFilmVendus( ) {return filmVendus;}

    public void setSalles(List<Salle> salles)
    {
        this.salles = salles;
    }
}
