import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.*;
import com.mpatric.mp3agic.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockData;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.mortbay.util.ajax.JSONObjectConvertor;

import javax.imageio.ImageIO;

public class Controller {
    // GLOBAIS //

    /* PUSH PATH OF SCRIPTS */

    String Path_YoutubeLinks = "C:\\Users\\Thiago\\IdeaProjects\\DitiDownloader\\src\\main\\java\\YoutubeLink.py";
    String DitiPytube = "C:\\Users\\Thiago\\IdeaProjects\\DitiDownloader\\src\\main\\java\\DitiPytube.py";

    Thread t1;
    Search Pesquisa;
    List<String> ID = new ArrayList<>();
    List<Label> titles = new ArrayList<>();
    List<ImageView> thumbs = new ArrayList<>();
    String dir;
    List<JFXSpinner> titles2 = new ArrayList<>();
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
    private ScrollPane ScrollPane;

    @FXML
    private JFXTextField path;

    @FXML
    private JFXComboBox<String> Formato;

    @FXML
    JFXButton downloadall;
    @FXML
    JFXSpinner Carregando;

    String TextoSeparado = "";
    @FXML
    void Ok(ActionEvent event) throws InterruptedException {
        //
        Carregando.setVisible(true);

            try {
                TextoSeparado = Pesquisar.getText().substring(0, 38);
            } catch (Exception e) {}
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
                            line = aux;
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
    List<Thread> threads = new ArrayList<>();
    Thread thread;
    String line = "";
    void DownloadVideo(String IdVideo,int i){


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                    if (i != -1) {
                        Platform.runLater(() -> titles2.get(i).setProgress(porctagem));
                    }

                } catch (Exception e) {

                }


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
    void search(String Texto) {
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


        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    t1.join();
                    ClearALL();

                    for (String title : Pesquisa.getID()) {
                        ID.add(title);
                    }

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

                    }
                    Platform.runLater(() -> scroll.getChildren().add(Grid));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    void ClearALL() {
        Platform.runLater(() -> scroll.getChildren().clear());
        Platform.runLater(() -> Grid.getChildren().clear());

        try {
            ID.clear();
        } catch (Exception e) {
        }
        try {
            titles2.clear();
        } catch (Exception e) {
        }
        try {
            titles.clear();
        } catch (Exception e) {
        }
        try {
            thumbs.clear();
        } catch (Exception e) {
        }
    }

    // Set action on ImageView after search Video of Youtube
    private EventHandler<? super MouseEvent> action(int i) {

        EventHandler<MouseEvent> action = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
            DownloadVideo(ID.get(i),i);
                //https://www.youtube.com/playlist?list=PLQuDmj3ez49wUERdKxZYfoDVESQuvppH2
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

    void Converte(String dir, int i) {
        Float Total = null;
        try{        Total = GetDuration(dir);}
        catch (Exception e){
            titles2.get(i).setVisible(false);
        }
        try {
            String cmd = "ffmpeg  -i " + dir + " -progress - -y " + dir + ".mp3";
            ProcessBuilder builder = new ProcessBuilder("ffmpeg", "-i", dir, "-progress", "-", "-y", dir + ".mp3");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                try {
                    Float calc = GetDuration(dir + ".mp3");
                    Float porctagem = (calc) / Total;
                    if (i != -1) {
                        Platform.runLater(() -> titles2.get(i).setProgress(porctagem));
                    }

                } catch (Exception e) {

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SetMetada(dir + ".mp3",i);
    }

    @FXML
    void getmusic(ActionEvent event) {
        FileChooser directoryChooser = new FileChooser();
        directoryChooser.setTitle("Selecione onde quer salvar");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dir = directoryChooser.showOpenDialog(null).toString();
        path.setText(dir);

        Converte(dir, -1);

    }

    private Float GetDuration(String path) {
        /* Calls FFPROBE */
        String FFPROBE = "ffprobe -i \"" + path + "\" -v quiet -print_format json -show_format -show_streams -hide_banner";
        String aux = "";
        try {
            Process p = Runtime.getRuntime().exec(FFPROBE);
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line2 = "";
            while (true) {
                aux += line2;
                line2 = r.readLine();
                if (line2 == null) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject contentObj = new JsonParser().parse(aux).getAsJsonObject();
        JsonArray string = contentObj.getAsJsonArray("streams");
        JsonElement element = string.get(0);
        String durationString = element.getAsJsonObject().get("duration").toString();
        float duration = Float.parseFloat(durationString.replaceAll("\"", ""));

        return duration;
    }

    @FXML
    void DownloadAll() {
        Thread t = null;
        t = new Thread(() -> {
            int i = 0;
            for (String id : ID) {
                if (Thread.activeCount() > 3) {
                    try {
                        thread.join();
                    } catch (Exception e) {
                    }

                }
                DownloadVideo(id, i);
                i++;
            }
        });
        t.start();

    }
    void SetMetada(String dir,int i){
        try {
            File mp3File = new File(dir);
            AudioFile audioFile = AudioFileIO.read(mp3File);
            audioFile.setTag(new ID3v23Tag());
            Tag newTag = audioFile.getTag();
            String album = "teste";
            newTag.setField(FieldKey.ALBUM, album);

            String temp = System.getProperty("java.io.tmpdir");
            BufferedImage bImage = SwingFXUtils.fromFXImage(thumbs.get(i).getImage(), null);
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
            titles2.get(i).setVisible(false);
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
        SetMetada(dir,-1);
    }

    @FXML
    void initialize() {
        Formato.getItems().add("MP4");
        Formato.getItems().add("MP3");
        Formato.getSelectionModel().select(Formato.getItems().get(1));

        path.setText(System.getProperty("user.home") + "\\Desktop");


        }

    List<String> GetTitle(String VideoID) {

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
}