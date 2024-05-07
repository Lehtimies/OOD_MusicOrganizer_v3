package model;
import java.util.*;

public class Album extends Observable {
    private Album parentAlbum;
    private String albumName;
    private Set<SoundClip> SoundClips;
    private List<Album> subAlbums;

    /**Constructor that creates the root album |
     * USE ONLY FOR ROOT ALBUM*/
    public Album() {
        this("All Sound Clips", null);
    }

    /** Constructor that creates a new album
     * @param albumName The name of the new album
     * @param parent The parent album */
    public Album(String albumName, Album parent) {
        this.albumName = albumName;
        parentAlbum = parent;
        subAlbums = new ArrayList<>();
        SoundClips = new HashSet<>();
    }
    /** Adds a subAlbum to the album
     * @param newAlbum The album to be added
     * */
    public void addAlbum(Album newAlbum) {
        subAlbums.add(newAlbum);
        newAlbum.parentAlbum = this;
    }

    /** Removes an album
     * @param album The album to be removed*/
    public void removeAlbum(Album album) {
        getParentAlbum().getSubAlbums().remove(album);
        setChanged();
        notifyObservers("albumRemoved");
    }

    /** Adds a SoundClip to the album
     * @param clip The SoundClip to be added */
    public void addSoundClip(SoundClip clip) {
        addSoundClips(Set.of(clip));
    }

    /** Adds one or more SoundClips to the album
     * @param clips The SoundClips to be added */
    public void addSoundClips(Set<SoundClip> clips) {
        SoundClips.addAll(clips);
        if (parentAlbum != null) {
            parentAlbum.addSoundClips(clips);
        }
        setChanged();
        notifyObservers();
    }

    /** Removes a SoundClip from the album
     * @param clip The SoundClip to be removed */
    public void removeSoundClip(SoundClip clip) {
        removeSoundClips(Set.of(clip));
    }

    /** Removes one or more SoundClips from the album
     * @param clips The SoundClips to be removed */
    public void removeSoundClips(Set<SoundClip> clips) {
        SoundClips.removeAll(clips);
        for (Album a : subAlbums) {
            a.removeSoundClips(clips);
        }
        setChanged();
        notifyObservers();
    }

    /** @return - The albumName of the album */
    public String toString() {
        return albumName;
    }

    /** @return - parentAlbum */
    public Album getParentAlbum() {
        return parentAlbum;
    }


    /** @return - Set of SoundClips */
    public Set<SoundClip> getSoundClips() {
        return SoundClips;
    }

    /** @return - List of sub-albums */
    public List<Album> getSubAlbums() {
        return subAlbums;
    }
}
