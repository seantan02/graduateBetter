package com.graduatebetter.util;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReaderHelper {
    public static List<String> listOutFileNamesInADir(String dirPath) throws IOException, URISyntaxException {
        //result
        List<String> result = new ArrayList<String>();
        // Access the directory from the classpath
        ClassPathResource resource = new ClassPathResource("data");

        // Get the URL of the resource
        URL url = resource.getURL();

        // Use Paths.get to obtain a Path object
        Path path = Paths.get(url.toURI());

        // List the filenames in the directory
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path entry : directoryStream) {
                result.add(entry.getFileName().toString());
            }
        }
        return result;
    }

    public static String getFilePath(String filePath){
        // Create a Path object representing the folder
        Path folder = Paths.get(filePath);

        // Print the folder path
        return folder.toAbsolutePath().toString();
    }
}
