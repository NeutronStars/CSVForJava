package fr.neutronstars.csvfj;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author NeutronStars
 */

public class CSVParser
{
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * Create CSV Object.
     * @param file
     * @return
     * @throws IOException
     */
    public static CSV parse(File file) throws IOException
    {
        return parse(file, DEFAULT_SEPARATOR);
    }

    /**
     *
     * @param file
     * @param separator
     * @return
     * @throws IOException
     */

    public static CSV parse(File file, char separator) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));

        int line = 0;
        Map<Integer, List<Object>> map = new LinkedHashMap<>();
        int columnMax = 0;

        boolean in = false;
        List<Object> objects = new LinkedList<>();
        StringBuilder builder = new StringBuilder();

        while (reader.ready())
        {
            String lineString = reader.readLine();

            if(!in) line++;
            else builder.append("\n");

            char[] chars = lineString.toCharArray();

            for(int i = 0; i < chars.length; i++)
            {
                if(chars[i] == separator && !in)
                {
                    objects.add(builder.toString());
                    builder = new StringBuilder();
                    continue;
                }

                builder.append(chars[i]);
                if(chars[i] == '"') in = !in;
            }

            if(!in)
            {
                objects.add(builder.toString());
                builder = new StringBuilder();
                if(columnMax < objects.size()) columnMax = objects.size();
                map.put(line, objects);
                objects = new LinkedList<>();
            }
        }

        if(in)
            throw new IOException("Format CSV is not valid.");

        CSV csv = new SimpleCSV(columnMax, line);

        for(Map.Entry<Integer, List<Object>> entry : map.entrySet())
        {
            for(int i = 0; i < entry.getValue().size(); i++)
            {
                String str = entry.getValue().get(i).toString();
                if(str.startsWith("\"") && str.endsWith("\""))
                {
                    if(str.length() > 2)
                    {
                        builder = new StringBuilder().append(str);
                        str = builder.substring(1, builder.length()-1);
                    }else if(str.length() == 2) str = "";
                }
                csv.set(i, entry.getKey() - 1, str.replace("\"\"", "\""));
            }
        }

        return csv;
    }

    /**
     *
     * @param array
     * @return
     * @throws IOException
     */

    public static CSV parse(JSONArray array) throws IOException {
        List<String> keys = new ArrayList<>();

        for(int i = 0; i < array.length(); i++)
        {
            Object object = array.getJSONObject(i);
            if(!(object instanceof JSONObject))
                throw new IOException("Format JSONArray is not valid.");
            JSONObject jsonObject = (JSONObject)object;
            for(String key : jsonObject.keySet())
                if(!keys.contains(key)) keys.add(key);
        }

        CSV csv = new SimpleCSV(keys.size(), array.length()+1);

        for(int i = 0; i < keys.size(); i++)
            csv.set(i, 0, keys.get(i));

        for(int i = 0; i < array.length(); i++)
        {
            JSONObject jsonObject = array.getJSONObject(i);
            for(String key : jsonObject.keySet())
                csv.set(keys.indexOf(key), i+1, jsonObject.get(key));
        }

        return csv;
    }

    /**
     *
     * @param csv
     * @param zone
     * @return
     */

    public static JSONArray convertToJSON(CSV csv, CSVZone zone)
    {
        JSONArray array = new JSONArray();
        JSONObject object;
        String[] keys;
        switch (zone)
        {
            case COLUMN:
                keys = new String[csv.getColumns()];
                for(int i = 0; i < keys.length; i++)
                    keys[i] = csv.getString(i, 0);

                for(int y = 1; y < csv.getLines(); y++)
                {
                    object = new JSONObject();
                    for(int x = 0; x < keys.length; x++)
                        object.put(keys[x], csv.get(x, y));
                    array.put(object);
                }
                break;
            case LINE:
                keys = new String[csv.getLines()];
                for(int i = 0; i < keys.length; i++)
                    keys[i] = csv.getString(0, 1);

                for(int y = 1; y < csv.getColumns(); y++)
                {
                    object = new JSONObject();
                    for(int x = 0; x < keys.length; x++)
                        object.put(keys[x], csv.get(y, x));
                    array.put(object);
                }
                break;
        }

        return array;
    }
}
