package ba.unsa.etf.rpr.tutorijal08;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class NoviController {
    public TextField imeField;
    public TextField prezimeField;
    public TextField adresaField;
    public TextField gradField;
    public TextField brojField;

    public SimpleStringProperty ime;
    public SimpleStringProperty prezime;
    public SimpleStringProperty adresa;
    public SimpleStringProperty grad;
    public SimpleStringProperty postanskiBroj;

    ValidatorPostanskogBroja validator;

    public NoviController() {
        ime = new SimpleStringProperty("");
        prezime = new SimpleStringProperty("");
        adresa = new SimpleStringProperty("");
        grad = new SimpleStringProperty("");
        postanskiBroj = new SimpleStringProperty("");
        validator = new ValidatorPostanskogBroja("");
    }

    @FXML
    public void initialize() {
        imeField.textProperty().bindBidirectional(ime); //sa bindBidirectional sinhronizujemo vrijednosti tako da ako se ijedan property vrijednosti mijenja automatski se mijenja i drugi property
        prezimeField.textProperty().bindBidirectional(prezime);
        adresaField.textProperty().bindBidirectional(adresa);
        gradField.textProperty().bindBidirectional(grad);
        brojField.textProperty().bindBidirectional(postanskiBroj);

        addListeners();
    }

    private void addListeners() {
        brojField.focusedProperty().addListener(new ChangeListener<Boolean>() { //ovako izvrsavamo focus property na TextField..u prevodu validacija se vrsi kad polje izgubi fokus(klikne na drugi textfield)
                                                                                //moze se dodatno pogledat na Stack Overflowu
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) { //observable value omogucava da posmatra promjene nad promjenjivom
                if (aBoolean && !t1) {
                    validator.setBroj(brojField.getText());
                    //koristiomo Task kako bi omogucili pozadinsku nit...obavezno moramo override metodu call()
                    Task<Boolean> task = new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            return validator.provjeriPostanskiBroj(brojField.getText());
                        }
                    };

                    task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent workerStateEvent) {

                            Boolean value = task.getValue(); //getValue vraca trenutnu vrijednost od ObeservableValue, a kod nas je tipa boolean pa vraca true/false
                            if (value) {
                                brojField.getStyleClass().removeAll("poljeNijeIspravno");
                                brojField.getStyleClass().add("poljeIspravno"); //oboji zeleno
                            } else {
                                brojField.getStyleClass().removeAll("poljeIspravno");
                                brojField.getStyleClass().add("poljeNijeIspravno"); //oboji crveno
                            }
                        }
                    });

                    new Thread(task).start(); //Kreiranje pozadinske niti da korisnicki interfejs ne bi blokirao

                }
            }
        });

    }
}
