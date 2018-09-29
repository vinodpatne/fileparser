package com.mc.utils.codegen.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationMetaData {

    private String mainAnnotationClass;

    private List<AnnotationParameter> annotationParameterList;

    private String childAnnotationMetaDataListName;

    private List<AnnotationMetaData> childAnnotationMetaDataList;

    /**
     * @return Returns the mainAnnotation.
     */
    public String getMainAnnotationClass() {
        return mainAnnotationClass;
    }

    /**
     * @param mainAnnotation
     *            The mainAnnotation to set.
     */
    public void setMainAnnotationClass(String fullyQualifiedClassName) {
        this.mainAnnotationClass = fullyQualifiedClassName;
    }

    /**
     * @return Returns the annotationParameterList.
     */
    public List<AnnotationParameter> getAnnotationParameterList() {
        return annotationParameterList;
    }

    /**
     * @param annotationParameterList
     *            The annotationParameterList to set.
     */
    public void setAnnotationParameterList(List<AnnotationParameter> annotationParameterList) {
        this.annotationParameterList = annotationParameterList;
    }

    public void addAnnotationParameter(AnnotationParameter annotationParameter) {
        if (this.annotationParameterList == null) {
            this.annotationParameterList = new ArrayList<AnnotationParameter>();
        }
        this.annotationParameterList.add(annotationParameter);
    }

    /**
     * @return Returns the childAnnotationMetaDataList.
     */
    public List<AnnotationMetaData> getChildAnnotationMetaDataList() {
        return childAnnotationMetaDataList;
    }

    /**
     * @param childAnnotationMetaDataList
     *            The childAnnotationMetaDataList to set.
     */
    public void setChildAnnotationMetaDataList(List<AnnotationMetaData> annotationMetaDataList) {
        this.childAnnotationMetaDataList = annotationMetaDataList;
    }

    public void addAnnotationMetaData(AnnotationMetaData annotationMetaData) {
        if (this.childAnnotationMetaDataList == null) {
            this.childAnnotationMetaDataList = new ArrayList<AnnotationMetaData>();
        }
        this.childAnnotationMetaDataList.add(annotationMetaData);
    }

    /**
     * @return Returns the childAnnotationMetaDataListName.
     */
    public String getChildAnnotationMetaDataListName() {
        return childAnnotationMetaDataListName;
    }

    /**
     * @param childAnnotationMetaDataListName The childAnnotationMetaDataListName to set.
     */
    public void setChildAnnotationMetaDataListName(String childAnnotationMetaDataListName) {
        this.childAnnotationMetaDataListName = childAnnotationMetaDataListName;
    }

}
