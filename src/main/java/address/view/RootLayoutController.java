package address.view;

import address.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;

public class RootLayoutController {
    private MainApp mainApp;

    // ustawienie mainApp
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    // tworzenie nowej pustej książki adresów
    @FXML
    private void handleNew() {
        mainApp.getPersonData().clear();
        mainApp.setPersonFilePath(null);
    }

    // otwieranie książki adresów z pliku
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadPersonDataFromFile(file);
        }
    }

    // zapisywanie obecnie otwartej książki adresów
    @FXML
    private void handleSave() {
        File personFile = mainApp.getPersonFilePath();

        if (personFile != null) {
            mainApp.savePersonDataToFile(personFile);
        } else {
            handleSaveAs();
        }
    }

    // zapisywanie książki adresów jako nowy plik
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }

            mainApp.savePersonDataToFile(file);
        }
    }

    // otwieranie okna ze statystykami
    @FXML
    private void handleShowStatistics() {
        mainApp.showBirthdayStatistics();
    }

    // otwieranie okna z informacjami o aplikacji
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("AddressApp");
        alert.setHeaderText("About");
        alert.setContentText("Author: ");

        alert.showAndWait();
    }

    // zamykanie aplikacji
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}
