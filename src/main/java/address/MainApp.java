package address;

import address.model.Person;
import address.model.PersonListWrapper;
import address.view.BirthdayStatisticsController;
import address.view.PersonEditDialogController;
import address.view.PersonOverviewController;
import address.view.RootLayoutController;
import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.prefs.Preferences;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<Person> personData = FXCollections.observableArrayList();

    public MainApp() {
        personData.add(new Person("Jan", "Nowak", "Lipowa", 22400, "Lublin", LocalDate.of(1990, 10, 5)));
        personData.add(new Person("Piotr", "Kowalski", "Lipowa", 22400, "Lublin", LocalDate.of(1991, 12, 1)));
        personData.add(new Person("Paweł", "Pawłowski", "Lipowa", 22400, "Lublin", LocalDate.of(1988, 5, 5)));
        personData.add(new Person("Michał", "Zieliński", "Lipowa", 22400, "Lublin", LocalDate.of(1956, 1, 3)));
        personData.add(new Person("Tadeusz", "Pawlak", "Lipowa", 22400, "Lublin", LocalDate.of(1921, 3, 15)));
        personData.add(new Person("Brajan", "Silva", "Lipowa", 22400, "Lublin", LocalDate.of(1999, 7, 25)));
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");

        // ustawienie ikony aplikacji
        this.primaryStage.getIcons().add(new Image("file:src/main/resources/images/addressapp_icon.png"));


        // inicjalizacja głównego widoku
        initRootLayout();
        // wyświetlenie widoku z podglądem osób
        showPersonOverview();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initRootLayout() {
        try {
            // ładowanie głównego widoku aplikacji
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // ustawianie głównego widoku aplikacji na głównej scenie (stage)
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // ustawienie MainApp dla kontrolera, danie mu dostępu do niego
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void showPersonOverview() {
        try {
            // ładowanie widoku z podglądem osób
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = loader.load();

            // ustawienie widoku w środku widoku głównego
            rootLayout.setCenter(personOverview);

            // ustawienie MainApp dla kontrolera, danie mu dostępu do niego
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // otwieranie okna edycji osoby
    public boolean showPersonEditDialog(Person person) {
        try {
            // ładowanie widoku z edycją osoby
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane page = loader.load();

            // utworzenie okna edycji
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // ustawienie odpowiedniej osoby do edycji dla kontrolera
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // wyświetlenie okna edycji
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    // otwieranie okna ze statystykami
    public void showBirthdayStatistics() {
        try {
            // ładowanie widoku ze statystykami
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/BirthdayStatistics.fxml"));
            AnchorPane page = loader.load();

            // utworzenie okna ze statystykami
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Birthday Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // ustawienie danych o osobach potrzebnych do statystyk dla kontrolera
            BirthdayStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return  primaryStage;
    }

    public ObservableList<Person> getPersonData() {
        return personData;
    }

    // preferencje użytkownika, zwraca osttanio otwarty plik
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);

        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);

        if (file != null) {
            prefs.put("filePath", file.getPath());

            primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            primaryStage.setTitle("AddressApp");
        }
    }

    // załadowanie danych o osobach z pliku XML
    public void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

            personData.clear();
            personData.addAll(wrapper.getPersons());

            setPersonFilePath(file);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    // zapis danych o osobach do pliku XML
    public void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(personData);

            m.marshal(wrapper, file);

            setPersonFilePath(file);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save dara to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
}
