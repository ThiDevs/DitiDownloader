import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Comparator;

public class JFX extends Application {
    private TableView<Person> table = new TableView<Person>();
    private final ObservableList<Person> data =
            FXCollections.observableArrayList(
                    new Person("http://icons.iconarchive.com/icons/iconicon/veggies/256/tomato-icon.png",new JFXSpinner(),"Jacob", "Smith" )

            );

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Table View Sample");

        final Label label = new Label("Address Book");
        label.setFont(new Font("Arial", 20));

        final Label actionTaken = new Label();

        table.setEditable(true);

        TableColumn firstNameCol = new TableColumn("Title");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("firstName"));

        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("lastName"));

        TableColumn emailCol = new TableColumn("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("email"));

        TableColumn<Person, Person> btnCol = new TableColumn<>("Gifts");
        btnCol.setMinWidth(150);
        btnCol.setCellValueFactory(new Callback<CellDataFeatures<Person, Person>, ObservableValue<Person>>() {
            @Override public ObservableValue<Person> call(CellDataFeatures<Person, Person> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        btnCol.setComparator(new Comparator<Person>() {
            @Override public int compare(Person p1, Person p2) {
                return p1.getLikes().compareTo(p2.getLikes());
            }
        });
        btnCol.setCellFactory(new Callback<TableColumn<Person, Person>, TableCell<Person, Person>>() {
            @Override public TableCell<Person, Person> call(TableColumn<Person, Person> btnCol) {
                return new TableCell<Person, Person>() {
                    final ImageView buttonGraphic = new ImageView();
                    final JFXButton button = new JFXButton(); {
                        button.setGraphic(buttonGraphic);
                        button.setMinWidth(130);
                    }
                    @Override public void updateItem(final Person person, boolean empty) {
                        super.updateItem(person, empty);
                        if (person != null) {
                            button.setText("Buy coffee");
                            buttonGraphic.setImage(new Image(person.getLikes()));

                            setGraphic(button);
                            button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override public void handle(ActionEvent event) {
                                    actionTaken.setText("Bought " + person.getLikes().toLowerCase() + " for: " + person.getFirstName() + " " + person.getLastName());

                                    Search Pesquisa = new Search("Rap lord");
                                    Thread t1 = new Thread(Pesquisa);
                                    t1.start();
                                    try {
                                        t1.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    for (int i= 0 ; i != Pesquisa.getLista().size(); i++ ){
                                         data.add(new Person(Pesquisa.getThumb().get(i), new JFXSpinner(),Pesquisa.getLista().get(i), "Smith"));
                                }
                                }
                            });
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });

        table.setItems(data);
        table.getColumns().addAll( btnCol,  emailCol,lastNameCol,firstNameCol);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll(label, table, actionTaken);
        VBox.setVgrow(table, Priority.ALWAYS);

        stage.setScene(new Scene(vbox));
        stage.show();

    }

    public static class Person {

        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private JFXSpinner email;
        private final SimpleStringProperty likes;

        private Person(String likes,JFXSpinner email,String fName, String lName ) {
            this.likes = new SimpleStringProperty(likes);
            this.email = email;
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);


        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String fName) {
            lastName.set(fName);
        }

        public JFXSpinner getEmail() {
            return email;
        }

        public void setEmail(JFXSpinner fName) {
            email = fName;
        }

        public String getLikes() {
            return likes.get();
        }

        public void setLikes(String likes) {
            this.likes.set(likes);
        }
    }
}