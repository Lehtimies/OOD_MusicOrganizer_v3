package controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.stage.FileChooser;
import model.Album;
import model.SoundClip;
import model.SoundClipBlockingQueue;
import model.SoundClipLoader;
import model.SoundClipPlayer;
import view.AlbumWindowCreator;
import view.MusicOrganizerWindow;
import java.io.*;
import java.nio.file.Files;
import javafx.stage.Stage;

public class MusicOrganizerController implements Serializable {

	private MusicOrganizerWindow view;
	private SoundClipBlockingQueue queue;
	private Album root;
	private AlbumWindowCreator albumWindowCreator = new AlbumWindowCreator();
	/**
	 * Adds an album to the Music Organizer
	 */

	public MusicOrganizerController() {

		// Create the root album
		root = new Album();

		// Create the blocking queue
		queue = new SoundClipBlockingQueue();

		// Create a separate thread for the sound clip player and start it

		(new Thread(new SoundClipPlayer(queue))).start();

		// Set the controller for the windows
		albumWindowCreator.setController(this);
	}

	/**
	 * Load the sound clips found in all subfolders of a path on disk. If path is not
	 * an actual folder on disk, has no effect.
	 */
	public Set<SoundClip> loadSoundClips(String path) {
		Set<SoundClip> clips = SoundClipLoader.loadSoundClips(path);

		for (SoundClip clip : clips) {
			root.addSoundClip(clip);
		}

		return clips;
	}

	public void registerView(MusicOrganizerWindow view) {this.view = view;}

	/**
	 * Returns the root album
	 */
	public Album getRootAlbum(){
		return root;
	}

	public void addNewAlbum(){
		String albumName = view.promptForAlbumName();
		if(albumName != null) {
			Album parentAlbum = view.getSelectedAlbum();
			Album newAlbum = new Album(albumName, parentAlbum);
			if (parentAlbum != null) {
				parentAlbum.addAlbum(newAlbum);
				view.onAlbumAdded(parentAlbum, newAlbum);
			}
		}
	}
	
	/**
	 * Removes an album from the Music Organizer
	 */
	public void deleteAlbum(){
		Album selectedAlbum = view.getSelectedAlbum();
		selectedAlbum.removeAlbum(selectedAlbum);
		view.onAlbumRemoved(selectedAlbum);
	}
	
	/**
	 * Adds sound clips to an album
	 */
	public void addSoundClips(){
		List<SoundClip> selectedClips = view.getSelectedSoundClips();
		Album selectedAlbum = view.getSelectedAlbum();
		if (selectedAlbum != null) {
			selectedAlbum.addSoundClips(new HashSet<>(selectedClips));
		}
		view.onClipsUpdated();
	}
	
	/**
	 * Removes sound clips from an album
	 */
	public void removeSoundClips(){
		List<SoundClip> selectedClips = view.getSelectedSoundClips();
		Album selectedAlbum = view.getSelectedAlbum();
		if (!selectedAlbum.equals(root)) {
			selectedAlbum.removeSoundClips(new HashSet<>(selectedClips));
			view.onClipsUpdated();
		}
	}
	
	/**
	 * Puts the selected sound clips on the queue and lets
	 * the sound clip player thread play them. Essentially, when
	 * this method is called, the selected sound clips in the 
	 * SoundClipTable are played.
	 */
	public void playSoundClips(){
		List<SoundClip> l = view.getSelectedSoundClips();
		playSoundClips(l);
	}

	/** Takes a specific list of sound clips and plays them
	 * @param clips - the list of sound clips to be played
	 */
	public void playSoundClips(List<SoundClip> clips){
		queue.enqueue(clips);
		for(int i=0;i<clips.size();i++) {
			view.displayMessage("Playing " + clips.get(i));
		}
	}
	/**
	 * Saves the current view to a file
	 */
	public void saveAs(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save As");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML", "*.html"), new FileChooser.ExtensionFilter("Searialize", "*.ser"));
		File file = fileChooser.showSaveDialog(primaryStage);
		String fileExtension = file.getName().substring(file.getName().lastIndexOf("."));
		if (file != null) {
			if (fileExtension.equals(".html")) {
				file = createHTML(root, file);
				view.displayMessage("Successfully saved to " + file.getName());
			} else if (fileExtension.equals(".ser")) {
				saveHierarchy(file);
				view.displayMessage("Successfully saved to " + file.getName());
			} else {
				view.displayMessage("Invalid file type.");
			}
		}
	}

	/**
	 * Saves the hierarchy to a .ser file
	 * @param file
	 */
	public void saveHierarchy(File file) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
			out.writeObject(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads a hierarchy from a .ser file
	 */
	public void loadHierarchy() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Hierarchy");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Searialize", "*.ser")); // only allow loading .ser files
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			view.displayMessage("Loading " + file.getName());
			try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
				root = (Album) in.readObject();
				view.updateTreeView(root);
				view.onClipsUpdated();
				view.displayMessage("Successfully loaded hierarchy from " + file.getName());
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates an HTML file from the album hierarchy
	 * @param album
	 * @param file
	 * @return the created HTML file
	 */
	public File createHTML(Album album, File file) {
		String html = createAlbumHTML(album);
		File htmlFile = new File("HTMLTemplate/template.html"); // template file
		String htmlString = getHtmlString(htmlFile); // get the template as a string
		String title = "Music Organizer";
		htmlString = htmlString.replace("$title", title);
		htmlString = htmlString.replace("$list", html); // replace the list with the album hierarchy
		try {
			Files.write(file.toPath(), htmlString.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * Gets the contents of an HTML file as a string
	 * @param html
	 * @return the HTML file as a string
	 */
	public String getHtmlString(File html){
		String htmlString = "";
		try {
			htmlString = new String(Files.readAllBytes(html.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlString;
	}

	/**
	 * Creates an HTML representation of the album hierarchy
	 * @param album
	 * @return the HTML representation of the album hierarchy
	 */
	public String createAlbumHTML(Album album){
		String html = "<ul>"; // start of the list
		for (Album subAlbum : album.getSubAlbums()){
			html += "<li><b>" + subAlbum.toString() + "</b></li>"; // add a list item for every subAlbum
			html += createAlbumHTML(subAlbum); // add the subAlbum's subAlbums
		}
		for (SoundClip clip : album.getSoundClips()){
			html += "<li>" + clip.toString() + "</li>"; // add a list item for every SoundClip
		}
		html += "</ul>"; // end of the list
		return html;
	}
	/**
	 * Creates a new window displaying the contents of an Album
	 * @param album
	 */
	public void createNewWindow(Album album) {
		albumWindowCreator.createWindow(album);
	}
}
