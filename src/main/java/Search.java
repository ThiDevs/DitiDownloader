

/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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

/**
 * Print a list of videos matching a search term.
 *
 * @author Jeremy Walker
 */
public class Search implements Runnable {

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;
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

    public Search(String text) {
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

