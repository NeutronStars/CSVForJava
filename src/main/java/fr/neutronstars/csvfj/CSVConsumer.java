package fr.neutronstars.csvfj;

public interface CSVConsumer<T1, T2, T3> {
    void accept(T1 column, T2 line, T3 object);
}
