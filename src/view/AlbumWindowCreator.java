package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Album;
import model.SoundClip;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

/**
 * A class that creates a new window displaying the contents of an Album
 */
public class AlbumWindowCreator {
    /**
     * Creates a new window displaying the contents of an Album
     * @param initialAlbum The Album to display in the window
     */
    public void createWindow(Album initialAlbum) {
        // Create a new window
        Stage newWindow = new Stage();
        Scene scene = new Scene(new Group(), 250, 250);
        newWindow.setScene(scene);

        // Set the title of the window to the album's name
        newWindow.setTitle(initialAlbum.toString());

        // Create a ListView and have it reflect the ObservableList of the album's content
        ListView<SoundClip> listView = new ListView<>();
        ObservableList<SoundClip> observableList = FXCollections.observableArrayList(new HashSet<>(initialAlbum.getSoundClips()));
        listView.setItems(observableList);

        // Create an Observer that updates the ListView when the Album changes
        Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(arg instanceof String && arg.equals("albumRemoved")) {
                    // TODO Fix issue where the parent album's window is the one that closes instead of the removed album's window
                    newWindow.close();
                } else {
                    observableList.setAll(initialAlbum.getSoundClips());
                }
            }
        };

        // Add the Observer to the Album
        initialAlbum.addObserver(observer);
        System.out.println("Observer added to album"); // Test to see if the observer is added

        // Add the ListView to the new window
        ((Group) scene.getRoot()).getChildren().add(listView);

        // Show the new window
        newWindow.show();
    }
}
