package cs550.pa1.servers;

import java.util.List;

/**
 * Created by Ajay on 1/25/17.
 */
public interface Index {
    public abstract List<String> lookup(String text);
    public boolean registry(String loc,String portRequested,String type);

}
