package com.mc.utils.codegen.fileparser;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.mc.utils.codegen.CodeGenFactory;
import com.mc.utils.codegen.structure.AnnotationMetaData;
import com.mc.utils.codegen.structure.AnnotationParameter;
import com.mc.utils.codegen.structure.ClassMetaData;
import com.mc.utils.codegen.structure.MemberMetaData;
import com.sun.codemodel.JCodeModel;

public class Main {

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("config", "application.properties")));
        String outputDirectory = properties.getProperty("output.dir");
        String packageName = properties.getProperty("output.package.name");

        File configfile = new File("config", "Field_Mapping.csv");
        List<RecordConfig> recordConfigs = FieldMappingConfigParser.getFieldConfigs(configfile);

        for (RecordConfig recordConfig : recordConfigs) {
            String className = recordConfig.getRecordClassName();
            ClassMetaData classMetaData = new ClassMetaData();
            classMetaData.setPackageName(packageName);

            classMetaData.setClassName(className);
            classMetaData.setJavaDocs("This class is generated by Vinod.");

            AnnotationMetaData annotationMetaData = new AnnotationMetaData();
            annotationMetaData.setMainAnnotationClass("com.ancientprogramming.fixedformat4j.annotation.Record");
            classMetaData.addAnnotationMetaData(annotationMetaData);

            List<FieldConfig> fieldConfigs = recordConfig.getFieldConfigs();
            for (FieldConfig fieldConfig : fieldConfigs) {

                System.out.println("Start processing " + fieldConfig.getFieldName() + " field.");

                MemberMetaData memberMetaData = new MemberMetaData();
                memberMetaData.setMemberName(fieldConfig.getFieldName());

                memberMetaData.setJavaDocs(fieldConfig.getFieldDesc());
                memberMetaData.setDataType(fieldConfig.getDataType());
                memberMetaData.setRequireGetterSetterMethods(true);

                AnnotationMetaData fieldsAnnotationMetaData = new AnnotationMetaData();
                fieldsAnnotationMetaData.setMainAnnotationClass("com.ancientprogramming.fixedformat4j.annotation.Fields");
                // fieldsAnnotationMetaData.setMainAnnotationClass("Fields");

                AnnotationMetaData fieldAnnotationMetaData = new AnnotationMetaData();
                fieldAnnotationMetaData.setMainAnnotationClass("com.ancientprogramming.fixedformat4j.annotation.Field");
                fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("offset", "int", fieldConfig.getOffset()));
                fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("length", "int", fieldConfig.getLength()));

                // Enum
                if (fieldConfig.getAlign() != null) {
                    fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("align",
                            "com.ancientprogramming.fixedformat4j.annotation.Align." + fieldConfig.getAlign(), false));
                }

                if (fieldConfig.getPaddingChar() != null) {
                    fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("paddingChar", "char", fieldConfig.getPaddingChar()));
                }

                fieldsAnnotationMetaData.addAnnotationMetaData(fieldAnnotationMetaData);
                memberMetaData.addGetterAnnotationMetaData(fieldsAnnotationMetaData);

                if (fieldConfig.getFormat() != null) {
                    AnnotationMetaData fieldAnnotationMetaData1 = getFormatterClass(fieldConfig.getDataType(), fieldConfig.getFormat());
                    if (fieldAnnotationMetaData1 != null) {
                        memberMetaData.addGetterAnnotationMetaData(fieldAnnotationMetaData1);
                    }
                }

                classMetaData.addMemberMetaData(memberMetaData);
            }

            System.out.println("Generating " + classMetaData.getClassName() + " class.");

            JCodeModel codeModel = CodeGenFactory.createClass(classMetaData);

            CodeGenFactory.writeCodeModel(codeModel, outputDirectory);

        }

    }

    // Working
    public static AnnotationMetaData getFormatterClass(String dataType, String format) {
        AnnotationMetaData fieldAnnotationMetaData = null;
        if (StringUtils.isNotEmpty(format)) {

            String formatterClassPackage = "com.ancientprogramming.fixedformat4j.annotation.";
            dataType = dataType.toLowerCase();

            System.out.println("dataType=" + dataType);

            fieldAnnotationMetaData = new AnnotationMetaData();
            switch (dataType) {
            case "int":
            case "integer":
            case "long":
            case "short":
                fieldAnnotationMetaData.setMainAnnotationClass(formatterClassPackage + "FixedFormatNumber");
                fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("value", "String", format));
                break;
            case "double":
            case "float":
            case "bigdecimal":
                fieldAnnotationMetaData.setMainAnnotationClass(formatterClassPackage + "FixedFormatDecimal");
                // fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("value", "String", format));
                int decimals = format.substring(format.lastIndexOf(".") + 1).length();
                fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("decimals", "int", String.valueOf(decimals)));
                break;
            case "boolean":
                // @FixedFormatBoolean(trueValue = "1", falseValue = "0")
                fieldAnnotationMetaData.setMainAnnotationClass(formatterClassPackage + "FixedFormatBoolean");
                fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("trueValue", "String", format));
                break;
            case "java.util.date":
                fieldAnnotationMetaData.setMainAnnotationClass(formatterClassPackage + "FixedFormatPattern");
                fieldAnnotationMetaData.addAnnotationParameter(new AnnotationParameter("value", "String", format));
                break;
            default:
                fieldAnnotationMetaData = null;
                break;
            }
        }

        return fieldAnnotationMetaData;
    }

}