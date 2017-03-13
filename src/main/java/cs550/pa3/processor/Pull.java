package cs550.pa3.processor;

import cs550.pa3.helpers.Util;

import java.util.Date;

/**
 * Created by Ajay on 3/13/17.
 */
public class Pull  extends  PeerImpl implements Event {

    int TTR;
    Date now;

    public Pull() {
        if(Util.getValue("pull.switch").equals("on")){
            TTR = Integer.parseInt(Util.getValue("TTR"));
            while(true){
                handlePollRequests();
                expiryTTL();
            }
        }else{
            //stop the thread
            System.out.print("Pull switch is off");
        }
    }

    /**
     * 1.PeerServer Recieves Poll request [the origin server ID for the file, TTR, and last-modified-time]
     *         return      [the origin server ID for the file, TTR, and last-modified-time] for a file
     *
     * 2.It Checks request
     */

    private void handlePollRequests() {

    }

    public void expiryTTL(){
       /**
        *
        * TODO Find better algorithm for TTR Expiration
        *
        *
        if(now<TTR+now){
            trigger()
        }else{
            // sleep(TTR)
        }*/

    }



    /**
     * Based on TTR in Infinte loop poll the Watch folder
     *
     */

    @Override
    public void trigger() {
        pull();

    }

    public void pull(){
        /**
         * Pull will do following functions
         * for(all files in cachedFolder){
         *     1.BroadCast Poll Message ()
         * }
         *
         */
    }


}
