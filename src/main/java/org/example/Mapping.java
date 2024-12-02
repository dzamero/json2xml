package org.example;
import java.util.ArrayList;
import java.util.List;

public class Mapping {
    private String jPath; // JSONPath
    private String xPath; // XPath
    private boolean isList; // Whether this path represents a list
    private boolean isRoot; // Whether this is the root element
    private String type; // "object" or "list"
    private List<Mapping> childMappings; // Child mappings

    // Constructor
    public Mapping(String jPath, String xPath, boolean isList, boolean isRoot, String type) {
        this.jPath = jPath;
        this.xPath = xPath;
        this.isList = isList;
        this.isRoot = isRoot;
        this.type = type;
        this.childMappings = new ArrayList<>(); // Initialize as empty list
    }

    public Mapping(String jPath, String xPath, boolean isList, boolean isRoot, String type, List<Mapping> childMappings) {
        this.jPath = jPath;
        this.xPath = xPath;
        this.isList = isList;
        this.isRoot = isRoot;
        this.type = type;
        this.childMappings = childMappings != null ? childMappings : new ArrayList<>();
    }


    public void addChildMapping(Mapping child) {
        this.childMappings.add(child);
    }

    // Getters and Setters
    public String getJPath() {
        return jPath;
    }

    public void setJPath(String jPath) {
        this.jPath = jPath;
    }

    public String getXPath() {
        return xPath;
    }

    public void setXPath(String xPath) {
        this.xPath = xPath;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public List<Mapping> getChildMappings() {
        return childMappings;
    }

    public void setChildMappings(List<Mapping> childMappings) {
        this.childMappings = childMappings != null ? childMappings : new ArrayList<>();
    }
}
