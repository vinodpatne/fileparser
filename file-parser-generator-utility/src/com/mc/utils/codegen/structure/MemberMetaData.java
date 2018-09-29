package com.mc.utils.codegen.structure;

import java.util.ArrayList;
import java.util.List;

public class MemberMetaData {

    private String memberName;

    private String javaDocs;

    private String dataType;

    private String defaultLiteralValue;

    private boolean requireGetterSetterMethods;

    private List<AnnotationMetaData> fieldAnnotationMetaDataList;

    private List<AnnotationMetaData> getterAnnotationMetaDataList;

    private List<AnnotationMetaData> setterAnnotationMetaDataList;

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
     * @return Returns the defaultLiteralValue.
     */
    public String getDefaultLiteralValue() {
        return defaultLiteralValue;
    }

    /**
     * @param defaultLiteralValue
     *            The defaultLiteralValue to set.
     */
    public void setDefaultLiteralValue(String defaultValue) {
        this.defaultLiteralValue = defaultValue;
    }

    /**
     * @return Returns the javaDocs.
     */
    public String getJavaDocs() {
        return javaDocs;
    }

    /**
     * @param javaDocs
     *            The javaDocs to set.
     */
    public void setJavaDocs(String javaDocs) {
        this.javaDocs = javaDocs;
    }

    /**
     * @return Returns the memberName.
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * @param memberName
     *            The memberName to set.
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    /**
     * @param requireGetterSetterMethods
     *            The requireGetterSetterMethods to set.
     */
    public void setRequireGetterSetterMethods(boolean requireGetterSetterMethods) {
        this.requireGetterSetterMethods = requireGetterSetterMethods;
    }

    /**
     * @return Returns the getterAnnotationMetaDataList.
     */
    public List<AnnotationMetaData> getGetterAnnotationMetaDataList() {
        return getterAnnotationMetaDataList;
    }

    /**
     * @return Returns the annotationMetaData.
     */
    public List<AnnotationMetaData> getFieldAnnotationMetaDataList() {
        return fieldAnnotationMetaDataList;
    }

    /**
     * @param annotationMetaData
     *            The annotationMetaData to set.
     */
    public void setFieldAnnotationMetaDataList(List<AnnotationMetaData> annotationMetaData) {
        this.fieldAnnotationMetaDataList = annotationMetaData;
    }

    /**
     * @return Returns the requireGetterSetterMethods.
     */
    public boolean isRequireGetterSetterMethods() {
        return requireGetterSetterMethods;
    }

    /**
     * @param getterAnnotationMetaDataList
     *            The getterAnnotationMetaDataList to set.
     */
    public void setGetterAnnotationMetaDataList(List<AnnotationMetaData> getterAnnotationMetaDataList) {
        this.getterAnnotationMetaDataList = getterAnnotationMetaDataList;
    }

    /**
     * @return Returns the setterAnnotationMetaDataList.
     */
    public List<AnnotationMetaData> getSetterAnnotationMetaDataList() {
        return setterAnnotationMetaDataList;
    }

    /**
     * @param setterAnnotationMetaDataList
     *            The setterAnnotationMetaDataList to set.
     */
    public void setSetterAnnotationMetaDataList(List<AnnotationMetaData> setterAnnotationMetaDataList) {
        this.setterAnnotationMetaDataList = setterAnnotationMetaDataList;
    }

    /**
     * @param annotationMetaData
     *            The annotationMetaData to set.
     */
    public void addFieldAnnotationMetaData(AnnotationMetaData annotationMetaData) {
        if (fieldAnnotationMetaDataList == null) {
            fieldAnnotationMetaDataList = new ArrayList<AnnotationMetaData>();
        }
        this.fieldAnnotationMetaDataList.add(annotationMetaData);
    }

    /**
     * @param annotationMetaData
     *            The annotationMetaData to set.
     */
    public void addGetterAnnotationMetaData(AnnotationMetaData annotationMetaData) {
        if (getterAnnotationMetaDataList == null) {
            getterAnnotationMetaDataList = new ArrayList<AnnotationMetaData>();
        }
        this.getterAnnotationMetaDataList.add(annotationMetaData);
    }

    /**
     * @param annotationMetaData
     *            The annotationMetaData to set.
     */
    public void addSetterAnnotationMetaData(AnnotationMetaData annotationMetaData) {
        if (setterAnnotationMetaDataList == null) {
            setterAnnotationMetaDataList = new ArrayList<AnnotationMetaData>();
        }
        this.setterAnnotationMetaDataList.add(annotationMetaData);
    }
}
