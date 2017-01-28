package threads.poc;

/**
 * Created by Ajay on 1/27/17.
 */
// Create a second thread by extending Thread

class ExtendThread {
    public static void main(String args[]) {
        new NewThread2(); // create a new thread
        try {
            for (int i = 5; i > 0; i--) {
                System.out.println("Main Thread: " + i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }
        System.out.println("Main thread exiting.");
    }
}