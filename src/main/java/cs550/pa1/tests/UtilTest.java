package cs550.pa1.tests;

import cs550.pa1.helpers.Util;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ajay on 1/25/17.
 */
public class UtilTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void searchInFile(){

        List<String> results = Util.searchInFile("test1.txt");
        int  count=0;
        for(String r : results){
            if(r.contains("test1.txt")) count++;
        }
        assertEquals(3,results.size());
    }




    @Test
    public void searchInFile2(){

        List<String> results = Util.searchInFile("test");
        int  count=0;
        for(String r : results){
            if(r.contains("test")) count++;
        }
        assertEquals(1,results.size());
    }



}
