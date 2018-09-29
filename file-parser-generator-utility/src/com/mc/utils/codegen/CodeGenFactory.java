package com.mc.utils.codegen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.mc.utils.codegen.structure.AnnotationMetaData;
import com.mc.utils.codegen.structure.AnnotationParameter;
import com.mc.utils.codegen.structure.ClassMetaData;
import com.mc.utils.codegen.structure.MemberMetaData;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JDocCommentable;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;

/**
 * you cant add import statements directly they get added automatically when you ref classes.
 * 
 * @author vpatne
 * 
 */
public class CodeGenFactory {

    // Method to get JType based on any String Value
    private static JType getType(JCodeModel jCodeModel, String type) {
	if (type.equals("short")) {
	    return jCodeModel.SHORT;
	} else if (type.equals("int")) {
	    return jCodeModel.INT;
	} else if (type.equals("long")) {
	    return jCodeModel.LONG;
	} else if (type.equals("char")) {
	    return jCodeModel.CHAR;
	} else if (type.equals("boolean")) {
	    return jCodeModel.BOOLEAN;
	} else if (type.equals("double")) {
	    return jCodeModel.DOUBLE;
	} else if (type.equals("float")) {
	    return jCodeModel.FLOAT;
	} else if (type.equals("String")) {
	    return jCodeModel.ref(String.class);
	} else if (type.equals("Short")) {
	    return jCodeModel.ref(Short.class);
	} else if (type.equals("Integer")) {
	    return jCodeModel.ref(Integer.class);
	} else if (type.equals("Long")) {
	    return jCodeModel.ref(Long.class);
	} else if (type.equals("Date")) {
	    return jCodeModel.ref(java.util.Date.class);
	} else if (type.equals("sqlDate")) {
	    return jCodeModel.ref(java.sql.Date.class);
	} else if (type.equals("Character")) {
	    return jCodeModel.ref(Character.class);
	} else if (type.equals("Boolean")) {
	    return jCodeModel.ref(Boolean.class);
	} else if (type.equals("Double")) {
	    return jCodeModel.ref(Double.class);
	} else if (type.equals("Float")) {
	    return jCodeModel.ref(Float.class);
	} else if (type.equals("Unsigned32")) {
	    return jCodeModel.LONG;
	} else if (type.equals("Unsigned64")) {
	    return jCodeModel.LONG;
	} else if (type.equals("Integer32")) {
	    return jCodeModel.INT;
	} else if (type.equals("Integer64")) {
	    return jCodeModel.LONG;
	} else if (type.equals("Enumerated")) {
	    return jCodeModel.INT;
	} else if (type.equals("Float32")) {
	    return jCodeModel.FLOAT;
	} else if (type.equals("Float64")) {
	    return jCodeModel.DOUBLE;
	} else {
	    return null;
	}
    }

    public static JCodeModel createClass(ClassMetaData metaData) throws Exception {
	/* Creating java code model classes */
	JCodeModel jCodeModel = new JCodeModel();

	/* Adding packages here */
	JPackage jp = jCodeModel._package(metaData.getPackageName());

	/* Giving Class Name to Generate */
	JDefinedClass definedClass = jp._class(metaData.getClassName());

	addJavaDocsComment(definedClass, metaData.getJavaDocs());
	addAnnotations(jCodeModel, definedClass, metaData.getAnnotationMetaDataList());

	addMembers(jCodeModel, definedClass, metaData.getMemberMetaDataList());

	List<String> importClasses = metaData.getImportClasses();
	for (String importClass : importClasses) {
	    JClass importedClass = jCodeModel.ref(importClass);
	}

	return jCodeModel;
    }

    private static void addMembers(JCodeModel jCodeModel, JDefinedClass definedClass, List<MemberMetaData> memberMetaDataList) throws Exception {
	if (memberMetaDataList != null) {
	    int size = memberMetaDataList.size();
	    for (int index = 0; index < size; index++) {
		MemberMetaData memberMetaData = memberMetaDataList.get(index);
		addMember(jCodeModel, definedClass, memberMetaData);
	    }
	}
    }

    private static void addMember(JCodeModel jCodeModel, JDefinedClass definedClass, MemberMetaData memberMetaData) throws Exception {
	if (memberMetaData != null) {
	    JType jType = jCodeModel.parseType(memberMetaData.getDataType());
	    JFieldVar field = definedClass.field(JMod.PRIVATE, jType, memberMetaData.getMemberName());
	    String value = memberMetaData.getDefaultLiteralValue();
	    System.out.println("jType.binaryName()=" + jType.binaryName());
	    if (value != null) {
		if (jType.isPrimitive()) {
		    initTypeValue(jCodeModel, field, jType, value);
		} else {
		    field.init(JExpr.lit(value));
		}
	    }
	    String javaDocs = memberMetaData.getJavaDocs();
	    if (javaDocs != null) {
		addJavaDocsComment(field, javaDocs);
	    }

	    if (memberMetaData.isRequireGetterSetterMethods()) {
		addGetterSetterMethods(jCodeModel, definedClass, field, jType, memberMetaData);
	    }
	}
    }

    private static void addGetterSetterMethods(JCodeModel jCodeModel, JDefinedClass definedClass, JFieldVar field, JType jType,
	    MemberMetaData memberMetaData) throws Exception {
	String orgFieldName = field.name();
	StringBuilder fieldBuilder = new StringBuilder(orgFieldName);
	fieldBuilder.setCharAt(0, Character.toUpperCase(fieldBuilder.charAt(0)));
	String fieldName = fieldBuilder.toString();

	// Create the setter method and set the JFieldVar previously defined with the given parameter
	JMethod setterMethod = definedClass.method(JMod.PUBLIC | JMod.FINAL, jType, "set" + fieldName);
	setterMethod.param(jType, orgFieldName);
	setterMethod.body().assign(JExpr._this().ref(orgFieldName), JExpr.ref(orgFieldName));

	addJavaDocsComment(setterMethod, "Sets " + orgFieldName + " field value.");
	// @param
	addParamJavaDocsComment(setterMethod, orgFieldName, "            The " + orgFieldName + " to set.");

	addAnnotations(jCodeModel, setterMethod, memberMetaData.getSetterAnnotationMetaDataList());

	// Create the getter method and return the JFieldVar previously defined
	JMethod getterMethod = definedClass.method(JMod.PUBLIC | JMod.FINAL, jType, "get" + fieldName);
	JBlock block = getterMethod.body();
	block._return(JExpr._this().ref(orgFieldName));

	addJavaDocsComment(getterMethod, "This method returns the " + orgFieldName + " value.<BR>");
	addReturnJavaDocsComment(getterMethod, "Returns the " + orgFieldName + ".");
	addAnnotations(jCodeModel, getterMethod, memberMetaData.getGetterAnnotationMetaDataList());

    }

    // Method to get JType based on any String Value
    private static void initTypeValue(JCodeModel jCodeModel, JFieldVar field, JType jType, String value) {
	// default value
	if (jType.compareTo(jCodeModel.SHORT) == 0) {
	    field.init(JExpr.lit(Short.valueOf(value)));
	} else if (jType.compareTo(jCodeModel.INT) == 0) {
	    field.init(JExpr.lit(Integer.valueOf(value)));
	} else if (jType.compareTo(jCodeModel.LONG) == 0) {
	    field.init(JExpr.lit(Long.valueOf(value)));
	} else if (jType.compareTo(jCodeModel.CHAR) == 0) {
	    field.init(JExpr.lit(value.charAt(0)));
	} else if (jType.compareTo(jCodeModel.BOOLEAN) == 0) {
	    field.init(JExpr.lit(Boolean.valueOf(value)));
	} else if (jType.compareTo(jCodeModel.FLOAT) == 0) {
	    field.init(JExpr.lit(Float.valueOf(value)));
	} else if (jType.compareTo(jCodeModel.DOUBLE) == 0) {
	    field.init(JExpr.lit(Double.valueOf(value)));
	}
    }

    // Method to get JType based on any String Value
    private static void setParamTypeValue(JAnnotationUse annotationUse, String param, String dataType, String value) {
	if (dataType.equals("short")) {
	    annotationUse.param(param, JExpr.lit(Short.valueOf(value)));
	} else if (dataType.equals("int")) {
	    annotationUse.param(param, JExpr.lit(Integer.valueOf(value)));
	} else if (dataType.equals("long")) {
	    annotationUse.param(param, JExpr.lit(Long.valueOf(value)));
	} else if (dataType.equals("char")) {
	    annotationUse.param(param, JExpr.lit(value != null && value.length() > 0 ? value.charAt(0) : ' '));
	} else if (dataType.equals("boolean")) {
	    annotationUse.param(param, JExpr.lit(Boolean.valueOf(value)));
	} else if (dataType.equals("double")) {
	    annotationUse.param(param, JExpr.lit(Double.valueOf(value)));
	} else if (dataType.equals("float")) {
	    annotationUse.param(param, JExpr.lit(Float.valueOf(value)));
	} else if (dataType.equals("String")) {
	    annotationUse.param(param, JExpr.lit(value));
	}
    }

    private static void addJavaDocsComment(JDocCommentable docCommentable, String javaDocs) {
	/* Adding class level coment */
	if (javaDocs != null) {
	    JDocComment jDocComment = docCommentable.javadoc();
	    jDocComment.add(javaDocs);
	}
    }

    private static void addParamJavaDocsComment(JDocCommentable docCommentable, String param, String javaDocs) {
	/* Adding class level coment */
	if (javaDocs != null) {
	    JDocComment jDocComment = docCommentable.javadoc();
	    jDocComment.addParam(param).add(javaDocs);
	}
    }

    private static void addReturnJavaDocsComment(JDocCommentable docCommentable, String javaDocs) {
	/* Adding class level coment */
	if (javaDocs != null) {
	    JDocComment jDocComment = docCommentable.javadoc();
	    jDocComment.addReturn().add(javaDocs);
	}
    }

    private static void addAnnotations(JCodeModel jCodeModel, JAnnotatable annotatableObject, List<AnnotationMetaData> annotationMetaDataList) {
	addAnnotations(jCodeModel, annotatableObject, annotationMetaDataList, null, null);
    }

    private static void addAnnotations(JCodeModel jCodeModel, JAnnotatable annotatableObject, List<AnnotationMetaData> annotationMetaDataList,
	    String childAnnotationListName, JAnnotationUse parentAnnotation) {
	if (annotationMetaDataList != null) {
	    JAnnotationArrayMember arrayMember = null;
	    if (parentAnnotation != null) {
		String childAnnotationListNameTemp = "value";
		if (childAnnotationListName != null && childAnnotationListName.trim().length() > 0) {
		    childAnnotationListNameTemp = childAnnotationListName;
		}
		arrayMember = parentAnnotation.paramArray(childAnnotationListNameTemp);
	    }
	    int size = annotationMetaDataList.size();
	    for (int index = 0; index < size; index++) {
		AnnotationMetaData annotationMetaData = annotationMetaDataList.get(index);
		JClass mainAnnotationClass = jCodeModel.ref(annotationMetaData.getMainAnnotationClass());

		JAnnotationUse annotationUse = null;
		if (arrayMember != null) {
		    annotationUse = arrayMember.annotate(mainAnnotationClass);
		} else {
		    annotationUse = annotatableObject.annotate(mainAnnotationClass);
		}

		// Add Class Ref annotations
		List<AnnotationParameter> annotationParameterList = annotationMetaData.getAnnotationParameterList();
		if (annotationParameterList != null) {
		    int paramCount = annotationParameterList.size();
		    for (int index1 = 0; index1 < paramCount; index1++) {
			AnnotationParameter annotationParameter = annotationParameterList.get(index1);
			String param = annotationParameter.getName();
			String dataType = annotationParameter.getDataType();
			String value = annotationParameter.getValue();
			if (annotationParameter.isClassRef()) {
			    JClass classRef = jCodeModel.ref(value);
			    annotationUse.param(param, classRef);
			} else {
			    setParamTypeValue(annotationUse, param, dataType, value);
			}
		    }
		}
		// Check for inner annotations
		addAnnotations(jCodeModel, annotatableObject, annotationMetaData.getChildAnnotationMetaDataList(),
			annotationMetaData.getChildAnnotationMetaDataListName(), annotationUse);
	    }
	}

    }

    // Function to generate CodeModel Class
    public static void writeCodeModel(JCodeModel jCodeModel, String outputDirectory) throws IOException {
	/* Building class at given location */
	jCodeModel.build(new File(outputDirectory));
    }

    // Function to generate CodeModel Class
    public void writeCodeModel(String factroyPackage) {
	try {

	    /* Creating java code model classes */
	    JCodeModel jCodeModel = new JCodeModel();

	    /* Adding packages here */
	    JPackage jp = jCodeModel._package(factroyPackage);

	    /* Giving Class Name to Generate */
	    JDefinedClass definedClass = jp._class("GeneratedFactory");

	    /* Adding annotation for the Class */
	    // jc.annotate(Factory.class);
	    JClass runWith = jCodeModel.ref("org.junit.runner.RunWith");
	    JClass runnerRef = jCodeModel.ref("com.solstoneplus.global.guice.GuiceJUnitRunner");
	    JAnnotationUse annotationUse = definedClass.annotate(runWith);
	    annotationUse.param("value", runnerRef);
	    annotationUse.param("version", "0.1");

	    /* Adding class level coment */
	    JDocComment jDocComment = definedClass.javadoc();
	    jDocComment.add("Class Level Java Docs");

	    JFieldVar field = definedClass.field(JMod.PRIVATE, int.class, "intVar");
	    field.init(JExpr.lit(5)); // default value

	    // Create the getter method and return the JFieldVar previously defined
	    JMethod getterMethod = definedClass.method(JMod.PUBLIC, int.class, "get" + field.name());
	    JBlock block = getterMethod.body();
	    block._return(field);

	    // Create the setter method and set the JFieldVar previously defined with the given parameter
	    JMethod setterMethod = definedClass.method(JMod.PUBLIC, int.class, "set" + field.name());
	    setterMethod.param(int.class, field.name());
	    setterMethod.body().assign(JExpr._this().ref("intVar"), JExpr.ref(field.name()));

	    final JMethod method = definedClass.method(JMod.PUBLIC, Object.class, "getValue");
	    final JTypeVar t = method.generify("T");
	    method.type(t);
	    method.param(jCodeModel.ref(Class.class).narrow(t), "type");
	    method.body()._return(JExpr._null());

	    /* Adding method to the Class which is public static and returns com.somclass.AnyXYZ.class */
	    String mehtodName = "myFirstMehtod";
	    JMethod jmCreate = definedClass.method(JMod.PUBLIC | JMod.STATIC, Object.class, "create" + mehtodName);

	    /* Addign java doc for method */
	    jmCreate.javadoc().add("Method Level Java Docs");

	    JMethod m = definedClass.method(JMod.PUBLIC, jCodeModel.ref(List.class).narrow(jCodeModel.ref(String.class)), "getLsColumn");

	    /* Adding method body */
	    JBlock jBlock = jmCreate.body();

	    /* Defining method parameter */
	    JType jt = getType(jCodeModel, "Unsigned32");
	    if (jt != null) {
		jmCreate.param(jt, "data");
	    } else {
		jmCreate.param(java.lang.String.class, "data");
	    }

	    /* Defining some class Variable in mthod body */
	    JClass jClassavpImpl = jCodeModel.ref(Object.class);
	    JVar jvarAvpImpl = jBlock.decl(jClassavpImpl, "varName");
	    jvarAvpImpl.init(JExpr._new(jClassavpImpl));

	    /* Adding some direct statement */
	    jBlock.directStatement("varName.setCode(100);");

	    /* returning varibalbe */
	    jBlock._return(jvarAvpImpl);

	    /* Building class at given location */
	    jCodeModel.build(new File("output/src"));

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

}
