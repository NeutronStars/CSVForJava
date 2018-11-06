package fr.neutronstars.csvfj;

import java.io.File;
import java.util.function.Consumer;

public abstract class CSV
{
    public abstract Object get(int column, int line);
    public abstract String getString(int column, int line);
    public abstract int getInt(int column, int line);

    public abstract int getColumns();
    public abstract int getLines();

    public abstract void set(int column, int line, Object object);

    public abstract void forEach(int index, CSVZone zone, Consumer<Object> consumer);
    public abstract void forEach(CSVConsumer<Integer, Integer, Object> csvConsumer);

    public abstract void save(File file);
}
