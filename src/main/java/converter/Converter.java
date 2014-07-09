package converter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Converter
{
    private Logger log_ = LoggerFactory.getLogger(Converter.class);

    public Converter()
    {
    }

    private void convert()
    {
        log_.info("Converting");
        try
        {
            String path = "/Users/dmitry/Downloads/fb2.Flibusta.Net/d.fb2-172703-173908";
            Files.walk(Paths.get(path)).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".fb2"))
                {
//                    log_.info("{}", filePath.getFileName());
                    convert(filePath);
                }
            });

        }
        catch (Exception e)
        {
            log_.error("Failed to convert", e);
        }
    }

    private void convert(Path filePath)
    {
//        log_.info("Converting {}", filePath);
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()));
            String line = reader.readLine();
//            log_.info("XML: " + xmlLine);

            int from = line.indexOf("encoding") + "encoding=".length() + 1;
            int to = line.indexOf("\"", from);
            String encoding = line.substring(from, to);

            reader.close();

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath.toFile()), encoding));

            String bookTitle = "";
            while ((line = reader.readLine()) != null)
            {
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("<book-title"))
                {
                    bookTitle = trimmedLine.substring("<book-title>".length(), trimmedLine.indexOf("</"));
                }
            }
            log_.info(bookTitle);
        }
        catch (UnsupportedEncodingException uee)
        {
            log_.info("Unsupported encoding for {}", filePath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        new Converter().convert();
    }
}
