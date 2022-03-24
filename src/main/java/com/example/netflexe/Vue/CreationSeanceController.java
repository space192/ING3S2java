package com.example.netflexe.Vue;

import com.example.netflexe.Model.Cinema;
import com.example.netflexe.Model.Movie;
import com.example.netflexe.Model.Profil;
import com.example.netflexe.Model.Seance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.time.LocalDate;
import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;

public class CreationSeanceController
{
    @FXML
    private Label Title;
    @FXML
    private ImageView Poster;
    @FXML
    private Label DateDeSortie;
    @FXML
    private Label Duree;
    @FXML
    private Label Slogan;
    @FXML
    private Label nomCine;
    @FXML
    private ChoiceBox<String> choixSalle;
    @FXML
    private DatePicker dateSeance;
    @FXML
    private TextField heureSeance;
    @FXML
    private TextField prix;

    private SceneController mainApp;
    private Movie movieS;
    private Cinema monCinema;

    public void retourMenu()
    {
        mainApp.showAccueilAdmin();
    }

    public void setMovie(Movie movie)
    {
        movieS = movie;
        //System.out.print(movie.getTitle());
        Title.setText(movie.getTitle());
        Poster.setImage(movie.getImage());
        DateDeSortie.setText(movie.getDate_de_sortie_S());
        Duree.setText(movie.getDuree());
        Slogan.setText(movie.getSlogan());
        movieS = movie;
    }


    public void setCinema(Cinema cinema)
    {
        monCinema = cinema;
        nomCine.setText(cinema.getName());
        for (var salle : cinema.getSalles())
        {
            choixSalle.getItems().add("Salle " + salle.getNumero());
        }
    }

    public void confirmerBoutonClick()
    {
        if ((!isNullOrWhiteSpace(prix.getText())) && (!isNullOrWhiteSpace(heureSeance.getText())) && (!Objects.equals(choixSalle.getValue(), null)))
        {
            double prixFinal = 0;
            boolean bonPrix = false;
            try
            {
                prixFinal = Double.parseDouble(prix.getText());
                if (prixFinal <= 0)
                    throw new NumberFormatException("Erreur : le prix doit être positif");
                else bonPrix = true;
            } catch (NumberFormatException ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                alert.show();
            }
            if (bonPrix)
            {
                LocalDate dateDeSeance = dateSeance.getValue();
                if (dateDeSeance == null)
                {
                    dateDeSeance = LocalDate.now();
                }
                int numSalle = Integer.parseInt(choixSalle.getValue().split(" ")[1]);
                Seance seance = new Seance(movieS.getTitle(), movieS, dateDeSeance, heureSeance.getText(), numSalle, prixFinal);
                monCinema.addSeance(numSalle, seance);
                mainApp.setCinemaAdmin(monCinema);
                mainApp.showAccueilAdmin();
            }
        } else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez renseigner tous les champs.", ButtonType.OK);
            alert.show();
        }
    }

    public void setMainApp(SceneController mainApp)
    {
        this.mainApp = mainApp;
    }

    private static boolean isNullOrWhiteSpace(String s)
    {
        if (isNullOrEmpty(s))
        {
            return true;
        } else
        {
            int length = s.length();
            if (length > 0)
            {
                for (int i = 0; i < length; i++)
                {
                    if (!Character.isWhitespace(s.charAt(i)))
                    {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }
}