import fr.neutronstars.csvfj.CSV;
import fr.neutronstars.csvfj.CSVParser;
import fr.neutronstars.csvfj.CSVZone;
import fr.neutronstars.csvfj.json.JSONWriter;

import java.io.File;
import java.io.IOException;

public class Main
{
    public static void main(String... args)
    {
        CSV csv = null;
        try {
            csv = CSVParser.parse(new File("Test.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (JSONWriter writer = new JSONWriter(new File("TestJSON.json"))){
            writer.write(CSVParser.convertToJSON(csv, CSVZone.COLUMN));
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        /*
        try {
            JSONArray array = new JSONReader(new File("TestJSON.json")).toJSONArray();
            csv = CSVParser.parse(array);

            csv.forEach((column, line, object) -> {
                if(line != LINE)
                {
                    System.out.println();
                    LINE=line;
                }
                System.out.print("| "+object+" |");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        csv.save(new File("TESTCSV.csv"));
    }

    public static int LINE = 0;
}
