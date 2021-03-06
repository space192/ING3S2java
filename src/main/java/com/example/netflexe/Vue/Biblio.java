package com.example.netflexe.Vue;

import com.example.netflexe.Controller.SceneController;
import com.example.netflexe.Model.CinemaCollection;
import com.example.netflexe.Model.MovieCollection;
import com.example.netflexe.Model.Profil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.example.netflexe.Model.Movie;
import java.time.LocalDate;


public class Biblio {

    @FXML
    private ListView<String> listView1;
    @FXML
    private ListView<String> listView2;
    @FXML
    private ListView<String> listView3;
    @FXML
    private ListView<String> listView4;
    @FXML
    private ListView<String> listView5;
    @FXML
    private Label mainTitle;


    private SceneController mainApp;


    private MovieCollection collection[] = {new MovieCollection(), new MovieCollection(), new MovieCollection(), new MovieCollection()};
    private CinemaCollection collectionC = new CinemaCollection();
    //private MovieCollection collectionAvenir = new MovieCollection();
    private LocalDate dateAJD = LocalDate.now();


    @FXML
    /**
     * méthode appelée à linitialisation
     */
    private void initialize() {

        mainTitle.setText("Bibliothèque");
    }

    /**
     * permet de set les attributs des diéfferentes list views
     * @param monProfil permet de récupérer les genres likés par l'utilisateur
     */
    public void initializeBis(Profil monProfil)
    {
        LocalDate dateTemp;
        collection[0] = monProfil.getFilmLike();
        for(int i = 0; i<collection[0].getSize();i++)
        {
            dateTemp = collection[0].getMovie(i).getDate_de_sortie_LD();
            if(dateTemp.isAfter(dateAJD))
            {
                collection[1].addMovie(collection[0].getMovie(i));
            }
        }

        initialiseListView(listView1, 0);
        if(collection[1].getSize() != 0)
        {
            initialiseListView(listView2, 1);
        }

        for(int j = 0; j < collection[0].getSize(); j++)
        {
            for (int i = 0; i < collectionC.getSize(); i++) {
                if(collectionC.getCinema(i).checkMovie(collection[0].getMovie(j).getTitle()))
                {
                    if(!collection[2].checkBoolean(collection[0].getMovie(j).getTitle()))
                    {
                        collection[2].addMovie(collection[0].getMovie(j));
                    }

                }
            }
        }
        initialiseListView(listView3, 2);

        collection[3] = monProfil.getFilmDejaVu();
        initialiseListView(listView4, 3);
    }

    /**
     * permet de rajouter des films dans les list views, en fonction d'une collection de films
     * @param listView1 liste de films
     * @param k indice de la collection de film
     */
    private void initialiseListView(ListView<String> listView1, int k)
    {
        ObservableList<String> items = FXCollections.observableArrayList ();
        for(int i = 0 ; i < collection[k].getSize(); i++)
        {
            items.add(collection[k].getName(i)) ;
        }
        listView1.setItems(items);
        listView1.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        listView1.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(String name, boolean empty) {

                super.updateItem(name, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String tempName = "";
                    imageView.setImage(collection[k].getImage(name));
                    imageView.setFitHeight(173);
                    imageView.setFitWidth(118);
                    setText(null);
                    if(name.length() > 15)
                    {
                        tempName = (name.substring(0,12) + "...");
                    }
                    else{
                        tempName = name;
                    }
                    VBox myBox = new VBox(imageView,new Label(tempName));

                    myBox.setAlignment(Pos.BASELINE_CENTER);
                    setGraphic(myBox);
                }
            }
        });

        listView1.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2  ) {
                String selectedName = listView1.getSelectionModel().getSelectedItem();

                Movie movie = collection[k].getMovie(selectedName);

                mainApp.showInfo(movie,null,false);
            }
        });

        listView1.setOrientation(Orientation.HORIZONTAL);
    }

    /**
     * référence à l application principale
     * @param mainApp
     */
    public void setMainApp(SceneController mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * fait une référence à l'ensemble des cinémas
     * @param collection
     */

    public void setCinemaC(CinemaCollection collection)
    {
        this.collectionC = collection;
    }

    /**
     * permet d'update les films présent dans la bibliothèque
     * @param monProfil profil de l'utilisateur dont on va observer la collection de film
     */
    public void updateBiblio(Profil monProfil)
    {
        LocalDate dateTemp;
        collection[0] = monProfil.getFilmLike();
        for(int i = 0; i<collection[0].getSize();i++)
        {
            dateTemp = collection[0].getMovie(i).getDate_de_sortie_LD();
            if(dateTemp.isAfter(dateAJD))
            {
                if(!collection[1].checkContain(collection[0].getMovie(i)))
                {
                    collection[1].addMovie(collection[0].getMovie(i));
                }
            }
        }
        initialiseListView(listView1, 0);
        if(collection[1].getSize() != 0)
        {
            initialiseListView(listView2, 1);
        }

        for(int j = 0; j < collection[0].getSize(); j++)
        {
            for (int i = 0; i < collectionC.getSize(); i++) {
                if(collectionC.getCinema(i).checkMovie(collection[0].getMovie(j).getTitle()))
                {
                    if(!collection[2].checkBoolean(collection[0].getMovie(j).getTitle()))
                    {
                        if(!collection[2].checkContain(collection[0].getMovie(j)))
                        {
                            collection[2].addMovie(collection[0].getMovie(j));
                        }
                    }

                }
            }
        }
        initialiseListView(listView3, 2);

        collection[3] = monProfil.getFilmDejaVu();
        initialiseListView(listView4, 3);
    }

}







