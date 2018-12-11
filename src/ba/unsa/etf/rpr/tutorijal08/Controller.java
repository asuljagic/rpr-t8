package ba.unsa.etf.rpr.tutorijal08;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Controller {
    public TextField traziStringPolje;
    public SimpleStringProperty traziString;
    public ListView listaDatotekaField;
    public SimpleListProperty<String> listaDatoteka;
    public Button traziBtn;
    public Button prekiniBtn;
    private Thread nit1, nit2;
    private List<String> rezultat;
    public boolean prekid = false;

    private Thread backgroundWorker;


    public Controller() {
        traziString = new SimpleStringProperty("");
        listaDatoteka = new SimpleListProperty<String>();
        rezultat = Collections.synchronizedList(new ArrayList<String>());
    }

    @FXML
    public void initialize() {
        traziStringPolje.textProperty().bindBidirectional(traziString);
        listaDatotekaField.itemsProperty().bindBidirectional(listaDatoteka);
        listaDatoteka.set(FXCollections.observableArrayList(rezultat));
        prekiniBtn.setDisable(true);
        prekid = false;

        listaDatotekaField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(listaDatotekaField.getSelectionModel()
                        .getSelectedItem());
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NoviSample.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.DECORATED);
                    stage.setTitle("Podaci o korisniku");
                    stage.setScene(new Scene(root1, 400, 300));
                    stage.show();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void pokreniPretragu(ActionEvent actionEvent) {
        initialize();
        Runnable r1 = () -> {  //Runnable koristimo kad god upotrebljavamo threadove
            traziBtn.setDisable(true);
            traziStringPolje.setDisable(true);
            prekiniBtn.setDisable(false);
            dajFajloveKojiSePodudaraju(new File(System.getProperty("user.home")));
        };

        nit1 = new Thread(r1);
        nit1.start();

    }

    public void dajFajloveKojiSePodudaraju(File dir) {
        if (prekid) return;
        try {
            File[] files = dir.listFiles();
            if (files == null) return;
            for (File file : files) {
                if (file.isDirectory()) {
                    //rekurzivno dobavljanje file-ova
                    dajFajloveKojiSePodudaraju(file);
                } else {
                    if (file.getCanonicalPath().toLowerCase().contains(traziStringPolje.getText().toLowerCase())) {
                        String result = file.getCanonicalPath(); //getCanonicalPath automatski rjesava se imena koja pocinju ./ ili ../ odnosno daje jedinstveni apsolute path do fajla
                        Platform.runLater(() -> {
                            //Platform.runLater koristimo za azuriranje GUIa(u nasem slucaju fajlova) i nikad se ne smije upotrijebiti za duze operacije jer dovodi do freezinga aplikacija
                            //za slucaj duzih operacija se koristi Task
                            listaDatotekaField.getItems().add(result);
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); //ako se baci izuzetak dozvaljava nam da vidimo koja metoda ima bug i zasto je uzrokavala problem
        }
    }

    public void prekiniPretragu(ActionEvent actionEvent) {
        if (nit1!= null || nit2!=null) {
            prekid = true;
            traziBtn.setDisable(false);
            traziStringPolje.setDisable(false);
            prekiniBtn.setDisable(true);
        }
    }
}