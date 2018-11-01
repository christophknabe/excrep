package exercise;

import static multex.MultexUtil.create;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Class with file services.
 * @author Christoph Knabe
 * @since 2016-10-26
 * @version 2018-11-01
 */
public class FileService {

    /**Cannot count lines of file {0}.*/
    public static class CountFailure extends multex.Failure {}

    /**Counts the lines in the given File.
     * @param file  the path of the file to be analyzed
     * @return Number of lines as determined by BufferedReader.readLine
     * @throws CountFailure  The file could not be read successfully.
     */
    public int countLines(final File file) throws CountFailure {
        //TODO Exercise: Replace the return statement by an implementation in this method.
    	//Test it by class FileServiceTest.
        return predefinedImplementation(file);
    }
    
    
    
    
    
    
    
    
    
    

	private int predefinedImplementation(final File file) {
		try{
        	final BufferedReader in = new BufferedReader(new FileReader(file));
        	for(int i=0;; i++) {
        		final String line = in.readLine();
        		if(line==null) {
        			return i;
        		}
        	}
        }catch(Exception ex){  
        	throw create(CountFailure.class, ex, file);
        }
	}
}
