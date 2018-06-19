import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

public class Controller {
    // GLOBAIS //
    Thread t1;
    Search Pesquisa;
    List<String> ID = new ArrayList<>();
    String dir;
    // GLOBAIS //
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXTextField Pesquisar;

    @FXML
    private GridPane Grid = new GridPane();

    @FXML
    private AnchorPane Principal;

    @FXML
    private AnchorPane scroll;

    @FXML
    private JFXTextField path;

    @FXML
    private JFXComboBox<String> Formato;

    @FXML
    void Ok(ActionEvent event) throws InterruptedException {
        search(Pesquisar.getText());
    }

    @FXML
    void search(String Texto)  {
        Pesquisa = new Search(Texto);
        t1 = new Thread(Pesquisa);
        t1.start();

        Thread t = new Thread(new Runnable() {
            public void run() {
                try{
                    t1.join();

                    for (String title : Pesquisa.getID()){
                        ID.add(title);
                    }

                    List<Label> titles = new ArrayList<>();
                    for (String title : Pesquisa.getLista()){
                        titles.add(new Label(title));
                    }

                    List<ImageView> thumbs = new ArrayList<>();

                    for (String url : Pesquisa.getThumb()){
                        thumbs.add(new ImageView(new Image(url)));
                    }

                    for (int i = 0 ; i!= thumbs.size(); i++){

                        thumbs.get(i).setFitHeight(100);
                        thumbs.get(i).setFitWidth(100);

                        thumbs.get(i).setOnMouseClicked(action(i));
                        titles.get(i).setOnMouseClicked(action(i));

                        Grid.add(thumbs.get(i), 0, i);
                        Grid.add(titles.get(i),1, i);

                    }
                    Platform.runLater(() -> scroll.getChildren().add(Grid));


                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        });
        t.start();
    }
    // Set action on ImageView after search Video of Youtube
    private EventHandler<? super MouseEvent> action(int i) {

        EventHandler<MouseEvent> action = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String link = "http://youtube.com/watch?v=" + ID.get(i);
                        //Formato.getSelectionModel());
                        String cmd = "python C:\\Users\\20171bsi0464\\IdeaProjects\\DitiDownloader\\src\\main\\java\\DitiPytube.py " + link + " \"" + dir + "\" True," + Formato.getSelectionModel().getSelectedItem();
                        System.out.println(cmd);
                        try {
                            Process p = Runtime.getRuntime().exec(cmd);
                            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            String line;
                            while (true) {
                                line = r.readLine();
                                if (line == null) {
                                    break;
                                }
                                System.out.println(line);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }});
                thread.start();
            }
        };

        return action;
    }

    @FXML
    void salvarem(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione onde quer salvar");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dir = directoryChooser.showDialog(null).toString();
        path.setText(dir);


    }


    @FXML
    void initialize()  {
        Formato.getItems().add("MP4");
        Formato.getItems().add("MP3");


        //Principal.getChildren().add(listView);

    }
}
