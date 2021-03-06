package com.example.netflexe.Controller;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import com.example.netflexe.Model.*;
import com.example.netflexe.Model.Profil;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
/**
 * Class Contenant l'ensemble des requetes SQL de l'application, et qui contient le main permettant de lancer l'application
 */
public class HelloApplication extends Application {

    private Stage primaryStage;
    private SceneController sceneController;
    private Connection myConn;
    private Statement myStat;
    private Statement myStat2;
    private Profil user;
    private String tempUserId;
    private RunnableDemo thread;

    public SceneController get_sceneController()
    {
        return this.sceneController;
    }


    /**
     * Constructeur principale de notre application créé la connection à la base de donnée.
     * La base étant en remote sur un serveur il n'est pas nécessaire de lui fournir de paramètre
     */
    public HelloApplication()
    {
        try
        {
            this.myConn = DriverManager.getConnection("jdbc:mysql://fournierfamily.ovh:3306/Netflece", "jps", "poojava");
            this.myStat = myConn.createStatement();
            this.myStat2 = myConn.createStatement();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private MovieCollection[] collection = {new MovieCollection(),new MovieCollection(),new MovieCollection(),new MovieCollection(),new MovieCollection(),new MovieCollection()};
    private ActorCollection collectionActor = new ActorCollection();
    public int selectedMenu ;
    private ArrayList<String> genre = new ArrayList<String>();

    

    /**
     * 
     * @param id_film id du film dans la base de donnés pour récupéré les acteurs liés à un film
     * @return une collection d'acteur
     * @throws IOException
     */
    public ActorCollection CollectionActeursMovie (String id_film) throws IOException {
        collectionActor.erase();
        try {
            ResultSet myRes = myStat.executeQuery("SELECT DISTINCT person.nom, personnage.nom, prenom, pp, date_de_naissance, biographie FROM person JOIN film_person on film_person.id_person = person.id_person JOIN film \n" +
                    "ON (film_person.id_film = film.id_film) JOIN personnage ON personnage.id_film = film_person.id_film\n" +
                    " AND personnage.id_person = person.id_person WHERE film.id_film =" + id_film + ";");

            while (myRes.next()) {
                String nom = myRes.getString("person.nom");
                if(nom == null)
                    nom = "Donatien";
                String prenom = myRes.getString("prenom");
                if(prenom == null)
                    prenom = "Chevillard";
                String pp = myRes.getString("pp");
                if(pp == null)
                    pp = "https://scontent-cdg2-1.xx.fbcdn.net/v/t31.18172-8/28335920_10155374977864352_2211314611360502777_o.jpg?_nc_cat=108&ccb=1-5&_nc_sid=174925&_nc_ohc=uX32zEDgc6YAX_BmANe&_nc_ht=scontent-cdg2-1.xx&oh=00_AT-ZbRNI9ccH3cfJIXite7GdJ_WqxwvrfQQs8hmqWsH9Cg&oe=6262AF32";
                    //pp = "https://tse2.mm.bing.net/th?id=OIP.X-25juJ5xWiHhacWDz8vnwHaLH&pid=Api";
                String date_de_naissance = myRes.getString("date_de_naissance");
                if(date_de_naissance == null)
                    date_de_naissance = "2001-09-11";
                String biographie = myRes.getString("biographie");
                if(biographie == null)
                    biographie = "Il/Elle est né(e), a vécu et tourné dans ce film, et va mourir un jour.";
                String role = myRes.getString("personnage.nom");
                if(role == null)
                    role = "Figurant";
                collectionActor.addActor(new Actor(prenom, nom, pp, date_de_naissance, biographie, role));
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }



        return collectionActor;
    }
    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Netflece");
        this.primaryStage.setResizable(false);
        this.primaryStage.getIcons().add(new Image("file:Netflece_logo.png"));
        sceneController = new SceneController(primaryStage, user, collection, this);
    }
    /**
     * méthode principale pour la récupération des informations de la base de données.
     * Cette méthode va remplir les différentes collection de film et géré les genre aimé par l'utilisateur afin d'avoir une experience personalisé.
     * Cette méthode lance aussi un autre thread afin de charger les images pendant que l'application fonctionne
     */
    public void load_bdd_movie()
    {
        try
        {
            if(user != null)
            {
                ResultSet myres = myStat.executeQuery("SELECT DISTINCT genre.nom from utilisateur_genre JOIN genre ON utilisateur_genre.id_genre = genre.id_genre where id_user ='"+ String.valueOf(user.get_id()) +"' LIMIT 5;");
                while(myres.next())
                {
                    String tempgenre = myres.getString("nom");
                    genre.add(tempgenre);
                    user.add_genre(tempgenre);
                }
            }
            else
            {
                genre.add("Action");
                genre.add("Science-Fiction");
                genre.add("Aventure");
                genre.add("Animation");
                genre.add("Comédie");
            }




            for(int i = 0; i<1;i++)
            {
                ResultSet myRes = myStat.executeQuery("SELECT DISTINCT f.id_film,nom_film, poster, date_de_sortie, duree, synopsis, slogan, trailer, person.nom, person.prenom FROM film as f LEFT JOIN realisateur ON realisateur.id_film = f.id_film LEFT JOIN person ON person.id_person = realisateur.id_person ORDER BY f.id_film ASC;");
                int oldId = -1;
                while(myRes.next())
                {
                    if(myRes.getInt("f.id_film") != oldId)
                    {
                        String poster = myRes.getString("poster").replace("600","300").replace("900","450");
                        String dateDeSortie = myRes.getString("date_de_sortie");
                        String duree = myRes.getString("duree");
                        String synopsis = myRes.getString("synopsis");
                        String slogan = myRes.getString("slogan");
                        String id_film = myRes.getString("id_film");
                        String trailer;
                        if(myRes.getString("trailer") != "" && myRes.getString("trailer") != null)
                        {
                            trailer = myRes.getString("trailer").split("=")[1];
                        }
                        else
                        {
                            trailer = null;
                        }
                        //System.out.println(poster);
                        String realisateur = "";
                        if(myRes.getString("person.prenom") != null && myRes.getString("person.prenom") != "")
                        {
                            realisateur += myRes.getString("person.prenom");
                        }
                        if(myRes.getString("person.nom") != null && myRes.getString("person.nom") != "")
                        {
                            if(!realisateur.equals(""))
                            {
                                realisateur += " " + myRes.getString("person.nom");
                            }
                            else
                            {
                                realisateur += myRes.getString("person.nom");
                            }
                        }
                        collection[5].addMovie(new Movie(myRes.getString("nom_film"),realisateur,poster, dateDeSortie, dateDeSortie, duree, synopsis, slogan, id_film, trailer));
                        oldId = myRes.getInt("f.id_film");
                    }
                }
            }

            for(int i = 0; i<5;i++)
            {
                ResultSet myRes = myStat.executeQuery("SELECT DISTINCTROW f.id_film,nom_film, poster, date_de_sortie, duree, synopsis, slogan, trailer, person.nom, person.prenom FROM film as f LEFT JOIN film_genre ON (f.id_film = film_genre.id_film) LEFT JOIN genre ON (genre.id_genre = film_genre.id_genre) LEFT JOIN realisateur ON realisateur.id_film = f.id_film LEFT JOIN person ON person.id_person = realisateur.id_person  WHERE genre.nom = '" + genre.get(i) + "' ORDER BY f.id_film ASC;");
                int oldId = -1;
                while(myRes.next())
                {
                    if(myRes.getInt("f.id_film") != oldId)
                    {
                        String poster = myRes.getString("poster").replace("600","300").replace("900","450");
                        String dateDeSortie = myRes.getString("date_de_sortie");
                        String duree = myRes.getString("duree");
                        String synopsis = myRes.getString("synopsis");
                        String slogan = myRes.getString("slogan");
                        String id_film = myRes.getString("id_film");
                        String trailer;
                        if(myRes.getString("trailer") != "" && myRes.getString("trailer") != null)
                        {
                            trailer = myRes.getString("trailer").split("=")[1];
                        }
                        else
                        {
                            trailer = null;
                        }
                        //System.out.println(poster);
                        String realisateur = "";
                        if(myRes.getString("person.prenom") != null && myRes.getString("person.prenom") != "")
                        {
                            realisateur += myRes.getString("person.prenom");
                        }
                        if(myRes.getString("person.nom") != null && myRes.getString("person.nom") != "")
                        {
                            if(!realisateur.equals(""))
                            {
                                realisateur += " " + myRes.getString("person.nom");
                            }
                            else
                            {
                                realisateur += myRes.getString("person.nom");
                            }
                        }
                        collection[i].addMovie(new Movie(myRes.getString("nom_film"),realisateur,poster, dateDeSortie, dateDeSortie, duree, synopsis, slogan, id_film, trailer));
                        oldId = myRes.getInt("f.id_film");
                    }
                }
            }


            ResultSet myRes = myStat.executeQuery("SELECT id_cine, nom, lien_image FROM cinema");
            while(myRes.next())
            {
                //System.out.println(myRes.getString("nom"));
                sceneController.getCinemaCollection().addCinema(new Cinema(myRes.getInt("id_cine"), myRes.getString("nom"), myRes.getString("lien_image")));
            }
            sceneController.getCinemaCollection().setImage();
            for(int i = 0 ; i < sceneController.getCinemaCollection().getSize(); i++)
            {
                ResultSet myRes3 = myStat.executeQuery("SELECT * FROM promotion WHERE id_cine = '" + sceneController.getCinemaCollection().getCinema(i).get_id_cine() + "';");
                while(myRes3.next())
                {
                    sceneController.getCinemaCollection().getCinema(i).add_promo(new Promo(myRes3.getInt("id_promo"), myRes3.getString("nom_promo"), myRes3.getDouble("discount"), myRes3.getInt("min_age"), myRes3.getInt("max_age")));
                }
            }
            for(int i = 0 ; i < sceneController.getCinemaCollection().getSize(); i++)
            {
                ResultSet myRes2 = myStat.executeQuery("SELECT salle.id_salle, capacite, num_salle FROM salle JOIN cinema_salle ON cinema_salle.id_salle = salle.id_salle WHERE cinema_salle.id_cine = '" + String.valueOf(sceneController.getCinemaCollection().getCinema(i).get_id_cine()) +"';");
                while(myRes2.next())
                {
                    sceneController.getCinemaCollection().getCinema(i).addSalles(new Salle(myRes2.getInt("salle.id_salle"), myRes2.getInt("num_salle"), myRes2.getInt("capacite")));
                }
            }
            for(int i = 0 ; i < sceneController.getCinemaCollection().getSize(); i++)
            {
                for(int j = 0 ; j < sceneController.getCinemaCollection().getCinema(i).getSalles().size();j++)
                {
                    ResultSet myRes3 = myStat.executeQuery("SELECT seance.id_seance, seance.id_film, seance.date_horraire, seance.prix, film.id_film, film.poster, film.nom_film, film.date_de_sortie, film.duree, film.synopsis, film.slogan, film.trailer, salle.num_salle, person.prenom, person.nom FROM seance JOIN film on film.id_film = seance.id_film JOIN salle ON salle.id_salle = seance.id_salle  LEFT JOIN realisateur ON realisateur.id_film = film.id_film LEFT JOIN person ON person.id_person = realisateur.id_person WHERE seance.id_cine = '" + String.valueOf(sceneController.getCinemaCollection().getCinema(i).get_id_cine()) + "' AND seance.id_salle = '"+ String.valueOf(sceneController.getCinemaCollection().getCinema(i).getSalles().get(j).get_id_bdd())+"' ORDER BY seance.id_seance ASC;");
                    int oldId = -1;
                    while(myRes3.next())
                    {
                        if(myRes3.getInt("seance.id_seance") != oldId)
                        {
                            String trailer;
                            if(myRes3.getString("film.trailer") != "" && myRes3.getString("film.trailer") != null)
                            {
                                trailer = myRes3.getString("film.trailer").split("=")[1];
                            }
                            else
                            {
                                trailer = null;
                            }
                            String realisateur = "";
                            if(myRes3.getString("person.prenom") != null && myRes3.getString("person.prenom") != "")
                            {
                                realisateur += myRes3.getString("person.prenom");
                            }
                            if(myRes3.getString("person.nom") != null && myRes3.getString("person.nom") != "")
                            {
                                if(!realisateur.equals(""))
                                {
                                    realisateur += " " + myRes3.getString("person.nom");
                                }
                                else
                                {
                                    realisateur += myRes3.getString("person.nom");
                                }
                            }
                            //System.out.println(myRes3.getString("film.nom_film"));
                            Movie movie = new Movie(myRes3.getString("film.nom_film"), realisateur, myRes3.getString("film.poster"), myRes3.getString("film.date_de_sortie"), myRes3.getString("film.date_de_sortie"), myRes3.getString("film.duree"), myRes3.getString("film.synopsis"), myRes3.getString("film.slogan"), myRes3.getString("film.id_film"),trailer);
                            sceneController.getCinemaCollection().getCinema(i).getSalles().get(j).addSeance(new Seance(myRes3.getString("film.nom_film"), movie, LocalDate.parse(myRes3.getString("seance.date_horraire").split(" ")[0]), myRes3.getString("seance.date_horraire").split(" ")[1], myRes3.getInt("salle.num_salle"), myRes3.getDouble("seance.prix"), myRes3.getInt("seance.id_seance")));
                            sceneController.getCinemaCollection().getCinema(i).ajoutFilm(movie);
                            oldId = myRes3.getInt("seance.id_seance");
                        }
                    }
                }
                sceneController.getCinemaCollection().getCinema(i).setImage();
            }

            if(user != null)
            {
                ResultSet myRes2 = myStat.executeQuery("SELECT DISTINCT film.id_film,nom_film, poster, date_de_sortie, duree, synopsis, slogan, trailer, person.prenom, person.nom FROM film JOIN liked ON film.id_film = liked.id_film LEFT JOIN realisateur ON realisateur.id_film = film.id_film LEFT JOIN person ON person.id_person = realisateur.id_person WHERE liked.id_user = '" + String.valueOf(user.get_id()) +"' ORDER BY film.id_film ASC;");
                int oldId = -1;
                while(myRes2.next())
                {
                    if(myRes2.getInt("film.id_film") != oldId)
                    {
                        String tempDatedesortie = myRes2.getString("date_de_sortie");
                        String trailer;
                        if(myRes2.getString("trailer") != "" && myRes2.getString("trailer") != null)
                        {
                            trailer = myRes2.getString("trailer").split("=")[1];
                        }
                        else
                        {
                            trailer = null;
                        }
                        String realisateur = "";
                        if(myRes2.getString("person.prenom") != null && myRes2.getString("person.prenom") != "")
                        {
                            realisateur += myRes2.getString("person.prenom");
                        }
                        if(myRes2.getString("person.nom") != null && myRes2.getString("person.nom") != "")
                        {
                            if(!realisateur.equals(""))
                            {
                                realisateur += " " + myRes2.getString("person.nom");
                            }
                            else
                            {
                                realisateur += myRes2.getString("person.nom");
                            }
                        }
                        user.ajouterLike(new Movie(myRes2.getString("nom_film"), realisateur, myRes2.getString("poster"), tempDatedesortie,tempDatedesortie,myRes2.getString("duree"), myRes2.getString("synopsis"), myRes2.getString("slogan"), myRes2.getString("id_film"), trailer));
                        oldId = myRes2.getInt("film.id_film");
                    }
                }
                ResultSet myRes5 = myStat.executeQuery("SELECT DISTINCT film.id_film,nom_film, poster, date_de_sortie, duree, synopsis, slogan, trailer, person.prenom, person.nom FROM film JOIN deja_vu ON film.id_film = deja_vu.id_film LEFT JOIN realisateur ON realisateur.id_film = film.id_film LEFT JOIN person ON person.id_person = realisateur.id_person WHERE deja_vu.id_user = '" + String.valueOf(user.get_id()) +"' ORDER BY film.id_film ASC;");
                oldId = -1;
                while(myRes5.next())
                {
                    if(myRes5.getInt("film.id_film") != oldId)
                    {
                        String tempDatedesortie = myRes5.getString("date_de_sortie");
                        String trailer;
                        if(myRes5.getString("trailer") != "" && myRes5.getString("trailer") != null)
                        {
                            trailer = myRes5.getString("trailer").split("=")[1];
                        }
                        else
                        {
                            trailer = null;
                        }
                        String realisateur = "";
                        if(myRes5.getString("person.prenom") != null && myRes5.getString("person.prenom") != "")
                        {
                            realisateur += myRes5.getString("person.prenom");
                        }
                        if(myRes5.getString("person.nom") != null && myRes5.getString("person.nom") != "")
                        {
                            if(!realisateur.equals(""))
                            {
                                realisateur += " " + myRes5.getString("person.nom");
                            }
                            else
                            {
                                realisateur += myRes5.getString("person.nom");
                            }
                        }
                        user.ajouterDejaVu(new Movie(myRes5.getString("nom_film"), realisateur, myRes5.getString("poster"), tempDatedesortie,tempDatedesortie,myRes5.getString("duree"), myRes5.getString("synopsis"), myRes5.getString("slogan"), myRes5.getString("id_film"), trailer));
                        oldId = myRes5.getInt("film.id_film");
                    }
                }
                user.set_image();
            }

        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }

        thread = new RunnableDemo("Mon thread");
        thread.setMainApp(this);

        sceneController.set_collection(collection);
        sceneController.setProfil(user);

        //initRootLayout();
        //showMainMenu();
    }
    /**
     * méthode permettant de rajouter un film dans la table deja_vu de la base de donnée afin de pouvoir accéder au film déjà vu par l'utilisateur
     * +---------+---------+------+-----+---------+-------+
     * | Field   | Type    | Null | Key | Default | Extra |
     * +---------+---------+------+-----+---------+-------+
     * | id_user | int(11) | NO   |     | NULL    |       |
     * | id_film | int(11) | NO   |     | NULL    |       |
     * +---------+---------+------+-----+---------+-------+
     * @param id_user id de l'utilisateur dans la base de donné
     * @param id_film id du film a ajouté
     */
    public void set_deja_vu(int id_user, int id_film)
    {
        try
        {
            myStat.executeUpdate("INSERT INTO deja_vu (id_user, id_film) SELECT * FROM (SELECT '" + String.valueOf(id_user) +"' AS id_user, '" + String.valueOf(id_film) +"' AS id_film) AS tmp WHERE NOT EXISTS ( SELECT id_user FROM deja_vu WHERE (id_user='" + String.valueOf(id_user) + "' AND id_film='" + String.valueOf(id_film) +"')) LIMIT 1;");    
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
    /**
     * Méthode permettant d'ajouter une promotion dans un cinéma
     * structure de la table:
     * +-----------+--------------+------+-----+---------+----------------+
     * | Field     | Type         | Null | Key | Default | Extra          |
     * +-----------+--------------+------+-----+---------+----------------+
     * | id_promo  | int(11)      | NO   | PRI | NULL    | auto_increment |
     * | id_cine   | int(11)      | NO   |     | NULL    |                |
     * | nom_promo | varchar(255) | YES  |     | NULL    |                |
     * | discount  | double       | NO   |     | NULL    |                |
     * | max_age   | int(11)      | YES  |     | NULL    |                |
     * | min_age   | int(11)      | YES  |     | NULL    |                |
     * +-----------+--------------+------+-----+---------+----------------+
     * @param id_cine id du cinéma dans la base de donnée
     * @param nom_promo nom de la promotion afficher pour l'interface administrateur
     * @param pourcentage pourcentage de promotion exprimé entre 0 et 1 pour faire un pourcentage
     * @param min_age age minimum pour acceder a la promotion
     * @param max_age age maximum pour acceder a la promotion
     * @return return l'id de la promotion dans la base de données afin de pouvoir la supprimer plus tard si l'administrateur le veut
     */
    public int add_promotion(int id_cine, String nom_promo, double pourcentage, int min_age, int max_age)
    {
        try
        {
            myStat.executeUpdate("INSERT INTO promotion (id_cine, nom_promo, discount, max_age, min_age) SELECT * FROM (SELECT '" + String.valueOf(id_cine) +"' AS id_cine, '" + nom_promo +"' AS nom_promo, '" + String.valueOf(pourcentage) + "' AS discount, '" + String.valueOf(max_age) + "' AS max_age, '" + String.valueOf(min_age) + "' AS min_age) AS tmp WHERE NOT EXISTS ( SELECT id_cine FROM promotion WHERE (id_cine='" + String.valueOf(id_cine) + "' AND nom_promo='" + nom_promo +"' AND discount = '" + String.valueOf(pourcentage) + "' AND max_age = '" + String.valueOf(max_age) + "' AND min_age = '" + String.valueOf(min_age) + "')) LIMIT 1;");
            ResultSet myRes = myStat.executeQuery("SELECT LAST_INSERT_ID();");
            while(myRes.next())
            {
                return(myRes.getInt("LAST_INSERT_ID()"));
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * méthode permettant de supprimer une promotion de la base de donnée
     * @param id_promo nécessite l'id de la promotion dans la base de données pour la suppresion
     */
    public void suppr_promotion(int id_promo)
    {
        try
        {
            myStat.executeUpdate("DELETE FROM promotion WHERE id_promo = '" + String.valueOf(id_promo) + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * permet d'ajouter un film liké par l'utilisateur a la base donnée.
     * La structure de la base de donnée pour les films likés est la suivante :
     * +---------+---------+------+-----+---------+-------+
     * | Field   | Type    | Null | Key | Default | Extra |
     * +---------+---------+------+-----+---------+-------+
     * | id_film | int(11) | NO   |     | NULL    |       |
     * | id_user | int(11) | NO   |     | NULL    |       |
     * +---------+---------+------+-----+---------+-------+
     * @param id_user id de l'utilisateur qui vient de liker le film
     * @param id_film id du film
     */
    public void add_like(int id_user, String id_film)
    {
        try
        {
            myStat.executeUpdate("INSERT INTO liked (id_user, id_film) SELECT * FROM (SELECT '" + String.valueOf(id_user) +"' AS id_user, '" + id_film +"' AS id_film) AS tmp WHERE NOT EXISTS ( SELECT id_user FROM liked WHERE (id_user='" + String.valueOf(id_user) + "' AND id_film='" + id_film +"')) LIMIT 1;");    
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * permet d'ajouter une reservation a la base de données pour un affiche plus tard des billets reservé par l'utilisateur mais aussi pour les statistiques de l'application
     * Structure de la table reservation:
     * +-----------+---------+------+-----+---------+-------+
     * | Field     | Type    | Null | Key | Default | Extra |
     * +-----------+---------+------+-----+---------+-------+
     * | id_user   | int(11) | NO   |     | NULL    |       |
     * | id_seance | int(11) | NO   |     | NULL    |       |
     * | tarif     | float   | YES  |     | NULL    |       |
     * +-----------+---------+------+-----+---------+-------+
     * @param id_user id de l'utilisateur dans la base de données
     * @param id_seance id de la seance reservé
     * @param tarif tarif de la seance en fonction des promotions appliqués par l'utilisateur
     */
    public void add_reservation(int id_user, int id_seance, double tarif)
    {
        try
        {
            myStat.executeUpdate("INSERT INTO reservation (id_user, id_seance, tarif) VALUES ('"+ String.valueOf(id_user) +"','" + String.valueOf(id_seance) + "','" + String.valueOf(tarif) + "');");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * récupere les genres de la base de données ainsi que leur id pour que l'utilisateur puisse liker ensuite les différents genre qu'il aime au moment de la création de son compte
     * structure de la base de donnée:
     * +----------+--------------+------+-----+---------+----------------+
     * | Field    | Type         | Null | Key | Default | Extra          |
     * +----------+--------------+------+-----+---------+----------------+
     * | id_genre | int(11)      | NO   | PRI | NULL    | auto_increment |
     * | nom      | varchar(100) | YES  |     | NULL    |                |
     * +----------+--------------+------+-----+---------+----------------+
     * @return une list de genre pour l'affichage dans le genreLiker
     */
    public ArrayList<Genre> get_genre_from_bdd()
    {
        ArrayList<Genre> genres = new ArrayList<Genre>();
        String tempnom;
        int id_genre;
        String tempLien = "";
        try
        {
            ResultSet myRes = myStat.executeQuery("SELECT nom, id_genre from genre");
            while(myRes.next())
            {
                tempnom = myRes.getString("nom");
                id_genre  = myRes.getInt("id_genre");
                ResultSet myRes2 = myStat2.executeQuery("SELECT poster FROM film JOIN film_genre ON film_genre.id_film = film.id_film JOIN genre ON genre.id_genre = film_genre.id_genre WHERE genre.id_genre = '" + String.valueOf(id_genre) + "' ORDER BY RAND() LIMIT 1;");
                while(myRes2.next())
                {
                    tempLien = myRes2.getString("poster");
                }
                genres.add(new Genre(tempnom,id_genre, tempLien));
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return genres;
    }

    public void threadStarter()
    {
        thread.start();
    }

    public MovieCollection[] getMovieCollection(int i)
    {
        return collection;
    }

    public ActorCollection getActorCollection()
    {
        return collectionActor;
    }

    public Profil getProfil()
    {
        return user;
    }

    public Stage get_stage()
    {
        return primaryStage;
    }

    /**
     * Méthode permettant de modifier n'importe quel champ d'un utilisateur, il s'agit de la version de la méthode pour les string (méthode pour les images surchargé)
     * structure de la base utilisateur:
     * +-------------------+--------------+------+-----+---------+----------------+
     * | Field             | Type         | Null | Key | Default | Extra          |
     * +-------------------+--------------+------+-----+---------+----------------+
     * | id_user           | int(11)      | NO   | PRI | NULL    | auto_increment |
     * | prenom            | varchar(200) | YES  |     | NULL    |                |
     * | nom               | varchar(200) | YES  |     | NULL    |                |
     * | date_de_naissance | date         | YES  |     | NULL    |                |
     * | genre             | varchar(50)  | YES  |     | NULL    |                |
     * | email             | varchar(255) | NO   | UNI | NULL    |                |
     * | pwd               | varchar(300) | NO   |     | NULL    |                |
     * | admin             | tinyint(1)   | YES  |     | NULL    |                |
     * | PP                | mediumblob   | YES  |     | NULL    |                |
     * +-------------------+--------------+------+-----+---------+----------------+
     * @param field champ de la base de données a modifier pour l'utilisateur 
     * @param value valeur du nouveau champ
     */
    public int modify_user(String field, String value)
    {
        try
        {
            if(field.equals("email"))
            {
                ResultSet myRes2 = myStat.executeQuery("SELECT email FROM utilisateur WHERE email = '" + value + "';");
                while(myRes2.next())
                {
                    if(myRes2.getString("email") != "")
                    {
                        return -1;
                    }
                }
            } 
            String query = "UPDATE utilisateur SET " + field + " = '"+ value + "' WHERE id_user = ' " + String.valueOf(user.get_id())+ "';";
            PreparedStatement pstmt = myConn.prepareStatement(query);
            pstmt.executeUpdate();
            ResultSet myRes = myStat.executeQuery("SELECT id_user, prenom, nom, date_de_naissance, genre, email, pp, admin FROM utilisateur where id_user = '" + user.get_id() + "'");
            while(myRes.next())
            {
                this.user.set_id(myRes.getInt("id_user"));
                this.user.set_prenom(myRes.getString("prenom"));
                this.user.set_nom(myRes.getString("nom"));
                this.user.set_mail(myRes.getString("email"));
                this.user.set_genre(myRes.getString("genre"));
                this.user.set_age(myRes.getString("date_de_naissance"));
                this.user.set_pp(myRes.getBinaryStream("pp"));
                
            }
                sceneController.setProfil(this.user);
                sceneController.updateProfil();
                return 1;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * Méthode permettant d'ajouter un genre liké par l'utilisateur dans la base de données dans la table utilisateur_genre cette table suit la structure suivante :
     * structure de la table utilisateur_genre:
     * +----------+---------+------+-----+---------+-------+
     * | Field    | Type    | Null | Key | Default | Extra |
     * +----------+---------+------+-----+---------+-------+
     * | id_user  | int(11) | NO   |     | NULL    |       |
     * | id_genre | int(11) | NO   |     | NULL    |       |
     * +----------+---------+------+-----+---------+-------+
     * @param genre id du genre dans la base de donnée 
     */
    public void genre_like(String genre)
    {
        try
        {
            myStat.executeUpdate("INSERT INTO utilisateur_genre (id_user, id_genre) SELECT * FROM (SELECT '" + tempUserId +"' AS id_user, '" + genre +"' AS id_genre) AS tmp WHERE NOT EXISTS ( SELECT id_genre FROM utilisateur_genre WHERE (id_user='" + tempUserId + "' AND id_genre='" + genre +"')) LIMIT 1;");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * surchage de la méthode permettant de changer un champs pour l'utilisateur, cette version prend en entré le champ ainsi que le flux en bit du fichier a mettre en BLOB dans la base de donnée
     * 
     * @param field champ a modifié
     * @param value flux de donnée
     */
    public void modify_user(String field, FileInputStream value)
    {
        try
        {
            String query = "UPDATE utilisateur SET " + field + " = ? WHERE id_user = '" + String.valueOf(user.get_id()) + "';";
            PreparedStatement pstmt = myConn.prepareStatement(query);
            pstmt.setBinaryStream(1, value);
            pstmt.executeUpdate();
            ResultSet myRes = myStat.executeQuery("SELECT id_user, prenom, nom, date_de_naissance, genre, email, pp, admin FROM utilisateur where id_user = '" + user.get_id() + "'");
            while(myRes.next())
            {
                this.user.set_id(myRes.getInt("id_user"));
                this.user.set_prenom(myRes.getString("prenom"));
                this.user.set_nom(myRes.getString("nom"));
                this.user.set_mail(myRes.getString("email"));
                this.user.set_genre(myRes.getString("genre"));
                this.user.set_age(myRes.getString("date_de_naissance"));
                this.user.set_pp(myRes.getBinaryStream("pp"));
            }
            sceneController.setProfil(this.user);
            sceneController.updateProfil();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Méthode permettant a la création du compte utilisateur de faire une demande pour l'administration pour la gestion de cinéma
     * @param id_user_bdd id de l'utilisateur dans la base de donnée
     */
    public void setDemandeAdmin(String id_user_bdd)
    {
        try
        {
            myStat.executeUpdate("INSERT INTO attente_admin (id_user) SELECT * FROM (SELECT '"+ id_user_bdd +"' AS id_user) AS tmp WHERE NOT EXISTS ( SELECT id_user FROM attente_admin WHERE (id_user = '" + id_user_bdd + "')) LIMIT 1;");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Permet de créer un compte utilisateur et nécessite l'ensemble des paramètres pour la table utilisateur, l'ensemble des paramètres sont nécessaire
     * structure de la table utilisateur:
     * +-------------------+--------------+------+-----+---------+----------------+
     * | Field             | Type         | Null | Key | Default | Extra          |
     * +-------------------+--------------+------+-----+---------+----------------+
     * | id_user           | int(11)      | NO   | PRI | NULL    | auto_increment |
     * | prenom            | varchar(200) | YES  |     | NULL    |                |
     * | nom               | varchar(200) | YES  |     | NULL    |                |
     * | date_de_naissance | date         | YES  |     | NULL    |                |
     * | genre             | varchar(50)  | YES  |     | NULL    |                |
     * | email             | varchar(255) | NO   | UNI | NULL    |                |
     * | pwd               | varchar(300) | NO   |     | NULL    |                |
     * | admin             | tinyint(1)   | YES  |     | NULL    |                |
     * | PP                | mediumblob   | YES  |     | NULL    |                |
     * +-------------------+--------------+------+-----+---------+----------------+
     * @param prenom prénom de l'utilisateur
     * @param nom nom de l'utilisateur
     * @param genre sexe de l'utilisateur
     * @param year année de naissance
     * @param month mois de naissance
     * @param day jour dans le mois de naissance
     * @param login adresse mail de l'utilisateur
     * @param mdp mot de passe en clair qui sera encrypté ensuite par un algorithme SHA-256
     * @param admin boolean qui indique si l'utilisateur fait une demande pour devenir administrateur sur l'application
     * @param filePath chemin du fichier contenant la photo de profil de l'utilisateur
     * @return return 1 si la création du compte c'est bien passée et -1 sinon
     */
    public int create_acct(String prenom, String nom, String genre, int year, int month, int day, String login, String mdp, boolean admin, String filePath)
    {
        String naissance = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
        try
        {
            String query = "INSERT INTO utilisateur (prenom, nom, date_de_naissance, genre, email, pwd, admin, pp) SELECT * FROM (SELECT '" + prenom +"' AS prenom, '" + nom + "' AS nom, '" + naissance + "' AS date_de_naissance, '" + genre + "' AS genre, '" + login + "' AS email, '" + DigestUtils.sha256Hex(mdp) + "' AS mdp, FALSE, ? AS pp) AS tmp WHERE NOT EXISTS (SELECT id_user FROM utilisateur WHERE email='" + login +"') LIMIT 1;";
            PreparedStatement pstmt = myConn.prepareStatement(query);
            FileInputStream ppfile = new FileInputStream(filePath);
            pstmt.setBinaryStream(1, ppfile);
            if(pstmt.executeUpdate() != 0)
            {
                ResultSet myRes = myStat.executeQuery("SELECT id_user FROM utilisateur WHERE email = '" + login + "';");
                while(myRes.next())
                {
                    tempUserId = myRes.getString("id_user");
                    if(tempUserId != "")
                    {
                        if(admin)
                        {
                            setDemandeAdmin(tempUserId);
                        }
                        return 1;
                    }
                    else
                    {
                        return -1;
                    }
                }
            }
            else
            {
                return -1;
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * Méthode permettant d'ajouter un cinéma dans la base de donnée:
     * Structure de la table cinema:
     * +------------+--------------+------+-----+---------+----------------+
     * | Field      | Type         | Null | Key | Default | Extra          |
     * +------------+--------------+------+-----+---------+----------------+
     * | id_cine    | int(11)      | NO   | PRI | NULL    | auto_increment |
     * | nom        | varchar(100) | YES  |     | NULL    |                |
     * | id_user    | int(11)      | YES  |     | NULL    |                |
     * | lien_image | varchar(255) | YES  |     | NULL    |                |
     * +------------+--------------+------+-----+---------+----------------+
     * @param nom nom du cinéma qui s'affichera pour l'utilsateur
     * @param id_admin id de l'admin qui va gerer ce cinema
     * @param lien_image lien vers la photo de profil du cinéma
     */
    public void insertCinema_into_bdd(String nom, int id_admin, String lien_image)
    {
        try
        {
            myStat.executeUpdate("INSERT INTO cinema (nom,id_user,lien_image) SELECT * FROM (SELECT '" + nom +"' AS nom, '" + id_admin + "' AS id_user, '" + lien_image + "' AS lien_image) AS tmp WHERE NOT EXISTS ( SELECT id_cine FROM cinema WHERE (nom = '"+ nom + "' AND id_user = '" + id_admin + "' AND lien_image = '" + lien_image + "')) LIMIT 1;");    
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Méthode permettant de supprimer un cinéma de la base de donnée
     * @param id_cine id du cinéma a supprimé
     */
    public void SupprimerCinemaBDD(int id_cine)
    {
        try
        {
            myStat.executeUpdate("DELETE FROM cinema WHERE id_cine = '" + id_cine + "';");
            myStat.executeUpdate("DELETE FROM salle JOIN cinema_salle ON cinema_salle.id_salle = salle.id_salle WHERE cinema_salle.id_cine = '" + id_cine + "';");
            myStat.executeUpdate("DELETE FROM cinema_salle WHERE id_cine = '" + id_cine + "';");
            myStat.executeUpdate("DELETE FROM seance WHERE id_cine = '" + id_cine + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Méthode permettant d'ajouter un film dans la base de donnée, la structure de la classe film est la suivante :
     * +----------------+--------------+------+-----+---------+----------------+
     * | Field          | Type         | Null | Key | Default | Extra          |
     * +----------------+--------------+------+-----+---------+----------------+
     * | id_film        | int(11)      | NO   | PRI | NULL    | auto_increment |
     * | poster         | varchar(250) | YES  |     | NULL    |                |
     * | nom_film       | varchar(200) | YES  |     | NULL    |                |
     * | date_de_sortie | date         | YES  |     | NULL    |                |
     * | duree          | varchar(30)  | YES  |     | NULL    |                |
     * | synopsis       | text         | YES  |     | NULL    |                |
     * | slogan         | varchar(255) | YES  |     | NULL    |                |
     * | trailer        | varchar(250) | YES  |     | NULL    |                |
     * +----------------+--------------+------+-----+---------+----------------+
     * @param lien_poster lien du poster car les images ne sont pas stocké en BLOB dans la table film
     * @param nom_film nom du film
     * @param date_de_sortie date de sortie du film en string dans le format YYYY-MM-DD
     * @param duree duree du film en string de la forme HHhmm
     * @param synopsis synopsis du film blindé pour éviter les ' dans le texte
     * @param slogan slogan du film
     * @param trailer lien du trailer pour youtube pour pouvoir le jouer plus tard dans l'application
     * @return retourne l'id du film dans la base de donnée après insertion
     */
    public int insertMovie_into_bdd(String lien_poster, String nom_film, String date_de_sortie, String duree, String synopsis, String slogan, String trailer)
    {
        try
        {
            synopsis = synopsis.replace("'","''");
            myStat.executeUpdate("INSERT INTO film (poster,nom_film,date_de_sortie,duree,synopsis,slogan,trailer) SELECT * FROM (SELECT '" + lien_poster +"' AS poster, '" + nom_film +"' AS nom_film, '"+ date_de_sortie +"' AS date_de_sortie, '" + duree + "' AS duree, '" + synopsis + "' AS synopsis, '" + slogan + "' AS slogan, '" + trailer + "' AS trailer) AS tmp WHERE NOT EXISTS ( SELECT id_film FROM film WHERE (nom_film='" + nom_film  + "' AND poster='" + lien_poster +"' AND date_de_sortie = '" + date_de_sortie + "' AND duree= '"+ duree +"' AND synopsis = '" + synopsis + "' AND slogan = '" + slogan + "' AND trailer = '" + trailer + "')) LIMIT 1;");    
            ResultSet myRes = myStat.executeQuery("SELECT LAST_INSERT_ID();");
            while(myRes.next())
            {
                return(myRes.getInt("LAST_INSERT_ID()"));
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * Méthode permettant d'ajouter une salle de cinéma dans un cinéma prédefinit, cette méthode est dédié a la partie admnistration de l'application
     * Structure de la table salle dans la base de donnée:
     * +-----------+---------+------+-----+---------+----------------+
     * | Field     | Type    | Null | Key | Default | Extra          |
     * +-----------+---------+------+-----+---------+----------------+
     * | id_salle  | int(11) | NO   | PRI | NULL    | auto_increment |
     * | capacite  | int(11) | NO   |     | NULL    |                |
     * | num_salle | int(11) | NO   |     | NULL    |                |
     * +-----------+---------+------+-----+---------+----------------+
     * @param capacite taille de la salle
     * @param num_salle le numero de la salle dans le cinema
     * @param id_cine_bdd l'id du cinema dans lequel on va rajouter la salle
     * @return l'id de la salle dans la base de donnée pour pouvoir supprimer la salle plus tard
     */
    public int AjouterSalleCinema_into_bdd(int capacite, int num_salle, int id_cine_bdd)
    {
        try
        {
            int id_salle = -1;
            myStat.executeUpdate("INSERT INTO salle (capacite, num_salle) VALUES ('"+ String.valueOf(capacite) +"','" + String.valueOf(num_salle) + "');");
            ResultSet myRes = myStat.executeQuery("SELECT LAST_INSERT_ID();");
            while(myRes.next())
            {
                id_salle = myRes.getInt("LAST_INSERT_ID()");
            }
            if(id_salle != -1)
            {
                myStat.executeUpdate("INSERT INTO cinema_salle (id_cine, id_salle) VALUES ('" + String.valueOf(id_cine_bdd) + "','" + String.valueOf(id_salle) + "');");
            }
            return id_salle;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * Méthode permettant d'assigner un cinema a un administrateur, cette méthode permet a un Admin fraichement "promus" de pouvoir choisir le cinéma qu'il veut gerer
     * Structure de la table cinema dans la base de donnée:
     * +------------+--------------+------+-----+---------+----------------+
     * | Field      | Type         | Null | Key | Default | Extra          |
     * +------------+--------------+------+-----+---------+----------------+
     * | id_cine    | int(11)      | NO   | PRI | NULL    | auto_increment |
     * | nom        | varchar(100) | YES  |     | NULL    |                |
     * | id_user    | int(11)      | YES  |     | NULL    |                |
     * | lien_image | varchar(255) | YES  |     | NULL    |                |
     * +------------+--------------+------+-----+---------+----------------+
     * @param id_user id de l'utilisateur pour pouvoir lui attribuer le cinéma
     * @param id_cine_bdd id du cinema dans la base de donnée
     */
    public void AssignCinema(int id_user, int id_cine_bdd)
    {
        try
        {
            myStat.executeUpdate("UPDATE cinema SET id_user = '" + String.valueOf(id_user) + "' WHERE id_cine = '" + String.valueOf(id_cine_bdd) + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Méthode permettant de supprimer une salle de la base de donnée
     * @param id_salle_bdd id de la salle a supprimer
     */
    public void SupprimerUneSalleBDD(int id_salle_bdd)
    {
        try
        {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            ResultSet myRes = myStat.executeQuery("SELECT id_seance FROM seance WHERE id_salle = '" + String.valueOf(id_salle_bdd) + "';");
            while(myRes.next())
            {
                temp.add(myRes.getInt("id_seance"));
            }
            for(int i = 0 ; i < temp.size();i++)
            {
                myStat.executeUpdate("DELETE FROM reservation WHERE id_seance = '" + String.valueOf(temp.get(i)) + "';");
            }
            myStat.executeUpdate("DELETE FROM seance WHERE id_salle = '" + String.valueOf(id_salle_bdd) + "';");
            myStat.executeUpdate("DELETE FROM salle WHERE id_salle = '" + String.valueOf(id_salle_bdd) + "';");
            myStat.executeUpdate("DELETE FROM cinema_salle WHERE id_salle = '" + String.valueOf(id_salle_bdd) + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
    
    /**
     * Méthode permettant de supprimer une seance dans la base de donnée
     * @param id_seance id de la seance à supprimer
     */
    public void SupprimerUnseSeanceBDD(int id_seance)
    {
        try
        {
            myStat.executeUpdate("DELETE FROM seance WHERE id_seance = '" + String.valueOf(id_seance) + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
    
    /**
     * Méthode permettant de changer le nom du cinéma dans la base de donnée ainsi que pour l'affichage plus tard dans l'application
     * @param id_cine_bdd id du cinema dont il faut changer le nom
     * @param nomCine nouveau nom pour le cinéma
     */
    public void changerNomCinema(int id_cine_bdd, String nomCine)
    {
        try
        {
            myStat.executeUpdate("UPDATE cinema SET nom = '" + nomCine + "' WHERE id_cine = '" + String.valueOf(id_cine_bdd) + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Méthode permettant de changer le lien de l'image du cinema dans la base de donnée
     * @param id_cine_bdd id du cinema a modifié
     * @param lienImage nouveau lien de l'image a afficher
     */
    public void changerLienImageCinema(int id_cine_bdd, String lienImage)
    {
        try
        {
            myStat.executeUpdate("UPDATE cinema SET lien_image = '" + lienImage + "' WHERE id_cine = '" + String.valueOf(id_cine_bdd) + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
    /**
     * Méthode permettant de créer une nouvelle Seance dans un cinéma
     * @param id_film id du film a projetter
     * @param id_cine_bdd id du cinema dans le quel le film sera projetté
     * @param id_salle_bdd id de la salle dans la base de donnée
     * @param dateSeance date de la seance
     * @param heureSeance horraire de la seance
     * @param prixSeance prix de la seance sans promotion
     * @return return l'id de la seance pour pouvoir la supprimer ensuite si l'admnistrateur le veux
     */
    public int CreateSeance_into_bdd(int id_film,int id_cine_bdd, int id_salle_bdd, String dateSeance,String heureSeance, double prixSeance)
    {
        try
        {
            String tempDateHorraire = "";
            tempDateHorraire += dateSeance;
            if(heureSeance.length() == 5 && Integer.valueOf(heureSeance.substring(0,2)) < 24 && Integer.valueOf(heureSeance.substring(0,2)) > 0 && Integer.valueOf(heureSeance.substring(3)) < 60 && Integer.valueOf(heureSeance.substring(3)) >= 0)
            {
                if(heureSeance.contains("h"))
                {
                    tempDateHorraire += " " + heureSeance.split("h")[0] + ":" + heureSeance.split("h")[1];
                }
                else if(heureSeance.contains(":"))
                {
                    tempDateHorraire += " " + heureSeance.split(":")[0] + ":" + heureSeance.split(":")[1];
                }
                myStat.executeUpdate("INSERT INTO seance (id_salle, id_film, id_cine, date_horraire, prix) SELECT * FROM (SELECT '"+ id_salle_bdd + "' AS id_salle, '" + id_film + "' AS id_film, '" + id_cine_bdd + "' AS id_cine, '" + tempDateHorraire + "' AS date_horraire, '"+ prixSeance +"' AS prix) AS tmp WHERE NOT EXISTS ( SELECT id_seance FROM seance WHERE (id_cine = '" + id_cine_bdd + "' AND id_film = '" + id_film + "' AND id_salle = '" + id_salle_bdd + "' AND date_horraire = '" + tempDateHorraire + "')) LIMIT 1;");
                return 0;
            }
            else
            {
                return -1;
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return 1;
    }

    /**
     * Méthode permettant de mettre un utilisateur admnistrateur dans la base de donnée
     * @param id_user id de l'utilisateur a mettre en admin
     */
    public void SetAdmin(int id_user)
    {
        try
        {
            myStat.executeUpdate("UPDATE utilisateur SET admin = '1' WHERE id_user = '" + String.valueOf(id_user) + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
    
    /**
     * Méthode permettant de supprimer un utilisateur de la liste d'attente de demande d'admnistrateur
     * @param id_user id de l'utilisateur a enlever de la file d'attente
     */
    public void removeWaitingAdmin(int id_user)
    {
        try
        {
            myStat.executeUpdate("DELETE FROM attente_admin WHERE id_user = '" + id_user + "';");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
    /**
     * Méthode permettant de récuperer la liste des profils des utilisteurs en attente de devenir admnistrateur
     * @return une arrayList de Profil
     */
    public ArrayList<Profil> getAttenteAdmin()
    {
        ArrayList<Profil> temp = new ArrayList<Profil>();
        try
        {
            ResultSet myRes = myStat.executeQuery("SELECT utilisateur.id_user, utilisateur.prenom, utilisateur.nom, utilisateur.date_de_naissance, utilisateur.genre, utilisateur.email FROM attente_admin JOIN utilisateur ON utilisateur.id_user = attente_admin.id_user;");
            while(myRes.next())
            {
                temp.add(new Profil(myRes.getInt("utilisateur.id_user"), myRes.getString("utilisateur.prenom"), myRes.getString("utilisateur.nom"), myRes.getString("utilisateur.email"), myRes.getString("utilisateur.genre"), myRes.getString("utilisateur.date_de_naissance"), null, false,null));
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return temp;
    }
    public ArrayList<Stats2> get_nombreReserv(int id_cinema)
    {
        ArrayList<Stats2> stats = new ArrayList<>();
        try
        {
            ResultSet myRes = myStat.executeQuery("SELECT COUNT(reservation.id_seance), film.nom_film FROM reservation JOIN seance ON reservation.id_seance = seance.id_seance JOIN cinema on cinema.id_cine = seance.id_cine JOIN film on film.id_film = seance.id_film WHERE cinema.id_cine = '" + String.valueOf(id_cinema) + "' GROUP BY reservation.id_seance ORDER BY COUNT(reservation.id_seance) DESC;");
            while(myRes.next())
            {
                stats.add(new Stats2(myRes.getString("film.nom_film"), myRes.getInt("COUNT(reservation.id_seance)")));
            }
            return stats;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Méthode appeler lors du login de l'utilisateur sur l'application
     * @param login adresse mail de l'utilisateur
     * @param mdp mot de passe de l'utilisateur en clair avant d'etre encrypté en SHA-256 de nouveau afin de comparer avec le resultat dans la base de données
     * @return 1 si le login a bien marché et -1 si il y'a eu un probleme (du type le login et le mot de passe ne sont pas bon)
     */
    public int login_acct(String login, String mdp)
    {
        try
        {
            ResultSet myRes = myStat.executeQuery("SELECT utilisateur.id_user, prenom, utilisateur.nom, date_de_naissance, genre, email, pp, admin, cinema.id_cine,cinema.nom, cinema.lien_image FROM utilisateur LEFT JOIN cinema ON cinema.id_user = utilisateur.id_user WHERE email='" + login +"'" + " AND pwd='" + DigestUtils.sha256Hex(mdp) +"'");
            while(myRes.next())
            {
                if(myRes.getString("utilisateur.id_user") != "")
                {
                    Cinema tempCine = null;
                    if(myRes.getString("cinema.nom") != null && myRes.getString("cinema.lien_image") != null && myRes.getString("cinema.id_cine") != null)
                    {
                        tempCine = new Cinema(myRes.getInt("cinema.id_cine"),myRes.getString("cinema.nom"), myRes.getString("cinema.lien_image"));
                        ResultSet myRes4 = myStat2.executeQuery("SELECT * FROM promotion WHERE id_cine = '" + tempCine.get_id_cine() + "';");
                        while(myRes4.next())
                        {
                            tempCine.add_promo(new Promo(myRes4.getInt("id_promo"), myRes4.getString("nom_promo"), myRes4.getDouble("discount"), myRes4.getInt("min_age"), myRes4.getInt("max_age")));
                        }
                        ResultSet myRes2 = myStat2.executeQuery("SELECT salle.id_salle, capacite, num_salle FROM salle JOIN cinema_salle ON cinema_salle.id_salle = salle.id_salle WHERE cinema_salle.id_cine = '" + String.valueOf(tempCine.get_id_cine()) +"';");
                        while(myRes2.next())
                        {
                            tempCine.addSalles(new Salle(myRes2.getInt("salle.id_salle"), myRes2.getInt("num_salle"), myRes2.getInt("capacite")));
                        }
                        for(int j = 0 ; j < tempCine.getSalles().size();j++)
                        {
                            ResultSet myRes3 = myStat2.executeQuery("SELECT DISTINCT seance.id_seance, seance.id_film, seance.date_horraire, seance.prix, film.id_film, film.poster, film.nom_film, film.date_de_sortie, film.duree, film.synopsis, film.slogan, film.trailer, salle.num_salle, person.prenom, person.nom FROM seance LEFT JOIN film on film.id_film = seance.id_film LEFT JOIN salle ON salle.id_salle = seance.id_salle LEFT JOIN realisateur ON realisateur.id_film = film.id_film LEFT JOIN person ON person.id_person = realisateur.id_person WHERE seance.id_cine = '" + String.valueOf(tempCine.get_id_cine()) + "' AND seance.id_salle = '"+ String.valueOf(tempCine.getSalles().get(j).get_id_bdd())+"' ORDER BY seance.id_seance ASC;");
                            int oldId = -1;
                            while(myRes3.next())
                            {
                                if(myRes3.getInt("seance.id_seance") != oldId)
                                {
                                    String trailer;
                                    if(myRes3.getString("film.trailer") != "" && myRes3.getString("film.trailer") != null)
                                    {
                                        trailer = myRes3.getString("film.trailer").split("=")[1];
                                    }
                                    else
                                    {
                                        trailer = null;
                                    }
                                    String realisateur = "";
                                    if(myRes3.getString("person.prenom") != null && myRes3.getString("person.prenom") != "")
                                    {
                                        realisateur += myRes3.getString("person.prenom");
                                    }
                                    if(myRes3.getString("person.nom") != null && myRes3.getString("person.nom") != "")
                                    {
                                        if(!realisateur.equals(""))
                                        {
                                            realisateur += " " + myRes3.getString("person.nom");
                                        }
                                        else
                                        {
                                            realisateur += myRes3.getString("person.nom");
                                        }
                                    }
                                    //System.out.println(myRes3.getString("film.nom_film"));
                                    Movie movie = new Movie(myRes3.getString("film.nom_film"), realisateur, myRes3.getString("film.poster"), myRes3.getString("film.date_de_sortie"), myRes3.getString("film.date_de_sortie"), myRes3.getString("film.duree"), myRes3.getString("film.synopsis"), myRes3.getString("film.slogan"), myRes3.getString("film.id_film"),trailer);
                                    tempCine.getSalles().get(j).addSeance(new Seance(myRes3.getString("film.nom_film"), movie, LocalDate.parse(myRes3.getString("seance.date_horraire").split(" ")[0]), myRes3.getString("seance.date_horraire").split(" ")[1], myRes3.getInt("salle.num_salle"), myRes3.getDouble("seance.prix"), myRes3.getInt("seance.id_seance")));
                                    tempCine.ajoutFilm(movie);
                                    oldId = myRes3.getInt("seance.id_seance");
                                }
                            }
                        }
                            tempCine.setImage();
                    }
                    this.user = new Profil(myRes.getInt("utilisateur.id_user"), myRes.getString("prenom"), myRes.getString("nom"), myRes.getString("email"), myRes.getString("genre"), myRes.getString("date_de_naissance"), myRes.getBinaryStream("pp"), myRes.getBoolean("admin"), tempCine);
                    ResultSet myRes7 = myStat2.executeQuery("SELECT film.poster, film.nom_film, seance.date_horraire, cinema.nom, COUNT(*) FROM reservation JOIN seance on reservation.id_seance = seance.id_seance JOIN film ON seance.id_film = film.id_film JOIN cinema ON cinema.id_cine = seance.id_cine WHERE reservation.id_user ='" + myRes.getInt("utilisateur.id_user") +"'GROUP BY reservation.id_user, reservation.id_seance;");
                    while(myRes7.next())
                    {
                        this.user.ajouterReservation(new Reservation(new Movie(myRes7.getString("film.nom_film"), myRes7.getString("film.poster")), myRes7.getString("seance.date_horraire").split(" ")[1],myRes7.getString("cinema.nom") , myRes7.getString("seance.date_horraire").split(" ")[0], myRes7.getInt("COUNT(*)")));
                        //this.user.getFilmRes().get(this.user.getFilmRes().size()-1).getMovie()
                        
                    }
                    //this.user.ajouterReservation(new Reservation());
                    //System.out.println(this.user.get_prenom());
                    sceneController.setProfil(this.user);
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return 0;
    }
    public static void main(String[] args) {
        launch();
    }
}