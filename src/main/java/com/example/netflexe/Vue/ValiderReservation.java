package com.example.netflexe.Vue;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import com.example.netflexe.Controller.SceneController;
import com.example.netflexe.Model.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.time.LocalDate;


public class ValiderReservation {

    @FXML
    private ImageView image;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ChoiceBox<String> horaireBox;

    @FXML
    private ChoiceBox<Promo> promoChoiceBox;

    @FXML
    private Label prixLabel;

    @FXML
    private Label prixFLabel;

    @FXML
    private TextField mailInput;

    @FXML
    private TextField cardInput;

    private SceneController mainApp;
    private ArrayList<Seance> seances = new ArrayList<Seance>();
    private Seance seanceS = null;
    private double prixFinal = 0;
    private Profil profil;
    private Movie movieS;
    private LocalDate dateS;
    private String cinemaName;
    private Cinema cinema;
    private String horaireS;
    private String promo;
    private ArrayList<Promo> promos = new ArrayList<Promo>();


    @FXML
    private void initialize() {



    }

    /**
     * On va charger les informations disponibles pour les différentes séances
     * @param movie film que l'on souhaite réserver
     * @param cinema cinéma dans lequel on va réserver la séance
     */
    public void initializeBis(Movie movie, Cinema cinema)
    {
        this.cinema = cinema;
        movieS = movie;
        cinemaName = cinema.getName();
        image.setImage(movie.getImage());
        this.promos = cinema.get_promos();
        //seances = cinema.getAllSeances();
        /* A MODIF */
        seances.clear();
        for(var elem:cinema.getSalles())
        {
            seances.addAll(elem.getSeances());
        }


        ArrayList<String> horaires = new ArrayList<>();
        ArrayList<String> promotion = new ArrayList<>();
        int age = 0;

        promoChoiceBox.getItems().clear();
        promotion.clear();
        promoChoiceBox.valueProperty().set(null);
        horaireBox.getItems().clear();
        horaireBox.valueProperty().set(null);
        datePicker.getEditor().clear();
        datePicker.setValue(null);
        prixLabel.setText("");
        prixFLabel.setText("");
        if(!promotion.contains("Pas de promotion"))
        {
            promotion.add("Pas de promotion");
        }
        promoChoiceBox.getItems().add(new Promo(1,"Pas de promotion",1));
        //promos.add(new Promo(2,"Promotion senior",0.7, 65, 100));
        //promos.add(new Promo(3,"Promotion jeune",0.8, 0, 25));

        promoChoiceBox.setVisible(false);

        if(profil != null) {
            LocalDate date1 = LocalDate.parse(profil.get_age());
            LocalDate date2 = LocalDate.now();
            age = date2.getYear() - date1.getYear();
            for(int i = 0 ; i < this.promos.size() ;i++)
            {
                if(age >= this.promos.get(i).get_minAge() && age < this.promos.get(i).get_maxAge())
                {
                    promoChoiceBox.getItems().add(this.promos.get(i));
                }
            }
            mailInput.setText(profil.get_mail());
        }
        //promoChoiceBox.setItems(FXCollections.observableArrayList(promotion));
        


        horaireBox.setBackground(new Background(new BackgroundFill(Color.rgb(29,29,31), CornerRadii.EMPTY, Insets.EMPTY)));
        horaireBox.setStyle("-fx-background-color: #1d1d1d; -fx-border-color: #3f3f3f; -fx-border-width: 5px;");
        promoChoiceBox.setBackground(new Background(new BackgroundFill(Color.rgb(29,29,31), CornerRadii.EMPTY, Insets.EMPTY)));
        promoChoiceBox.setStyle("-fx-background-color: #1d1d1d; -fx-border-color: #3f3f3f; -fx-border-width: 5px;");


        //datePicker.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(true);
                setStyle("-fx-background-color: #ffc0cb;");
                for (Seance seance : seances) {
                    if (seance.getDate().toString().equals(date.toString()) && seance.getMovie().getTitle().equals(movieS.getTitle())) {
                        setDisable(false);
                        setStyle("-fx-background-color: #cbc0ff;");
                    }
                }
            }
        });


        datePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            horaires.clear();
            dateS = newValue;
            for (Seance seance : seances) {
                //System.out.println(seance.getName());
                //System.out.println(movie.getTitle());
                //System.out.println(seance.getDate().toString());
                //System.out.println(newValue.toString());
                if((newValue != null))
                { 
                    if ((seance.getName().equals(movie.getTitle())) && (seance.getDate().toString().equals(newValue.toString()))) {

                        horaires.add(seance.getHeure());
                    }
                }
            }
            horaireBox.setItems(FXCollections.observableArrayList(horaires));
        });

        horaireBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            horaireS = newValue;
            for (Seance seance : seances) {
                if(newValue != null)
                {
                    if ((seance.getDate().toString().equals(datePicker.valueProperty().getValue().toString())) && (seance.getHeure().equals(newValue.toString()))) {
                        seanceS = seance;
                        System.out.println(seanceS.getPrix());
                        prixLabel.setText((String.valueOf(seanceS.getPrix())));
                        promoChoiceBox.setVisible(true);
                    }
                }
                
            }
        });

        promoChoiceBox.valueProperty().addListener((ov, oldValue, newValue) -> {

            //System.out.println(newValue.toString());
            /*switch (newValue.toString()) {
                case "Pas de promotion" -> prixFinal = seanceS.getPrix();
                case "Promotion jeune" -> prixFinal = (seanceS.getPrix() * (0.8));
                case "Promotion senior" -> prixFinal = (seanceS.getPrix() * (0.7));
            }*/
            if(newValue != null)
            {
                System.out.println(newValue.get_pourcentage());
                prixFinal = seanceS.getPrix() * newValue.get_pourcentage();
                prixFLabel.setText((String.valueOf(prixFinal)));
            }
        });
    }

    public void setMainApp(SceneController mainApp) {
        this.mainApp = mainApp;
    }

    public void setProfil(Profil profil)
    {
        this.profil = profil;
    }

    /**
     * est appelée lorsque la séance est réservée
     */
    @FXML
    public void reserver()
    {


        if(!mailInput.getText().contains("@") && !mailInput.getText().contains(".com"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Adresse mail non valide", ButtonType.OK);
            alert.show();
        }
        else if(!(cardInput.getText().length() == 16))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Carte bancaire non valide", ButtonType.OK);
            alert.show();
        }
        else
        {
            Mail mail = new Mail();
            mail.sendMail(new Reservation(movieS,horaireS,cinemaName,dateS.toString(),1), mailInput.getText());
            if(profil != null)
            {
                profil.ajouterReservation(new Reservation(movieS,horaireS,cinemaName,dateS.toString(),1));
                mainApp.showBiblioRes(profil);
                this.cinema.updateStatsPromo(promo);
                this.cinema.updateStatsFilm(movieS.getTitle());
                this.mainApp.getHello().add_reservation(this.profil.get_id(),seanceS.get_idSeance(),prixFinal);
            }
            else
            {
                mainApp.showMainMenu();
            }
        }


    }
}
