package cs550.pa3.processor;

/**
 * Created by Ajay on 3/13/17.
 */
public class Push extends  PeerImpl implements Event {


    @Override
    public void trigger() {
            // BroadCast
    }

    public void update(){
       this.trigger();
    }




    /**
     *  1. Origin server broadcasts an "Invalidate" message for the file.
     *  2. The invalidate message propagates  exactly like a "query" message.
     *  3.
     *
     */
    public void broadCast(){

        // We need to use the same logic as search() in peerImpl but donot consider the TTL
        /*Socket sock = null;

        try{
            for (Host neighbour:neighbours) {
                sock = new Socket(neighbour.getUrl(),neighbour.getPort());
                PrintWriter out = new PrintWriter( sock.getOutputStream(), true );
                out.println(Constants.QUERY + " " + query_id + " " + fileName + " " + host.address() + " " + Integer.toString(ttl));
                out.close();
                if(!seenMessages.containsKey(query_id)){
                    List addr = new ArrayList<String>();
                    this.seenMessages.put(query_id,addr);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }*/
    }
}
