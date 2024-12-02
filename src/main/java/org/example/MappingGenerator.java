package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MappingGenerator {

    public static List<Mapping> readMappingsFromCsv(String filePath) throws IOException, CsvValidationException {
        Map<String, Mapping> mappingRegistry = new HashMap<>();
        List<Mapping> rootMappings = new ArrayList<>();

        // Create a virtual root mapping
        Mapping virtualRoot = new Mapping(null, "Root", false, true, "object");
        mappingRegistry.put("Root", virtualRoot);
        rootMappings.add(virtualRoot);

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = new ArrayList<>();
            String[] row;

            // Read all rows from the CSV file
            boolean isHeader = true;
            while ((row = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip the header row
                    continue;
                }
                rows.add(row);
            }

            // Process mappings iteratively
            processMappingsIteratively(rows, mappingRegistry, virtualRoot);
        }

        // Debug: Print the final mapping hierarchy
        printMappingHierarchy(virtualRoot, 0);

        // Return child mappings of the virtual root
        return virtualRoot.getChildMappings();
    }

    private static void processMappingsIteratively(List<String[]> rows, Map<String, Mapping> mappingRegistry, Mapping virtualRoot) {
        boolean changesMade;

        do {
            changesMade = false;

            for (String[] row : rows) {
                String jPath = row[0];
                String xPath = row[1];
                boolean isList = "Yes".equalsIgnoreCase(row[2]);
                boolean isRoot = "Yes".equalsIgnoreCase(row[3]);
                String type = isList ? "list" : "object";
                String parentXPath = row[5] != null ? row[5].trim() : "";

                if (mappingRegistry.containsKey(xPath)) {
                    // Skip already processed mappings
                    continue;
                }

                if (parentXPath.isEmpty() || mappingRegistry.containsKey(parentXPath)) {
                    // Process the mapping if parent exists or it's a root element
                    Mapping mapping = new Mapping(jPath, xPath, isList, isRoot, type);
                    if (parentXPath.isEmpty()) {
                        // Link top-level mappings to the virtual root
                        System.out.println("Linking top-level mapping to virtual root: " + xPath);
                        virtualRoot.addChildMapping(mapping);
                    } else {
                        Mapping parentMapping = mappingRegistry.get(parentXPath);
                        System.out.println("Adding child mapping: " + xPath + " to parent: " + parentXPath);
                        parentMapping.addChildMapping(mapping);
                    }

                    // Register the mapping
                    mappingRegistry.put(xPath, mapping);
                    changesMade = true;
                } else {
                    System.out.println("Parent XPath not resolved yet: " + parentXPath + " for " + xPath);
                }
            }
        } while (changesMade);
    }

    private static void printMappingHierarchy(Mapping mapping, int level) {
        String indent = " ".repeat(level * 2);
        System.out.println(indent + "Mapping: " + mapping.getXPath());
        for (Mapping child : mapping.getChildMappings()) {
            printMappingHierarchy(child, level + 1);
        }
    }
}
