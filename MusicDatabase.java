import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Set;

public class MusicDatabase {

    private Hashtable<String, ArrayList<PlayableItem>> data; 
    private TreeMap<String, ArrayList<PlayableItem>> artists;
    private Recommendation recommender;
    private int size;

    public MusicDatabase() {
        this.data = new Hashtable<String, ArrayList<PlayableItem>>();
        this.artists = new TreeMap<String, ArrayList<PlayableItem>>();
        this.size = 0;
        this.recommender = new Recommendation("UserData.csv");
    }

    public boolean addSongs(File inputFile) {
        if (inputFile.exists() && !inputFile.isDirectory()) {
            try {
                Scanner s = new Scanner(inputFile);
                String[] lst;
                if (s.hasNextLine()) {
                    s.nextLine();
                }
                while (s.hasNextLine()) {
                    lst = s.nextLine().split(",");
                    //String name = lst[2];
                    //String artist = lst[3];
                    int duration = Integer.parseInt(lst[4]);
                    int popularity = Integer.parseInt(lst[5]);
                    //String endpoint = lst[7];
                    this.addSongs(lst[2], lst[3], duration, popularity, lst[7]);
                }
                s.close();
                return true;
            } catch (FileNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    public void addSongs(String name, String artist, int duration,
                         int popularity, String endpoint) {
        PlayableItem newSong = new PlayableItem(0, duration,
                endpoint, name, artist, popularity);
        ArrayList<PlayableItem> songs;
        boolean found = false;
        if (data.containsKey(name)) {
            songs = data.get(name);
            for (int i = 0; i < songs.size(); i++) {
                if (newSong.equals(songs.get(i))) {
                    newSong = songs.get(i);
                    newSong.setPopularity(popularity);
                    songs.set(i, newSong);
                    found = true;
                }
            }
            if (!found) {
                songs.add(newSong);
            }
        } else {
            songs = new ArrayList<PlayableItem>();
            songs.add(newSong);
        }
        data.put(name, songs);

        found = false;
        if (artists.containsKey(artist)) {
            songs = artists.get(artist);
            for (int i = 0; i < songs.size(); i++) {
                if (newSong.equals(songs.get(i))) {
                    newSong = songs.get(i);
                    newSong.setPopularity(popularity);
                    songs.set(i, newSong);
                    found = true;
                }
            }
            if (!found) {
                songs.add(newSong);
            }
        } else {
            songs = new ArrayList<PlayableItem>();
            songs.add(newSong);
        }
        artists.put(artist, songs);
        this.size++;
    }

    public ArrayList<PlayableItem> partialSearchBySongName(String name) {
        Set<String> keys = data.keySet();
        ArrayList<PlayableItem> songs = new ArrayList<PlayableItem>();
        for (String key : keys) {
            if (key.toLowerCase().contains(name.toLowerCase())) {
                songs.addAll(data.get(key));
            }
        }
        return songs;
    }

    public ArrayList<PlayableItem> partialSearchByArtistName(String name) {
        Set<String> keys = artists.keySet();
        ArrayList<PlayableItem> songs = new ArrayList<PlayableItem>();
        for (String key : keys) {
            if (key.toLowerCase().contains(name.toLowerCase())) {
                ArrayList<PlayableItem> keySongs = artists.get(key);
                for (PlayableItem song : keySongs) {
                    int popularity = song.getPopularity();
                    int i = 0;
                    while (i < songs.size() && popularity <= songs.get(i).getPopularity()) {
                        i++;
                    }
                    if (i < songs.size()) {
                        songs.add(i, song);
                    } else {
                        songs.add(song);
                    }
                }
            }
        }
        return songs;
    }

    public ArrayList<PlayableItem> searchHighestPopularity(int threshold) {
        ArrayList<PlayableItem> songs = new ArrayList<PlayableItem>();
        for (String key : data.keySet()) {
            ArrayList<PlayableItem> keySongs = data.get(key);
            for (PlayableItem song : keySongs) {
                int popularity = song.getPopularity();
                if (popularity >= threshold) {
                    int i = 0;
                    while (i < songs.size() && popularity <= songs.get(i).getPopularity()) {
                        i++;
                    }
                    if (i < songs.size()) {
                        songs.add(i, song);
                    } else {
                        songs.add(song);
                    }
                }
            }
        }
        return songs;
    }

    public ArrayList<PlayableItem> getRecommendedSongs(List<String> fiveArtists) {
        String[] recommend = recommender.recommendNewArtists(fiveArtists);
        ArrayList<PlayableItem> songs = new ArrayList<>();
        for (String artist: recommend) {
            if (this.artists.containsKey(artist)) {
                songs.addAll(this.artists.get(artist));
                //System.out.println(artist);
            }
        }
        ArrayList<PlayableItem> finalSongs = new ArrayList<PlayableItem>();
        int added = 0;
        for (PlayableItem song : songs) {
            int popularity = song.getPopularity();
            int i = 0;
            while (i < finalSongs.size() && popularity <= finalSongs.get(i).getPopularity()) {
                i++;
            }
            if (i < finalSongs.size()) {
                finalSongs.add(i, song);
            } else {
                finalSongs.add(song);
            }
        }
        songs = new ArrayList<>();
        for (PlayableItem fSongs : finalSongs) {
            songs.add(fSongs);
            //System.out.println(fSongs);
            added++;
            if (added == 10) {
                return songs;
            }
        }
        return songs;
    }

    public int size() {
        return this.size;
    }
}
