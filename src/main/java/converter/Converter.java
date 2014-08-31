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
        try
        {
            String path = "/Users/dmitry/Downloads/fb2.Flibusta.Net/d.fb2-172703-173908";
            log_.info("Converting with path: {}", path);
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
            // determine encoding
            BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()));
            String line = reader.readLine();
            int from = line.indexOf("encoding") + "encoding=".length() + 1;
            int to = line.indexOf("\"", from);
            String encoding = line.substring(from, to);
            reader.close();

            // read it now
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath.toFile()), encoding));
            String bookTitle = null;
            while ((line = reader.readLine()) != null)
            {
                if (bookTitle == null)
                {
                    bookTitle = findTagValue(line, "book-title");
                }
            }

            if (bookTitle == null)
            {
                log_.info(filePath.toFile().getName());

//                reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath.toFile()), encoding));
//                while ((line = reader.readLine()) != null)
//                {
//                    String trimmedLine = line.trim();
//                    if (trimmedLine.length() < 1000 && !trimmedLine.startsWith("<p>"))
//                    {
//                        log_.info("{}", trimmedLine);
//                    }
//                }
//                System.exit(0);
            }
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

    private String findTagValue(String line, String tag)
    {
        String fullTag = "<" + tag + ">";
        int index = line.indexOf(fullTag);

        if (index < 0)
        {
            return null;
        }

        int beginIndex = index + fullTag.length();
        int endIndex = line.indexOf("</", beginIndex);
        if (endIndex < 0)
        {
            log_.info("No terminating </");
            return null;
        }

        return line.substring(beginIndex, endIndex);
    }

    public static void main(String[] args)
    {
        new Converter().convert();
    }
}
