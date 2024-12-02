package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class TransformerTest {


    @Test
    public void testComplexJsonToXmlTransformation() throws Exception {
        // Input JSON
        String jsonString = """
                {
                  "rootName": "ComplexRoot",
                  "metadata": {
                    "createdBy": "User123",
                    "timestamp": "2024-11-27T12:00:00Z"
                  },
                  "groups": [
                    {
                      "name": "ParentGroup",
                      "type": "parent",
                      "subGroups": [
                        {
                          "name": "ChildGroup1",
                          "items": [
                            {"id": "item1", "value": "value1"},
                            {"id": "item2", "value": "value2"}
                          ]
                        },
                        {
                          "name": "ChildGroup2",
                          "items": []
                        }
                      ]
                    },
                    {
                      "name": "AnotherGroup",
                      "type": "independent",
                      "items": ["itemA", "itemB"]
                    }
                  ]
                }
                """;

        // Expected XML
        String expectedXml = """
                <?xml version="1.0"?>
                <Root>
                    <RootName>ComplexRoot</RootName>
                    <Metadata>
                        <CreatedBy>User123</CreatedBy>
                        <Timestamp>2024-11-27T12:00:00Z</Timestamp>
                    </Metadata>
                    <Group name="ParentGroup" type="parent">
                        <SubGroup name="ChildGroup1">
                            <Item id="item1">
                                <Value>value1</Value>
                            </Item>
                            <Item id="item2">
                                <Value>value2</Value>
                            </Item>
                        </SubGroup>
                        <SubGroup name="ChildGroup2"></SubGroup>
                    </Group>
                    <Group name="AnotherGroup" type="independent">
                        <Item>itemA</Item>
                        <Item>itemB</Item>
                    </Group>
                </Root>
                """.trim();

        String csvFilePath = "src/test/resources/mapping.csv";

        // Read the CSV file
//       List<Mapping> mappings = MappingGenerator.readMappingsFromCsv(csvFilePath);

        List<Mapping> mappings = Arrays.asList(
                new Mapping("$.rootName", "RootName", false, true, "object"),

                new Mapping("$.metadata", "Metadata", false, false, "object", Arrays.asList(
                        new Mapping("$.createdBy", "CreatedBy", false, false, "object"),
                        new Mapping("$.timestamp", "Timestamp", false, false, "object")
                )),
                new Mapping("$.groups[*]", "Group", true, false, "list", Arrays.asList(
                        new Mapping("$.name", "Group/@name", false, false, "object"),
                        new Mapping("$.type", "Group/@type", false, false, "object"),
                        new Mapping("$.subGroups[*]", "SubGroup", true, false, "list", Arrays.asList(
                                new Mapping("$.name", "SubGroup/@name", false, false, "object"),
                                new Mapping("$.items[*]", "Item", true, false, "list", Arrays.asList(
                                        new Mapping("$.id", "Item/@id", false, false, "object"),
                                        new Mapping("$.value", "Value", false, false, "object")
                                ))
                        )),
                        new Mapping("$.items[*]", "Item", true, false, "list")
                ))
        );


        // Perform transformation
        String actualXml = JsonToXmlMapper.transformJsonToXml(jsonString, mappings);

        // Normalize XML for comparison (removing whitespace differences)
        assertEquals(expectedXml.replaceAll("\\s+", ""), actualXml.replaceAll("\\s+", ""),
                "The generated XML should match the expected XML.");

        System.out.println(actualXml);
    }

}
