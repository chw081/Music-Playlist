/**
 * <b>May not add any accessor/mutator for this class</b>
 */
public class PlayableItem implements Comparable<PlayableItem> {
    private int lastPlayedTime;
    private int totalPlayTime;
    private String endpoint;
    private String title;
    private String artist;
    private int popularity;
    private int playedCounts; // How many time this song has been played, initially to be 0

    public PlayableItem(int lastTime, int totalPlayTime, String endpoint,
                        String title, String artist, int popularity) {
        this.lastPlayedTime = lastTime;
        this.totalPlayTime = totalPlayTime;
        this.endpoint = endpoint;
        this.title = title;
        this.artist = artist;
        this.popularity = popularity;
        this.playedCounts = 0;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getTitle() {
        return this.title;
    }

    public int getPopularity() {
        return this.popularity;
    }

    public void setPopularity(int pop) {
        this.popularity = pop;
    }

    public boolean playable() {
        if (this.lastPlayedTime == this.totalPlayTime) {
            return false;
        }
        return true;
    }

    public boolean play() {
        if (this.lastPlayedTime < this.totalPlayTime) {
            this.lastPlayedTime++;
            if (playable()) {
                return true;
            }
            this.lastPlayedTime = 0;
            playedCounts++;
            return false;
        }
        //this.lastPlayedTime = 0;
        return true;
    }

    public boolean equals(PlayableItem another) {
        if (this.title.equals(another.getTitle()) && this.artist.equals(another.getArtist())
            && this.totalPlayTime == another.totalPlayTime
                && this.endpoint.equals(another.endpoint)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return this.title + "," + this.endpoint + "," + this.lastPlayedTime + ","
                + this.totalPlayTime + "," + this.artist + "," + this.popularity + ","
                + this.playedCounts;
    }

    public int compareTo(PlayableItem o) {
        if (this.playedCounts > o.playedCounts) {
            return 1;
        } else if (this.playedCounts < o.playedCounts) {
            return -1;
        }
        return 0;
        //return this.playedCounts - o.playedCounts;
    }
}
