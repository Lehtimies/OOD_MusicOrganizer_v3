package view;


import controller.MusicOrganizerController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ButtonPaneHBox extends HBox {

	private MusicOrganizerController controller;
	private MusicOrganizerWindow view;
	
	private Button newAlbumButton;
	private Button deleteAlbumButton;
	private Button addSoundClipsButton;
	private Button removeSoundClipsButton;	
	private Button playButton;
	private Button newWindowButton;
	public static final int BUTTON_MIN_WIDTH = 150;

	
	
	public ButtonPaneHBox(MusicOrganizerController contr, MusicOrganizerWindow view) {
		super();
		this.controller = contr;
		this.view = view;
		
		newAlbumButton = createNewAlbumButton();
		this.getChildren().add(newAlbumButton);

		deleteAlbumButton = createDeleteAlbumButton();
		this.getChildren().add(deleteAlbumButton);
		
		addSoundClipsButton = createAddSoundClipsButton();
		this.getChildren().add(addSoundClipsButton);
		
		removeSoundClipsButton = createRemoveSoundClipsButton();
		this.getChildren().add(removeSoundClipsButton);
		
		playButton = createPlaySoundClipsButton();
		this.getChildren().add(playButton);

		newWindowButton = createNewWindowButton();
		this.getChildren().add(newWindowButton);

	}
	
	/*
	 * Each method below creates a single button. The buttons are also linked
	 * with event handlers, so that they react to the user clicking on the buttons
	 * in the user interface
	 */

	private Button createNewAlbumButton() {
		Button button = new Button("New Album");
		button.setTooltip(new Tooltip("Create new sub-album to selected album"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
			
				controller.addNewAlbum();
			}
			
		});
		return button;
	}
	
	private Button createDeleteAlbumButton() {
		Button button = new Button("Remove Album");
		button.setTooltip(new Tooltip("Remove selected album"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				controller.deleteAlbum();
			}
			
		});
		return button;
	}
	
	private Button createAddSoundClipsButton() {
		Button button = new Button("Add Sound Clips");
		button.setTooltip(new Tooltip("Add selected sound clips to selected album"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				controller.addSoundClips();
			}
			
		});
		return button;
	}
	
	private Button createRemoveSoundClipsButton() {
		Button button = new Button("Remove Sound Clips");
		button.setTooltip(new Tooltip("Remove selected sound clips from selected album"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				controller.removeSoundClips();
			}
			
		});
		return button;
	}
	
	private Button createPlaySoundClipsButton() {
		Button button = new Button("Play Sound Clips");
		button.setTooltip(new Tooltip("Play selected sound clips"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				controller.playSoundClips();
			}
			
		});
		return button;
	}

	private Button createNewWindowButton() {
		Button button = new Button("New Window");
		button.setTooltip(new Tooltip("Open the album in a new window"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				// TODO add code later
				// Create a new stage (window)
				Stage newWindow = new Stage();

				// Create a new scene for the new window
				Scene scene = new Scene(new Group(), 450, 250);

				// Set the scene for the new window
				newWindow.setScene(scene);

				// Set the title for the new window
				newWindow.setTitle(view.getSelectedAlbum().toString());

				// Display the content of the album here
				// TODO add code later

				// Show the new window
				newWindow.show();
			}

		});
		return button;
	}
}
