package com.graduatebetter.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class FileReaderService {

    private final ResourceLoader resourceLoader;

    public FileReaderService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<String> readFilesInDirectory(String directoryPath) throws IOException {
        // Load resources matching the pattern
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                .getResources("classpath:" + directoryPath + "/*");

        // Process each resource
        for (Resource resource : resources) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Process each line in the file
                    System.out.println(line);
                }
            }
        }

        // Extract file names from resources
        List<String> fileNames = Arrays.asList(resources).stream()
                .map(Resource::getFilename)
                .toList();

        return fileNames;
    }
}