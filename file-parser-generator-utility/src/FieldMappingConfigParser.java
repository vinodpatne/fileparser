import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class FieldMappingConfigParser {
    public static List<RecordConfig> getFieldConfigs(File configfile) throws IOException {

	List<RecordConfig> recordConfigs = new ArrayList<RecordConfig>();

	List<FieldConfig> fieldConfigs = null;

	InputStream inputFileStream = new FileInputStream(configfile);
	List<String> fieldConfigLines = IOUtils.readLines(inputFileStream, Charset.defaultCharset());

	// remove header row
	fieldConfigLines.remove(0);

	String lastRecordClassName = "";

	for (String fieldConfigLine : fieldConfigLines) {
	    String[] values = fieldConfigLine.split(",");

	    FieldConfig fieldConfig = new FieldConfig();
	    int index = 0;
	    fieldConfig.setRecordClassName(values.length > index ? values[index++] : "");
	    fieldConfig.setFieldName(values.length > index ? values[index++].replaceAll(" ", "") : "");
	    fieldConfig.setFieldDesc(values.length > index ? values[index++] : "");
	    fieldConfig.setOffset(values.length > index ? values[index++] : "");
	    fieldConfig.setLength(values.length > index ? values[index++] : "");
	    fieldConfig.setAlign(values.length > index ? values[index++] : "");
	    fieldConfig.setPaddingChar(values.length > index ? values[index++] : "");
	    fieldConfig.setFormat(values.length > index ? values[index++] : "");
	    fieldConfig.setDataType(values.length > index ? values[index++] : "");
	    fieldConfig.setValidation(values.length > index ? values[index++] : "");
	    fieldConfig.setDefaultValue(values.length > index ? values[index++] : "");
	    fieldConfig.setConstantValue(values.length > index ? values[index++] : "");

	    if (!fieldConfig.getRecordClassName().equalsIgnoreCase(lastRecordClassName)) {
		// set prev list to new record config and add to record config list
		if (fieldConfigs != null) {
		    RecordConfig recordConfig = new RecordConfig(lastRecordClassName, fieldConfigs);
		    recordConfigs.add(recordConfig);
		}
		// create new field cofig list
		fieldConfigs = new ArrayList<FieldConfig>();
		lastRecordClassName = fieldConfig.getRecordClassName();
	    }

	    fieldConfigs.add(fieldConfig);
	}

	// set last list to new record config and add to record config list
	if (fieldConfigs != null) {
	    RecordConfig recordConfig = new RecordConfig(lastRecordClassName, fieldConfigs);
	    recordConfigs.add(recordConfig);
	}

	return recordConfigs;
    }
}
