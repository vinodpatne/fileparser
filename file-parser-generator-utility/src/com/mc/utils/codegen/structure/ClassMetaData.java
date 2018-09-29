package com.mc.utils.codegen.structure;

import java.util.ArrayList;
import java.util.List;

public class ClassMetaData {

    private String packageName;

    private String className;

    private String javaDocs;

    private List<AnnotationMetaData> annotationMetaDataList;

    private List<MemberMetaData> memberMetaDataList;

    private List<String> importClasses;

    public ClassMetaData() {
    }

    public ClassMetaData(String className, String packageName) {
	this.className = className;
	this.packageName = packageName;
    }

    /**
     * @return Returns the name.
     */
    public String getClassName() {
	return className;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setClassName(String className) {
	this.className = className;
    }

    /**
     * @return Returns the packageName.
     */
    public String getPackageName() {
	return packageName;
    }

    /**
     * @param packageName
     *            The packageName to set.
     */
    public void setPackageName(String packageName) {
	this.packageName = packageName;
    }

    /**
     * @return Returns the annotationMetaData.
     */
    public List<AnnotationMetaData> getAnnotationMetaDataList() {
	return annotationMetaDataList;
    }

    /**
     * @param annotationMetaData
     *            The annotationMetaData to set.
     */
    public void setAnnotationMetaDataList(List<AnnotationMetaData> annotationMetaData) {
	this.annotationMetaDataList = annotationMetaData;
    }

    /**
     * @param annotationMetaData
     *            The annotationMetaData to set.
     */
    public void addAnnotationMetaData(AnnotationMetaData annotationMetaData) {
	if (annotationMetaDataList == null) {
	    annotationMetaDataList = new ArrayList<AnnotationMetaData>();
	}
	this.annotationMetaDataList.add(annotationMetaData);
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
     * @return Returns the memberMetaDataList.
     */
    public List<MemberMetaData> getMemberMetaDataList() {
	return memberMetaDataList;
    }

    /**
     * @param memberMetaDataList
     *            The memberMetaDataList to set.
     */
    public void setMemberMetaDataList(List<MemberMetaData> membersMetaDataList) {
	this.memberMetaDataList = membersMetaDataList;
    }

    /**
     * @param annotationMetaData
     *            The annotationMetaData to set.
     */
    public void addMemberMetaData(MemberMetaData memberMetaData) {
	if (memberMetaDataList == null) {
	    memberMetaDataList = new ArrayList<MemberMetaData>();
	}
	this.memberMetaDataList.add(memberMetaData);
    }

    /**
     * @return the importClasses
     */
    public final List<String> getImportClasses() {
	return importClasses;
    }

    /**
     * @param importClasses
     *            the importClasses to set
     */
    public final void setImportClasses(List<String> importClasses) {
	this.importClasses = importClasses;
    }

    public void addImportClass(String fullyQualifiedClassName) {
	if (importClasses == null) {
	    importClasses = new ArrayList<String>();
	}
	this.importClasses.add(fullyQualifiedClassName);
    }

}
