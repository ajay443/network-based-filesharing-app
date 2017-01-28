package cs550.indexserver;


/**
 * Created by Ajay on 1/27/17.
 */
public class IndexServerTest {

    public static void main(String[] args) {
        IndexServerBrain i = new IndexServerBrain();
        new LookUp(i);
        new Registry(i);

    }
}
