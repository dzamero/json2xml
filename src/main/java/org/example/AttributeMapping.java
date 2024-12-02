package org.example;

public class AttributeMapping {
    private String jpath; // JSON path
    private String xpath; // XML path
    private String sourceDataType;
    private String targetDataType;
    private String transformationRule;


    public AttributeMapping(String jpath, String xpath, String sourceDataType, String targetDataType, String transformationRule) {
        this.jpath = jpath;
        this.xpath = xpath;
        this.sourceDataType = sourceDataType;
        this.targetDataType = targetDataType;
        this.transformationRule = transformationRule;
    }

    // Getters and Setters
    public String getJpath() {
        return jpath;
    }
    public void setJpath(String jpath) {
        this.jpath = jpath;
    }
    public String getXpath() {
        return xpath;
    }
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
    public String getSourceDataType() {
        return sourceDataType;
    }
    public void setSourceDataType(String sourceDataType) {
        this.sourceDataType = sourceDataType;
    }
    public String getTargetDataType() {
        return targetDataType;
    }
    public void setTargetDataType(String targetDataType) {
        this.targetDataType = targetDataType;
    }
    public String getTransformationRule() {
        return transformationRule;
    }
    public void setTransformationRule(String transformationRule) {
        this.transformationRule = transformationRule;
    }
}

