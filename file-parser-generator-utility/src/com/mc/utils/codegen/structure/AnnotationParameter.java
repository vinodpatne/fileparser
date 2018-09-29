package com.mc.utils.codegen.structure;

public class AnnotationParameter {

    private String name;

    private String dataType;

    private String value;

    private boolean classRef;

    public AnnotationParameter(String name, String dataType, String value) {
        this.name = name;
        this.dataType = dataType;
        this.value = value;
    }

    public AnnotationParameter(String name, String value, boolean classRef) {
        this.name = name;
        this.value = value;
        this.classRef = classRef;
    }

    /**
     * @return Returns the dataType.
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType
     *            The dataType to set.
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return Returns the classRef.
     */
    public boolean isClassRef() {
        return classRef;
    }

    /**
     * @param classRef
     *            The classRef to set.
     */
    public void setClassRef(boolean classRef) {
        this.classRef = classRef;
    }

}
