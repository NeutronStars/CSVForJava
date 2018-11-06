package fr.neutronstars.csvfj.error;

public class CSVSizeException extends RuntimeException
{
    public CSVSizeException()
    {

    }

    public CSVSizeException(String message)
    {
        super(message);
    }
}
