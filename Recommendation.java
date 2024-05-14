import java.io.File;
import java.io.FileNotFoundException;
//import java.io.IOException;
import java.util.ArrayList;
//import java.util.Comparator;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
//import java.util.Set;
//import java.util.stream.Collectors;
import java.util.Collections;

public class Recommendation {

    Map<Long, HashMap<String, Integer>> userData;
    //Map<Long, HashMap<String, Integer>> allData;

    public Recommendation(String filePath) {
        userData = new HashMap<>();
        //allData = new HashMap<>();
        this.parseCsvFile(filePath);
    }

    private void parseCsvFile(String csvFilePath) {
        try {
            Scanner s = new Scanner(new File(csvFilePath));
            String[] lst;
            if (s.hasNextLine()) {
                s.nextLine();
            }
            while (s.hasNextLine()) {
                lst = s.nextLine().split(",");
                long userID = Long.parseLong(lst[0]);
                String artist = lst[1];
                int minutes = Integer.parseInt(lst[3]);
                HashMap<String, Integer> newData;
                if (this.userData.containsKey(userID)) {
                    newData = this.userData.get(userID);
                    if (newData.containsKey(artist)) {
                        minutes += newData.get(artist);
                    }
                } else {
                    newData = new HashMap<String, Integer>();
                }
                newData.put(artist, minutes);
                this.userData.put(userID, newData);
                //this.allData.put(userID, newData);
            }
            s.close();
            for (long key : this.userData.keySet()) {
                this.sortArtist(key);
            }
        } catch (FileNotFoundException e) {
            /*exp*/
        }
    }

    private void sortArtist(long key) {
        HashMap<String, Integer> keySongs = this.userData.get(key);
        ArrayList<Integer> minutes = new ArrayList<Integer>(keySongs.values());
        //System.out.println(keySongs.keySet());
        Collections.sort(minutes);
        Collections.reverse(minutes);
        HashMap<String, Integer> artists = new HashMap<String, Integer>();
        for (Integer minute : minutes) {
            for (String artist : keySongs.keySet()) {
                if (keySongs.get(artist) == minute) {
                    //System.out.println(artist);
                    artists.put(artist, minute);
                    //i++;
                }
                if (artists.size() == 5) {
                    this.userData.put(key, artists);
                    /*
                    for (String art : artists.keySet()) {
                        System.out.println(art);
                    }
                     */
                    return;
                }
            }
        }
        /*
        for (String art : artists.keySet()) {
            System.out.println(art);
        }
         */
        this.userData.put(key, artists);
    }

    public String[] recommendNewArtists(List<String> artistList) {
        ArrayList<Long> users = new ArrayList<>(this.userData.keySet());
        ArrayList<Double> similarity = new ArrayList<>();
        for (long key : users) {
            int cup = 0;
            int cap = 0;
            for (String artistA : artistList) {
                for (String artistB : this.userData.get(key).keySet()) {
                    if (artistA.equals(artistB)) {
                        cap++;
                    }
                }
            }
            cup = artistList.size() + this.userData.get(key).keySet().size() - cap;
            //System.out.println(key + ":" + (cap + 0.0) / cup);
            similarity.add((cap + 0.0) / cup);
        }
        int i = 0;
        ArrayList<String> tops = new ArrayList<String>();
        while (i < 3 && users.size() > 0) {
            int idx = 0;
            for (int j = 0; j < users.size(); j++) {
                if (similarity.get(j) > similarity.get(idx)) {
                    idx = j;
                }
            }
            //System.out.println(users.get(idx));
            for (String artists : this.userData.get(users.get(idx)).keySet()) {
                if (!tops.contains(artists) && !artistList.contains(artists)) {
                    //System.out.println(artists + " - added");
                    tops.add(artists);
                }
            }
            users.remove(users.get(idx));
            similarity.remove(similarity.get(idx));
            i++;
        }

        String[] recommend = new String[tops.size()];
        for (int idx = 0; idx < recommend.length; idx++) {
            recommend[idx] = tops.get(idx);
            //System.out.println(recommend[idx]);
        }
        return recommend;
    }

}
