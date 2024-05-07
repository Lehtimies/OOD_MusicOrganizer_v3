package controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Album;
import model.SoundClip;
import model.SoundClipBlockingQueue;
import model.SoundClipLoader;
import model.SoundClipPlayer;
import view.AlbumWindowCreator;
import view.MusicOrganizerWindow;

public class MusicOrganizerController {

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
				view.onAlbumAdded(newAlbum);
			}
		}
	}
	
	/**
	 * Removes an album from the Music Organizer
	 */
	public void deleteAlbum(){
		Album selectedAlbum = view.getSelectedAlbum();
		selectedAlbum.removeAlbum(selectedAlbum);
		view.onAlbumRemoved();
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
	 * Creates a new window displaying the contents of an Album
	 * @param album
	 */
	public void createNewWindow(Album album) {
		albumWindowCreator.createWindow(album);
	}
}
