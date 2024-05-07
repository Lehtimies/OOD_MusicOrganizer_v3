package view;

import controller.MusicOrganizerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import model.Album;
import model.SoundClip;
import java.util.List;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

/**
 * A class that creates a new window displaying the contents of an Album
 */
public class AlbumWindowCreator {
    private MusicOrganizerController controller;

    // Set the controller for the windows
    public void setController(MusicOrganizerController controller) {
        this.controller = controller;
    }

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
                // If the Album has been removed, close the window. If not then update the ListView
                if(arg instanceof String && arg.equals("albumRemoved")) {
                    newWindow.close();
                } else {
                    observableList.setAll(initialAlbum.getSoundClips());
                }
            }
        };

        // Add an event handler to the Window so that sound clips are played when you double-click them
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                List<SoundClip> selectedClip = new ArrayList<>();
                selectedClip.add(listView.getSelectionModel().getSelectedItem());
                if (e.getClickCount() == 2) {
                    controller.playSoundClips(selectedClip);
                }
            }
        });



        // Add the Observer to the Album
        initialAlbum.addObserver(observer);

        // Add the ListView to the new window
        ((Group) scene.getRoot()).getChildren().add(listView);

        // Show the new window
        newWindow.show();
    }
}
