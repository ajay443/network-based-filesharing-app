package cs550.pa3.helpers;

/**
 * Created by Ajay on 3/13/17.
 */
public class PeerFile {
    boolean original;
    String name;
    int TTR;

    public PeerFile(boolean original, String name, int TTR) {
        this.original = original;
        this.name = name;
        this.TTR = TTR;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTTR() {
        return TTR;
    }

    public void setTTR(int TTR) {
        this.TTR = TTR;
    }
}
