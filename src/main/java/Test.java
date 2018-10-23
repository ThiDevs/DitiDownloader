import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.List;
/**
 * User: paul
 * Date: 09-Jun-2009
 */
public class Test
{
    public static void main(String[] args) {

try {

    YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
            new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("video-test").build();

    final String videoId = "Hl-zzrqQoSE";
    YouTube.Videos.List videoRequest = youtube.videos().list("snippet,statistics,contentDetails");
    videoRequest.setId(videoId);
    videoRequest.setKey("AIzaSyDGRbEc7qbGJ59Vsv68fL0aHml1FYpX_1g");
    VideoListResponse listResponse = videoRequest.execute();
    List<Video> videoList = listResponse.getItems();

    Video targetVideo = videoList.iterator().next();
    System.out.println(targetVideo.getSnippet().getDescription());
    System.out.println(targetVideo.getStatistics().getViewCount());
    System.out.println(targetVideo.getSnippet().getChannelTitle());
   //
    //targetVideo.getContentDetails().getDuration(););



} catch (Exception e){
    e.printStackTrace();

}




        Search4 test = new Search4("Rap Lord");
        Thread t1 = new Thread(test);
       // t1.start();



    }

    void getInfos(){

    }

}



/**
 * Print a list of videos matching a search term.
 *
 * @author Jeremy Walker
 */
class Search4 implements Runnable {

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 7;
    //private static final Object List = ;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    static String texto;
    private static YouTube youtube;
    List<String> id;
    List<String> titles;
    List<String> thumb;

    public Search4(String text) {
        texto = text;
    }

    private void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
        try{id.clear();}catch (Exception e){}
        try{titles.clear();}catch (Exception e){}
        try{thumb.clear();}catch (Exception e){}

        if (!iteratorSearchResults.hasNext()) {
        }
        id = new ArrayList<String>();
        titles = new ArrayList<String>();
        thumb = new ArrayList<String>();
        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();



            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getMedium();

                //ControllerAudio.Id.add(rId.getVideoId());
                id.add(rId.getVideoId());
                titles.add(singleVideo.getSnippet().getTitle());
                //ControllerAudio.Title.add();
                System.out.println(singleVideo.getSnippet().getTitle());
                System.out.println(singleVideo);


                thumb.add(thumbnail.getUrl());
                //ControllerAudio.Url.add(thumbnail.getUrl());
            }
        }
        setID(id);
        setLista(titles);
        setThumb(thumb);
    }

    public void run() {
        Properties properties = new Properties();
        try {
            InputStream in = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }

        try {
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();
            String queryTerm = texto;
            YouTube.Search.List search = youtube.search().list("id,snippet");
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/medium/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm);
                // setList(prettyPrint(searchResultList.iterator(), queryTerm));
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void setLista(List<String> titles) {
        this.titles = titles;
    }
    public List<String> getLista() {
        return titles;
    }
    public void setThumb(List<String> thumb) {
        this.thumb = thumb;
    }
    public List<String> getThumb() {
        return thumb;
    }
    public void setID(List<String> ids) {
        this.id = ids;
    }
    public List<String> getID() {
        return id;
    }
}

