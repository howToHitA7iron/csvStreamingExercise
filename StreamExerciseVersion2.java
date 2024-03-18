import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * ***Submitting post the 4 hr window.  Added parallel streaming and fixed several bugs***
 * 
 * Full disclosure:  streaming csv files is new to me.  I Googled for a solution and
 * shamelessly borrowed from others.  I could not have done this much otherwise.
 * 
 * I presumed that the developer has awareness of the csv file layout.
 * 
 * If given more time, I'd like to have made the code more readable 
 */
public class StreamExerciseVersion2
{

    public static void main(String[] args)
    throws Exception
    {
        // splits on comma, ignoring commas inside quotes.  yep, stole this too.
        // had no idea how to ignore commas inside of quotes, until i googled it
        final String delimiter = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        String csvFile = null;
        if (args == null || args.length == 0 || args[0] == null || args[0].isEmpty())
        {
            csvFile = "\\java\\eclipse_2021_09_workspace\\test_josege\\src\\StreamExercise.csv";
        }
        else
        {
            csvFile = args[0];	
        }

        // try-with-resources block ensures resources are closed when exited.  found this bit
        // while researching streaming.  did not have time to test if it really does close resources
        try (Stream<String> lines = Files.lines(Path.of(csvFile)).parallel()) {
    	
            // Get field names from csv file
            String[] fields = Files.lines(Path.of(csvFile)).map(fld -> fld.split(delimiter, -1)).findFirst().get();

            lines.skip(1)
              .forEach(line -> {
        		  
            	  // placing inside loop.  guessing this will keep the size of the Map to
            	  // a minimum (i.e. just the size of one row) vs. declaring outside of loop (in previous version)
            	  // where is could have millions of entries on very large csv files.
                  Map<String, Object> fieldsMap = new LinkedHashMap<>();
              	
                  String[] parseRow = line.split(delimiter, -1);
        		  
                  if (parseRow.length != fields.length)
                  {
                	  fieldsMap.put("Error", "number of row fields [" + parseRow.length + 
                              "] does not match header field size [" + fields.length + "]: " + 
                              Arrays.toString(parseRow));
                  }
                  else
                  {
                      // Ordinarily, instead of a Map I'd have used a formal object 
                      // (e.g. Entity, DTO or something similar with getter/setters)
                      // 
                      // I'm guessing there is some purpose to the Map requirement
                      // that I'm just not seeing.  Probably my lack of familiarity
                      // with this part of the Java code base.  But, alas, my 4 hrs is up.
        			  
                      // ID, ZIPCODE, TAX_RATE
                      Integer id = null;
                      Integer zipCode = null;
                      Double taxRate = null;
                      try 
                      {
                          id = parseInteger(parseRow[0]);
                          zipCode = parseInteger(parseRow[4]);
                          taxRate = parseDouble(parseRow[5]);
                          
                          // First Name
                          String firstName = parseRow[1].trim(); 
    					  
                          // Last Name
                          String lastName = parseRow[2].trim();
    					  
                          // Address
                          String address = parseRow[3].trim();
    					  
                          fieldsMap.put(fields[0], id);
                          fieldsMap.put(fields[1], firstName);
                          fieldsMap.put(fields[2], lastName);
                          fieldsMap.put(fields[3], address);
                          fieldsMap.put(fields[4], zipCode);
                          fieldsMap.put(fields[5], taxRate);
                		    
                      } 
                      catch (Exception e) 
                      {
                          // ordinarily would log the full stack
                          System.out.println("Expected a numeric value, and found instead: " + line);
                      }
        			  
                  }
        		  
                  if (!fieldsMap.isEmpty())
                  {
                      System.out.println(fieldsMap);
                  }
                  
              });
    			
        }
	
    }
    
    private static Integer parseInteger(String value)
    {
        Integer i = Integer.valueOf(value);
        return i;
    }
    
    private static Double parseDouble(String value)
    {
        Double d = Double.valueOf(value);
        return d;
    }
   
}