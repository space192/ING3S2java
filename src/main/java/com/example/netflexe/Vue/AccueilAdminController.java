package com.example.netflexe.Vue;

import com.example.netflexe.Model.Cinema;
import com.example.netflexe.Model.Movie;
import com.example.netflexe.Model.MovieCollection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AccueilAdminController
{
    @FXML
    private Label nomCinema;
    @FXML
    private ListView<String> listView1 = new ListView<>();
    @FXML
    private ListView<String> listView2;
    @FXML
    private Button ajoutFilmBoutton;

    private Cinema cinema;
    private SceneController mainApp;
    private MovieCollection collection;
    private ContextMenu contextMenu = new ContextMenu();

    public void init(Cinema c)
    {
        ajoutFilmBoutton.setTooltip(new Tooltip("Cliquez ici pour ajouter un film."));
        cinema = c;
        nomCinema.setText(cinema.getName());
        collection = c.getFilmP();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < collection.getSize(); i++)
        {
            items.add(collection.getName(i));
        }
        listView1.setItems(items);
        listView1.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        listView1.setCellFactory(param -> new ListCell<String>()
        {
            private final ImageView imageView = new ImageView();

            @Override
            public void updateItem(String name, boolean empty)
            {
                super.updateItem(name, empty);
                if (empty)
                {
                    setText(null);
                    setGraphic(null);
                } else
                {
                    String tempName = "";
                    imageView.setImage(collection.getImage(name));
                    imageView.setFitHeight(160);
                    imageView.setFitWidth(110);
                    setText(null);
                    if (name.length() > 15)
                    {
                        tempName = (name.substring(0, 12) + "...");
                    } else
                    {
                        tempName = name;
                    }
                    VBox myBox = new VBox(imageView, new Label(tempName));
                    myBox.setAlignment(Pos.BASELINE_CENTER);
                    setGraphic(myBox);
                }
            }
        });

        MenuItem suppr = new MenuItem("Supprimer le film");
        suppr.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                String selectedName = listView1.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce film ?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(rs -> {
                    if(rs == ButtonType.YES)
                    {
                        if (collection.deleteMovie(selectedName))
                            //mainApp.showAccueilAdmin();
                            System.out.println("Supprimé : " + selectedName);
                        else
                        {
                            Alert alert2 = new Alert(Alert.AlertType.ERROR, "Le film n'a pas pu être supprimé.");
                            alert2.show();
                        }
                    }
                });
            }
        });
        contextMenu.getItems().add(suppr);

        listView1.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2  ) {
                String selectedName = listView1.getSelectionModel().getSelectedItem();

                Movie movie = collection.getMovie(selectedName);

                mainApp.showInfo(movie);
            }
            else if (event.getButton() == MouseButton.SECONDARY)
            {
                contextMenu.show(mainApp.getScene().getWindow(), event.getScreenX(), event.getScreenY());
            }
        });
        
        listView1.setOrientation(Orientation.HORIZONTAL);
    }

    public void setMainApp(SceneController mainApp)
    {
        this.mainApp = mainApp;
    }
}
