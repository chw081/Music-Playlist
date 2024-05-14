import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The drivem class, they are required to do part of this file
 */
public class MyPlayer {

    public static MusicDatabase data = new MusicDatabase();

    public static ArrayList<Playlist> playlists = new ArrayList<>();// one player might have multiple playlist profile
    private static int cur = 0;
    private static int playingMode = 0;

    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        playlists.add(new Playlist());

        while (data.size() == 0) {
            System.out.println(
                    "********************\nOption Lists: \nDatabase Management:\n\t[0] (for testing) Initialize the music database with a single song\n\t[1] Initialize the music database\nType the number you select after star line or type 12 to close the application\n********************");
            switch (Integer.parseInt(scnr.nextLine())) {// required to use nextLine to pass autograder
                case 0:
                    System.out.println(
                            "Type info using the following format and press enter: name,artist,duration,popularity,endpoint");
                    String[] temp = scnr.nextLine().trim().split(",");
                    if (temp.length < 5) {
                        System.out.println("Invalid option");
                        break;
                    }
                    data.addSongs(temp[0], temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), temp[4]);
                    break;
                case 1:
                    try {
                        if (!data.addSongs(new File("songs.csv")))
                            System.out.println("Unable to open and parse the file");
                    } catch (Exception e) {
                        System.out.println("Invalid option");
                    }
                    break;
                case 12:
                    System.out.println("Exiting");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }

        while (true) {
            System.out.println("********************\nCurrent Playlist: " + playlists.get(cur).getName());
            System.out.println(
                    "\t[1] Play\n\t[2] Play the previous song\n\t[3] Switch playback mode\n\t[4] Print the current playlist\n\t[5] Add a song from the database to the current playlist\n\t[6] Remove a song from the current playlist");
            System.out.println(
                    "PlayList Management:\n\t[7] Create a new playlist\n\t[8] Switch to another playlist\nDatabase Management:\n\t[9] (for testing) add a single song to the music database \n\t[10] Add multiple songs to the music database with another file\n\t[11] Print the database\nType the number you select after star line or type 12 to close the application\n*********************");
            switch (Integer.parseInt(scnr.nextLine())) {
                case 1:// \n[5] Play
                    System.out.println("Enter seconds to play: ");
                    playlists.get(cur).play(Integer.parseInt(scnr.nextLine()));
                    break;
                case 2:
                    playlists.get(cur).goBack();
                    break;
                case 3:// \n[6] Switch playback mode
                    System.out.println(
                            "Switch playback mode: 0 - normal, 1 - random, 2 - most frequently, 3 - recommedation");
                    int mode = Integer.parseInt(scnr.nextLine());
                    if (in(mode, 0, 3)) {
                        playingMode = mode;
                        if (in(playingMode, 0, 2)) {
                            playlists.get(cur).switchPlayingMode(playingMode);
                        } else {
                            // recommendation case
                            playlists.get(cur).switchPlayingMode(playingMode);
                            ArrayList<PlayableItem> recommendationMusic = data
                                    .getRecommendedSongs(playlists.get(cur).getFiveMostPopular());
                            // recommendation more likes an adding case instead of playing mode
                            if (recommendationMusic != null)
                                playlists.get(cur).addPlayableItem(recommendationMusic);
                        }
                    } else
                        System.out.println("Invalid option");
                    break;
                case 4:
                    System.out.println(playlists.get(cur).showPlaylistStatus());
                    break;
                case 5:// \n[4] Search and add to current playlist
                    System.out.println(
                            "Enter the search mode: \n\t[0] Search by partial song name\n\t[1] Search by partial artist name\n\t[2] Search by popularity based on threshold");

                    // user is able to add one or all in the searching result to current playlist
                    switch (Integer.parseInt(scnr.nextLine())) {
                        case 0:
                            System.out.println("Enter partial song name to search: ");
                            selection(data.partialSearchBySongName(scnr.nextLine().trim()), scnr);
                            break;
                        case 1:
                            System.out.println("Enter partial artist name to search: ");
                            selection(data.partialSearchByArtistName(scnr.nextLine().trim()), scnr);
                            break;
                        case 2:
                            System.out.println(
                                    "Enter threshold to search, the program will find all musics larger than the given threshold: ");
                            selection(data.searchHighestPopularity(Integer.parseInt(scnr.nextLine().trim())),
                                    scnr);
                            break;
                        default:
                            System.out.println("Invalid option");
                            break;
                    }
                    break;
                case 6:// remove one from playlist
                    if (playlists.get(cur).size() != 0) {
                        System.out.println(playlists.get(cur).showPlaylistStatus());
                        System.out.println("Type the item you want to remove: ");
                        if (playlists.get(cur).removePlayableItem(Integer.parseInt(scnr.nextLine())))
                            System.out.println("Successufully removed");
                        else
                            System.out.println("Unsuccessfully removed");
                    } else
                        System.out.println("No item to remove");
                    break;
                case 7:// new playlist
                    cur = playlists.size();
                    System.out.println("Type name for the new playlist: ");
                    playlists.add(new Playlist(scnr.nextLine().trim()));
                    break;
                case 8:// [3] Switch to another playlist
                    System.out.println("We have the following playlist: ");
                    int counter = 0;
                    for (Playlist a : playlists) {
                        System.out.println("[" + counter++ + "] " + a.toString());
                    }
                    System.out.println("Select the number you want to use: ");
                    int toSwitch = Integer.parseInt(scnr.nextLine());
                    if (in(toSwitch, 0, counter - 1)) {
                        cur = toSwitch;
                        if (in(playingMode, 0, 2)) {
                            playlists.get(cur).switchPlayingMode(playingMode);
                        } else {
                            // recommendation case: retain its original playing order
                            // TODO: need consider base case here
                            playlists.get(cur).switchPlayingMode(playingMode);
                            ArrayList<PlayableItem> recommendationMusic = data
                                    .getRecommendedSongs(playlists.get(cur).getFiveMostPopular());
                            // recommendation more likes an adding case instead of playing mode
                            if (recommendationMusic != null)
                                playlists.get(cur).addPlayableItem(recommendationMusic);
                        }

                    } else
                        System.out.println("Invalid option");
                    break;
                case 9:
                    System.out.println(
                            "Type info using the following format and press enter: name,artist,duration,popularity,endpoint");
                    String[] temp = scnr.nextLine().trim().split(",");
                    if (temp.length < 5) {
                        System.out.println("Invalid option");
                        break;
                    }
                    data.addSongs(temp[0], temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), temp[4]);
                    break;
                case 10:
                    System.out.println("Input file path: ");
                    try {
                        if (!data.addSongs(new File(scnr.nextLine().trim())))
                            System.out.println("Unable to open and parse the file");
                    } catch (Exception e) {
                        System.out.println("Invalid option");
                    }
                    break;

                case 11:
                    int count = 0;
                    for (PlayableItem a : data.searchHighestPopularity(0)) {
                        System.out.println("[" + count++ + "] " + a.toString());
                    }
                    break;
                case 12:
                    System.out.println("Exiting");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }

    }

    public static boolean in(int x, int a, int b) {
        return x >= a && x <= b;
    }

    public static void selection(ArrayList<PlayableItem> lists, Scanner scnr) {
        if (lists == null) {
            System.out.println("No valid result");
            return;
        }
        if (lists.size() == 0) {
            System.out.println("No valid result");
            return;
        }
        for (int i = 0; i < lists.size(); i++) {
            System.out.println("[" + i + "] " + lists.get(i).toString());
        }
        System.out.println("[" + lists.size() + "] Add all");
        System.out.print("Select an item to add: ");
        int searchCommand = Integer.parseInt(scnr.nextLine());
        if (in(searchCommand, 0, lists.size() - 1))
            playlists.get(cur).addPlayableItem(lists.get(searchCommand));
        else if (searchCommand == lists.size())
            playlists.get(cur).addPlayableItem(lists);
        else
            System.out.println("Invalid option");
    }
}