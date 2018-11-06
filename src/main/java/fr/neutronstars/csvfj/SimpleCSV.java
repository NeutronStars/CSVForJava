package fr.neutronstars.csvfj;

import fr.neutronstars.csvfj.error.CSVSizeException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class SimpleCSV extends CSV
{
    private final Object[][] objects;

    public SimpleCSV(Object[][] objects)
    {
        if(objects.length == 0 || objects[0].length == 0)
            throw new CSVSizeException("Your CSV must have at least 1 column and 1 line.");
        this.objects = objects;
    }

    public SimpleCSV(int column, int line)
    {
        if(column == 0 || line == 0)
            throw new CSVSizeException("Your CSV must have at least 1 column and 1 line.");
        this.objects = new Object[column][line];
    }

    @Override
    public int getColumns()
    {
        return objects.length;
    }

    @Override
    public int getLines()
    {
        return objects[0].length;
    }

    @Override
    public Object get(int column, int line)
    {
        if(column >= objects.length || line >= objects[0].length)
            throw new IndexOutOfBoundsException("The column "+column+" and/or the line "+line+" is outside the borders of the table.");
        return objects[column][line] != null ? objects[column][line] : "";
    }

    @Override
    public String getString(int column, int line)
    {
        return get(column, line).toString();
    }

    @Override
    public int getInt(int column, int line)
    {
        Object object = get(column, line);
        if(object instanceof Number) return (int) object;

        if(object instanceof String)
        {
            if(((String) object).length() == 0) return 0;
            try {
                return new BigDecimal((String) object).intValue();
            }catch (NumberFormatException nfe){}
        }

        throw new NumberFormatException("The column "+column+" and line "+line+" is not Number");
    }

    @Override
    public void set(int column, int line, Object object)
    {
        if(column >= objects.length || line >= objects[0].length)
            throw new IndexOutOfBoundsException("The column "+column+" and/or the line "+line+" is outside the borders of the table.");
        this.objects[column][line] = object;
    }

    @Override
    public void forEach(int index, CSVZone zone, Consumer<Object> consumer)
    {
        switch (zone)
        {
            case COLUMN:
                if(getColumns() >= index)
                    throw new IndexOutOfBoundsException("The column "+index+" is outside the borders of the table.");
                for(int i = 0; i < getLines(); i++)
                    consumer.accept(get(index, i));
                break;
            case LINE:
                if(getLines() >= index)
                    throw new IndexOutOfBoundsException("The line "+index+" is outside the borders of the table.");
                for(int i = 0; i < getColumns(); i++)
                    consumer.accept(get(i, index));
                break;
        }
    }

    @Override
    public void forEach(CSVConsumer<Integer, Integer, Object> csvConsumer)
    {
        for(int y = 0; y < getLines(); y++)
            for(int x = 0; x < getColumns(); x++)
                csvConsumer.accept(x, y, get(x, y));
    }

    @Override
    public void save(File file)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            for(int y = 0; y < getLines(); y++)
            {
                if(y!=0) writer.newLine();
                for(int x = 0; x < getColumns(); x++)
                {
                    if(x != 0) writer.write(CSVParser.DEFAULT_SEPARATOR);
                    String line = getString(x, y).replace("\"", "\"\"");
                    if(line.contains(String.valueOf(CSVParser.DEFAULT_SEPARATOR)))
                        line = "\""+line+"\"";
                    writer.write(line);
                }
            }
            writer.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
