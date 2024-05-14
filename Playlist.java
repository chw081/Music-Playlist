import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Random;
import java.util.Collections;
import java.util.HashSet;

public class Playlist {

    private String name;
    private int playingMode = 0;
    private int playingIndex = 0;
    private ArrayList<PlayableItem> curList;
    private PlayableItem cur;
    private Stack<PlayableItem> history;
    private PriorityQueue<PlayableItem> freqListened;
    private ArrayList<PlayableItem> playlist;

    public Playlist() {
        this.name = "Default";
        curList = new ArrayList<PlayableItem>();
        cur = null;
        history = new Stack<PlayableItem>();
        freqListened = new PriorityQueue<PlayableItem>(Collections.reverseOrder());
        playlist = new ArrayList<PlayableItem>();
    }

    public Playlist(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int size() {
        return playlist.size();
    }

    public String toString() {
        return this.name + "," + this.curList.size() + " songs";
    }

    public void addPlayableItem(PlayableItem newItem) {
        if (playingMode == 0) {
            //System.out.println("add"); 101
            curList.add(newItem);
        } else if (playingMode == 1) {
            //System.out.println("add");
            curList.add(newItem);
        } else if (playingMode == 2) {
            //System.out.println("add"); 0
            freqListened.offer(newItem);
        } else if (playingMode == 3) {
            //System.out.println(newItem); 20
            //curList = new ArrayList<PlayableItem>();
            curList.add(newItem);
        }
        //System.out.println("add");
        playlist.add(newItem);
    }

    public void addPlayableItem(ArrayList<PlayableItem> newItem) {
        //System.out.println("0"); 1
        //System.out.println("2"); 0
        //System.out.println("3"); 2
        for (PlayableItem item : newItem) {
            //121 songs
            this.addPlayableItem(item);
        }
    }

    public boolean removePlayableItem(int number) {
        if (number > this.playlist.size()) {
            return false;
        }
        PlayableItem rmvSong = this.playlist.get(number - 1);
        if (playingMode == 2) {
            freqListened.remove(rmvSong);
        } else if (cur == null || !cur.equals(rmvSong)) {
            this.curList.remove(rmvSong);
        }
        //System.out.println("remove"); 1
        return this.playlist.remove(rmvSong);
    }

    public void switchPlayingMode(int newMode) {
        //System.out.println(newMode); 4
        this.playingMode = newMode;
        if (newMode == 0) {
            //System.out.println(newMode); 1
            this.curList = new ArrayList<PlayableItem>();
            this.curList.add(cur);
            for (PlayableItem song : playlist) {
                this.curList.add(song);
            }
        } else if (newMode == 2) {
            //System.out.println(newMode); 1
            /*
            this.freqListened = new PriorityQueue<PlayableItem>(Collections.reverseOrder());
            for (PlayableItem song : playlist) {
                this.freqListened.offer(song);
            }
             */
            this.freqListened = new PriorityQueue<PlayableItem>(Collections.reverseOrder());
            for (PlayableItem song : playlist) {
                this.freqListened.offer(song);
            }
        } else if (newMode == 3) {
            //System.out.println(newMode); 2
            //this.showPlaylistStatus();
            this.curList = new ArrayList<PlayableItem>();
            if (cur != null) {
                this.curList.add(cur);
            }
        }
    }

    public ArrayList<String> getFiveMostPopular() {
        PriorityQueue<PlayableItem> popular = new PriorityQueue<PlayableItem>(
                Collections.reverseOrder());
        for (PlayableItem song : playlist) {
            popular.offer(song);
        }
        HashSet<String> artists = new HashSet<String>();
        PlayableItem song = null;
        while (!popular.isEmpty() && artists.size() < 5) {
            song = popular.poll();
            artists.add(song.getArtist());
            //System.out.println(song.getArtist());
        }
        while (!popular.isEmpty() && song != null && popular.peek().compareTo(song) == 0) {
            //System.out.println(popular.peek().getArtist());
            artists.add(popular.poll().getArtist());
        }
        ArrayList<String> lst = new ArrayList<String>(artists);
        Collections.sort(lst);
        while (lst.size() > 5) {
            lst.remove(lst.size() - 1);
        }
        return lst;
    }

    /**
     * Go to the last playing item
     */
    public void goBack() {
        if (history.isEmpty()) {
            System.out.println("No more step to go back");
        } else if (playingMode == 2) {
            if (cur != null) {
                freqListened.offer(cur);
            }
            cur = history.pop();
        } else {
            curList.add(0, history.pop());
            cur = curList.get(0);
        }
    }

    public void play(int seconds) {
        //System.out.println("play"); 5
        if (seconds <= 0) {
            System.out.println("Invalid option");
            return;
        }
        if (playingMode != 2 && curList.isEmpty()) {
            System.out.println("No more music to play.");
            return;
        }
        if (playingMode == 0) {
            this.normalMode(seconds);
        } else if (playingMode == 1) {
            this.randomMode(seconds);
        } else if (playingMode == 2) {
            if (cur == null) {
                cur = this.getNextPlayable();
            }
            if (cur == null) {
                System.out.println("No more music to play.");
                return;
            }
            System.out.println("Seconds 0 : " + cur.getTitle() + " start.");
            for (int i = 1; i <= seconds; i++) {
                if (!cur.play()) {
                    System.out.println("Seconds " + i + " : " + cur.getTitle()
                            + " complete.");
                    history.add(cur);
                    cur = this.getNextPlayable();
                    //System.out.println(cur);
                    if (cur == null) {
                        System.out.println("No more music to play.");
                        return;
                    }
                    System.out.println("Seconds " + i + " : " + cur.getTitle() + " start.");
                }
            }
        } else if (playingMode == 3) {
            if (cur == null) {
                cur = this.getNextPlayable();
            }
            if (cur == null) {
                System.out.println("No more music to play.");
                return;
            }
            System.out.println("Seconds 0 : " + cur.getTitle() + " start.");
            for (int i = 1; i <= seconds; i++) {
                if (!cur.play()) {
                    System.out.println("Seconds " + i + " : " + cur.getTitle()
                            + " complete.");
                    history.add(curList.remove(0));
                    cur = this.getNextPlayable();
                    if (cur == null) {
                        System.out.println("No more music to play.");
                        return;
                    }
                    //cur = this.getNextPlayable();
                    System.out.println("Seconds " + i + " : " + cur.getTitle() + " start.");
                }
            }
        }
        //System.out.println(this.showPlaylistStatus());
    }

    private void normalMode(int seconds) {
        if (cur == null) {
            cur = this.getNextPlayable();
            playingIndex = 0;
        }
        if (cur == null) {
            System.out.println("No more music to play.");
            return;
        }
        System.out.println("Seconds 0 : " + cur.getTitle() + " start.");
        for (int i = 1; i <= seconds; i++) {
            if (!cur.play()) {
                System.out.println("Seconds " + i + " : " + cur.getTitle()
                        + " complete.");
                history.add(curList.remove(0));
                cur = this.getNextPlayable();
                if (cur == null) {
                    System.out.println("No more music to play.");
                    return;
                }
                //cur = this.getNextPlayable();
                System.out.println("Seconds " + i + " : " + cur.getTitle() + " start.");
            }
        }
    }

    private void randomMode(int seconds) {
        if (cur == null) {
            cur = this.getNextPlayable();
        }
        System.out.println("Seconds 0 : " + cur.getTitle() + " start.");
        for (int i = 1; i <= seconds; i++) {
            if (!cur.play()) {
                System.out.println("Seconds " + i + " : " + cur.getTitle()
                        + " complete.");
                history.add(cur);
                cur = this.getNextPlayable();
                System.out.println("Seconds " + i + " : " + cur.getTitle() + " start.");
            }
        }
    }

    public String showPlaylistStatus() {
        String status = "";
        if (playlist.size() == 0) {
            return status;
        }
        int counter = 1;
        for (PlayableItem song : playlist) {
            //System.out.println(counter + "." + song.toString());
            status += counter + ". " + song.toString();
            if (cur != null && song.equals(cur)) {
                status += " - Currently play";
            }
            status += "\n";
            counter++;
        }
        //System.out.println(status);
        return status.strip();
        //return status;
    }

    public PlayableItem getNextPlayable() {
        if (curList.size() == 0 && playingMode != 2) {
            return null;
        }
        if (freqListened.size() == 0 && playingMode == 2) {
            return null;
        }
        if (playingMode == 0) {
            if (cur == null || !cur.equals(curList.get(0))) {
                return curList.get(0);
            } else if (curList.size() > 1) {
                return curList.get(1);
            }
        } else if (playingMode == 1) {
            Random rand = new Random();
            int randIdx = rand.nextInt(playlist.size());
            return playlist.get(randIdx);
        } else if (playingMode == 3) {
            //this.showPlaylistStatus();
            if (cur == null || !cur.equals(curList.get(0))) {
                return curList.get(0);
            } else if (curList.size() > 1) {
                return curList.get(1);
            }
        } else if (playingMode == 2) {
            //freqListened.peek();
            return freqListened.poll();
        }
        return null;
    }

}

