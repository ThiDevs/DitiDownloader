import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import javax.imageio.ImageIO;

public class Controller {
    // GLOBAIS //

    /* PUSH PATH OF SCRIPTS */

    private String Path_YoutubeLinks = "C:\\Users\\Thiago\\IdeaProjects\\DitiDownloader\\src\\main\\java\\YoutubeLink.py";
    private String DitiPytube = "C:\\Users\\Thiago\\IdeaProjects\\DitiDownloader\\src\\main\\java\\DitiPytube.py";

    private Thread t1;
    private Search Pesquisa;
    private List<String> ID = new ArrayList<>();
    private List<Label> titles = new ArrayList<>();
    private List<ImageView> thumbs = new ArrayList<>();
    private String dir;
    private List<JFXSpinner> titles2 = new ArrayList<>();
    // GLOBAIS //

    @FXML
    private JFXTextField Pesquisar;

    @FXML
    private GridPane Grid = new GridPane();

    @FXML
    private AnchorPane scroll;

    @FXML
    private JFXTextField path;

    @FXML
    private JFXComboBox<String> Formato;

    @FXML
    JFXButton downloadall;
    @FXML
    JFXSpinner Carregando;

    private String TextoSeparado = "";
    @FXML
    void Ok() {
        //
        Carregando.setVisible(true);

            try {
                TextoSeparado = Pesquisar.getText().substring(0, 38);
            } catch (Exception ignored) {}
        if (TextoSeparado.equals("https://www.youtube.com/playlist?list=")) {
            downloadall.setVisible(true);
            Platform.runLater(() -> Carregando.setVisible(true));
            List<Label> titles = new ArrayList<>();

            Thread thread = new Thread(() -> {
                ClearALL();
                String link = Pesquisar.getText();
                /* Calls Python */

                String cmd = "python "+Path_YoutubeLinks+" " + link;
                System.out.println(cmd);
                String line = "";
                try {
                    Process p = Runtime.getRuntime().exec(cmd);
                    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), "Windows-1252"));
                    String aux;
                    int i = 0;
                    ID.clear();
                    while (true) {
                        aux = line;
                        line = r.readLine();
                        if (line == null) {
                            break;
                        }

                        if(!aux.equals(line)) {
                            try {
                                List<String> INFOS = GetTitle(line.substring(31));
                                String titulo = INFOS.get(0);
                                String thumb = INFOS.get(1);

                                thumbs.add(new ImageView(new Image(thumb)));
                                thumbs.get(i).setFitHeight(100);
                                thumbs.get(i).setFitWidth(100);
                                Grid.add(thumbs.get(i), 0, i);

                                titles.add(new Label(titulo.replaceAll("\"", "")));
                                titles.get(i).setStyle("-fx-text-fill:  #d4d6cd;");
                                Grid.add(titles.get(i), 2, i);

                                thumbs.get(i).setOnMouseClicked(action(i));
                                titles.get(i).setOnMouseClicked(action(i));

                                titles2.add(new JFXSpinner());
                                titles2.get(i).setVisible(false);
                                Grid.add(titles2.get(i), 1, i);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //

                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> scroll.getChildren().add(Grid));
                Platform.runLater(() -> Carregando.setVisible(false));

            });
            thread.start();
        } else {
            search(Pesquisar.getText());
            Platform.runLater(() -> Carregando.setVisible(false));

        }

    }

    private Thread thread;
    private String line = "";
    private void DownloadVideo(String IdVideo, int i){


        thread = new Thread(() -> {
            String link = "http://youtube.com/watch?v=" + IdVideo;
            /* Calls Python */
            titles2.get(i).setVisible(true);
            dir = path.getText();
            String cmd = "python "+DitiPytube+" " + link + " \"" + dir + "\" True," + Formato.getSelectionModel().getSelectedItem();
            System.out.println(cmd);

            try {
                Process p = Runtime.getRuntime().exec(cmd);
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), "Windows-1252"));
                String aux;
                while (true) {
                    aux = line;
                    line = r.readLine();

                    if (line == null) {
                        line = aux;
                        break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                double porctagem = 100.00;
                Platform.runLater(() -> titles2.get(i).setProgress(porctagem));

            } catch (Exception ignored) {

            }


        });
        thread.start();
        /*Thread _ConverteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    thread.join();
                } catch (Exception e){

                }
                String path = dir + "\\" + line;
                Converte(path, i);

            }});
        Converte_Thread.start();


*/
        //Thread para converter


    }

    @FXML
    private void search(String Texto) {
        Pesquisa = new Search(Texto);
        t1 = new Thread(Pesquisa);
        t1.start();
/*
        String url2 = "https://api.vagalume.com.br/search.excerpt?q="+Texto+"&limit=5";

        try {
            URL url = new URL(url2);
            URLConnection request = url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject docs = root.getAsJsonObject().get("response").getAsJsonObject().get("docs").getAsJsonArray().get(0).getAsJsonObject();
            System.out.println(docs.get("title"));
            System.out.println(docs.get("band"));



        } catch (Exception e ){

        }

*/


        Thread t = new Thread(() -> {
            try {
                t1.join();
                ClearALL();

                ID.addAll(Pesquisa.getID());

                for (String title : Pesquisa.getLista()) {
                    titles.add(new Label(title));
                }


                for (String url : Pesquisa.getThumb()) {
                    thumbs.add(new ImageView(new Image(url)));
                }


                for (int i = 0; i != thumbs.size(); i++) {

                    thumbs.get(i).setFitHeight(100);
                    thumbs.get(i).setFitWidth(100);

                    thumbs.get(i).setOnMouseClicked(action(i));
                    titles.get(i).setOnMouseClicked(action(i));
                    titles.get(i).setStyle("-fx-text-fill: white");
                    Grid.add(thumbs.get(i), 0, i);
                    Grid.add(titles.get(i), 2, i);
                    titles2.add(new JFXSpinner());
                    titles2.get(i).setVisible(false);
                    Grid.add(titles2.get(i), 1, i);

                    data.add(new Person(Pesquisa.getThumb().get(i), new JFXSpinner(),Pesquisa.getLista().get(i)));

                }
                Platform.runLater(() -> scroll.getChildren().add(Grid));


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    private void ClearALL() {
        Platform.runLater(() -> scroll.getChildren().clear());
        Platform.runLater(() -> Grid.getChildren().clear());

        try {
            ID.clear();
        } catch (Exception ignored) {
        }
        try {
            titles2.clear();
        } catch (Exception ignored) {
        }
        try {
            titles.clear();
        } catch (Exception ignored) {
        }
        try {
            thumbs.clear();
        } catch (Exception ignored) {
        }
    }

    // Set action on ImageView after search Video of Youtube
    private EventHandler<? super MouseEvent> action(int i) {

        return (EventHandler<MouseEvent>) event -> {
        DownloadVideo(ID.get(i),i);
            //https://www.youtube.com/playlist?list=PLQuDmj3ez49wUERdKxZYfoDVESQuvppH2
        };
    }

    @FXML
    void salvarem() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione onde quer salvar");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dir = directoryChooser.showDialog(null).toString();
        path.setText(dir);
    }

    private void Converte(String dir) {
        Float Total = null;
        try{        Total = GetDuration(dir);}
        catch (Exception e){
            titles2.get(-1).setVisible(false);
        }
        try {
            ProcessBuilder builder = new ProcessBuilder("ffmpeg", "-i", dir, "-progress", "-", "-y", dir + ".mp3");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                try {
                    Float calc = GetDuration(dir + ".mp3");
                    float porctagem = (calc) / Total;
                    Platform.runLater(() -> titles2.get(-1).setProgress(porctagem));

                } catch (Exception ignored) {

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SetMetada(dir + ".mp3");
    }

    @FXML
    void getmusic() {
        FileChooser directoryChooser = new FileChooser();
        directoryChooser.setTitle("Selecione onde quer salvar");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dir = directoryChooser.showOpenDialog(null).toString();
        path.setText(dir);

        Converte(dir);

    }

    private Float GetDuration(String path) {
        /* Calls FFPROBE */
        String FFPROBE = "ffprobe -i \"" + path + "\" -v quiet -print_format json -show_format -show_streams -hide_banner";
        StringBuilder aux = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(FFPROBE);
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line2 = "";
            do {
                aux.append(line2);
                line2 = r.readLine();
            } while (line2 != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject contentObj = new JsonParser().parse(aux.toString()).getAsJsonObject();
        JsonArray string = contentObj.getAsJsonArray("streams");
        JsonElement element = string.get(0);
        String durationString = element.getAsJsonObject().get("duration").toString();

        return Float.parseFloat(durationString.replaceAll("\"", ""));
    }

    @FXML
    void DownloadAll() {
        Thread t;
        t = new Thread(() -> {
            int i = 0;
            for (String id : ID) {
                if (Thread.activeCount() > 3) {
                    try {
                        thread.join();
                    } catch (Exception ignored) {
                    }

                }
                DownloadVideo(id, i);
                i++;
            }
        });
        t.start();

    }
    private void SetMetada(String dir){
        try {
            File mp3File = new File(dir);
            AudioFile audioFile = AudioFileIO.read(mp3File);
            audioFile.setTag(new ID3v23Tag());
            Tag newTag = audioFile.getTag();
            String album = "teste";
            newTag.setField(FieldKey.ALBUM, album);

            String temp = System.getProperty("java.io.tmpdir");
            BufferedImage bImage = SwingFXUtils.fromFXImage(thumbs.get(-1).getImage(), null);
            try {
                ImageIO.write(bImage, "png",new File(temp+"\\image.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Artwork artwork = Artwork.createArtworkFromFile(new File(temp+"\\image.png"));//createArtworkFromMetadataBlockDataPicture();//(fileCover);
            newTag.addField(artwork);
            newTag.setField(artwork);
            audioFile.commit();
        } catch (Exception e){
            titles2.get(-1).setVisible(false);
            e.printStackTrace();
        }


    }
    @FXML
    void SetMetadas() {
        FileChooser directoryChooser = new FileChooser();
        directoryChooser.setTitle("Selecione onde quer salvar");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dir = directoryChooser.showOpenDialog(null).toString();
        path.setText(dir);
        SetMetada(dir);
    }
    @FXML
    private TableView<Person> table;
    private final ObservableList<Person> data =
            FXCollections.observableArrayList();
    @FXML
    private VBox vbox;
    @FXML
    void initialize() {
        Formato.getItems().add("MP4");
        Formato.getItems().add("MP3");
        Formato.getSelectionModel().select(Formato.getItems().get(1));

        path.setText(System.getProperty("user.home") + "\\Desktop");

        Grid.setVisible(false);


        table.setEditable(true);

        TableColumn<Person, String> firstNameCol = new TableColumn<>("Title");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(30);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        TableColumn<Person, Person> btnCol = new TableColumn<>("Gifts");
        btnCol.setMinWidth(350);
        btnCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue()));
        btnCol.setComparator(Comparator.comparing(Person::getLikes));
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

                            buttonGraphic.setImage(new Image(person.getLikes()));

                            setGraphic(button);
                            button.setOnAction(event -> {

                                Search Pesquisa = new Search("Rap lord");
                                Thread t1 = new Thread(Pesquisa);
                                t1.start();
                                try {
                                    t1.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                for (int i= 0 ; i != Pesquisa.getLista().size(); i++ ){
                                    data.add(new Person(Pesquisa.getThumb().get(i), new JFXSpinner(),Pesquisa.getLista().get(i)));
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
        table.getColumns().addAll( btnCol,  emailCol,firstNameCol);
//teste
        vbox.setSpacing(5);
        VBox.setVgrow(table, Priority.ALWAYS);



        }

    private List<String> GetTitle(String VideoID) {

        String ur2l = "https://www.googleapis.com/youtube/v3/videos?part=id%2C+snippet&id=" + VideoID + "&key=AIzaSyDGRbEc7qbGJ59Vsv68fL0aHml1FYpX_1g";
        List<String> info = new ArrayList<>();
        try {
            URL url = new URL(ur2l);
            URLConnection request = url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject rootobj = root.getAsJsonObject();
            JsonArray string = rootobj.getAsJsonArray("items");
            JsonElement element = string.get(0);
            JsonElement element2 = element.getAsJsonObject().get("snippet");
            String titulo = element2.getAsJsonObject().get("title").toString();
            info.add(titulo);


            String thumb = element2.getAsJsonObject().get("thumbnails").getAsJsonObject().get("default").getAsJsonObject().get("url").toString().replaceAll("\"","");

            info.add(thumb);

            String id = element.getAsJsonObject().get("id").toString().replaceAll("\"","");

            ID.add(id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }
    public static class Person {

        private final SimpleStringProperty firstName;
        private JFXSpinner email;
        private final SimpleStringProperty likes;

        private Person(String likes,JFXSpinner email,String fName ) {
            this.likes = new SimpleStringProperty(likes);
            this.email = email;
            this.firstName = new SimpleStringProperty(fName);


        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
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