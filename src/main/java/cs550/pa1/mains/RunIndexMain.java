package cs550.pa1.mains;

import java.io.IOException;

/**
 * Created by Ajay on 1/25/17.
 */
public class RunIndexMain {

    public static void main(String[] args) throws IOException {

        /*if (args.length != 1) {
            System.err.println("Usage: java RunIndexMain <port number>");
            System.err.println("example: java RunIndexMain 8080");
            System.exit(1);
        }



        int portNumber = Integer.parseInt(args[0]);*/

       // Util.LOGGER.info("IndexServer Started ");


        IndexImpl indexServer = new IndexImpl();



    }
}
