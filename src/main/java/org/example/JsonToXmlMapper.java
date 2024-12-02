package org.example;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.List;

public class JsonToXmlMapper {
    public static String transformJsonToXml(String jsonString, List<Mapping> mappings) throws Exception {
        // Parse JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        // Initialize XML writer
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stringWriter);

        writer.writeStartDocument("1.0");
        writer.writeStartElement("Root");

        // Process each mapping
        for (Mapping mapping : mappings) {
            String jsonPointer = convertJsonPathToJsonPointer(mapping.getJPath());
            JsonNode jsonValue = rootNode.at(jsonPointer);

            if (!jsonValue.isMissingNode()) {
                writeXmlElement(writer, jsonValue, mapping);
            } else {
                System.out.println("Skipping missing node for: " + mapping.getJPath());
            }
        }

        writer.writeEndElement(); // Close Root
        writer.writeEndDocument();
        writer.close();

        return stringWriter.toString();
    }

    private static void writeXmlElement(XMLStreamWriter writer, JsonNode jsonNode, Mapping mapping) throws Exception {
        String[] xpathParts = mapping.getXPath().split("/");

        // Extract element name and attribute (if present)
        String elementName = xpathParts[0];
        String attributeName = xpathParts.length > 1 && xpathParts[1].startsWith("@") ? xpathParts[1].substring(1) : null;

        // Log mapping and current JSON node
        System.out.println("Processing mapping: " + mapping.getXPath());
        System.out.println("JSON Node: " + jsonNode.toPrettyString());

        if (mapping.isList() && jsonNode.isArray()) {
            // Handle lists
            for (JsonNode listItem : jsonNode) {
                System.out.println("Processing list item for element: " + elementName);
                writer.writeStartElement(elementName);

                // Write attributes
                writeAttributes(writer, listItem, mapping);

                // Process child elements or write direct value
                if (listItem.isValueNode()) {
                    System.out.println("Writing value for element: " + elementName + " = " + listItem.asText());
                    writer.writeCharacters(listItem.asText());
                } else {
                    processChildMappings(writer, listItem, mapping);
                }

                writer.writeEndElement();
                System.out.println("Finished processing list item for element: " + elementName);
            }
        } else if (jsonNode.isObject()) {
            // Handle objects
            writer.writeStartElement(elementName);

            // Write attributes
            writeAttributes(writer, jsonNode, mapping);

            // Process child mappings for object fields
            if (mapping.getChildMappings() != null && !mapping.getChildMappings().isEmpty()) {
                processChildMappings(writer, jsonNode, mapping);
            } else if (jsonNode.isValueNode()) {
                System.out.println("Writing direct value for element: " + elementName + " = " + jsonNode.asText());
                writer.writeCharacters(jsonNode.asText());
            }

            writer.writeEndElement();
            System.out.println("Finished processing object for element: " + elementName);
        } else if (jsonNode.isValueNode()) {
            // Handle single values
            System.out.println("Processing value node for element: " + elementName + " = " + jsonNode.asText());
            writer.writeStartElement(elementName);
            writer.writeCharacters(jsonNode.asText());
            writer.writeEndElement();
            System.out.println("Finished processing value node for element: " + elementName);
        }
    }

    // Helper method to write attributes
    private static void writeAttributes(XMLStreamWriter writer, JsonNode jsonNode, Mapping mapping) throws Exception {
        if (mapping.getChildMappings() != null) {
            for (Mapping childMapping : mapping.getChildMappings()) {
                if (childMapping.getXPath().contains("@")) {
                    String childPointer = convertJsonPathToJsonPointer(childMapping.getJPath());
                    JsonNode attributeNode = jsonNode.at(childPointer);
                    if (!attributeNode.isMissingNode()) {
                        String attrName = childMapping.getXPath().split("@")[1];
                        System.out.println("Writing attribute: " + attrName + " = " + attributeNode.asText());
                        writer.writeAttribute(attrName, attributeNode.asText());
                    }
                }
            }
        }
    }

    // Helper method to process child mappings
    private static void processChildMappings(XMLStreamWriter writer, JsonNode jsonNode, Mapping mapping) throws Exception {
        if (mapping.getChildMappings() != null) {
            for (Mapping childMapping : mapping.getChildMappings()) {
                if (!childMapping.getXPath().contains("@")) { // Skip attributes
                    String childPointer = convertJsonPathToJsonPointer(childMapping.getJPath());
                    JsonNode childNode = jsonNode.at(childPointer);
                    if (!childNode.isMissingNode()) {
                        System.out.println("Processing child mapping: " + childMapping.getXPath());
                        writeXmlElement(writer, childNode, childMapping);
                    } else {
                        System.out.println("Child node missing for: " + childMapping.getXPath());
                    }
                }
            }
        }
    }


    private static String convertJsonPathToJsonPointer(String jsonPath) {
        if (jsonPath.startsWith("$.")) {
            return "/" + jsonPath.substring(2).replace(".", "/").replace("[*]", "");
        }
        if (jsonPath.equals("$")) {
            return ""; // Root JSON path
        }
        throw new IllegalArgumentException("Invalid JSONPath expression: " + jsonPath);
    }

}
