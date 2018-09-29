
public class FieldConfig {

    private String recordClassName;
    private String fieldName;
    private String fieldDesc;
    private String offset;
    private String length;
    private String align;
    private String paddingChar;
    private String format;
    private String dataType;
    private String validation;
    private String defaultValue;
    private String constantValue;

    /**
     * @return the recordClassName
     */
    public final String getRecordClassName() {
	return recordClassName;
    }

    /**
     * @param recordClassName
     *            the recordClassName to set
     */
    public final void setRecordClassName(String recordName) {
	this.recordClassName = recordName;
    }

    /**
     * @return the fieldName
     */
    public final String getFieldName() {
	return fieldName;
    }

    /**
     * @param fieldName
     *            the fieldName to set
     */
    public final void setFieldName(String fieldName) {
	this.fieldName = fieldName;
    }

    /**
     * @return the offset
     */
    public final String getOffset() {
	return offset;
    }

    /**
     * @param offset
     *            the offset to set
     */
    public final void setOffset(String offset) {
	this.offset = offset;
    }

    /**
     * @return the length
     */
    public final String getLength() {
	return length;
    }

    /**
     * @param length
     *            the length to set
     */
    public final void setLength(String length) {
	this.length = length;
    }

    /**
     * @return the align
     */
    public final String getAlign() {
	return align;
    }

    /**
     * @param align
     *            the align to set
     */
    public final void setAlign(String align) {
	this.align = (align != null && align.equalsIgnoreCase("RIGHT")) ? "RIGHT" : null;
    }

    /**
     * @return the paddingChar
     */
    public final String getPaddingChar() {
	return paddingChar;
    }

    /**
     * @param paddingChar
     *            the paddingChar to set
     */
    public final void setPaddingChar(String paddingChar) {
	this.paddingChar = paddingChar;
    }

    /**
     * @return the format
     */
    public final String getFormat() {
	return format;
    }

    /**
     * @param format
     *            the format to set
     */
    public final void setFormat(String format) {
	this.format = format;
    }

    /**
     * @return the dataType
     */
    public final String getDataType() {
	return dataType;
    }

    /**
     * @param dataType
     *            the dataType to set
     */
    public final void setDataType(String dataType) {
	if (dataType.equalsIgnoreCase("date")) {
	    this.dataType = "java.util.Date";
	} else if (dataType.equalsIgnoreCase("Integer")) {
	    this.dataType = "java.lang.Integer";
	} else {
	    this.dataType = dataType;
	}
    }

    /**
     * @return the validation
     */
    public final String getValidation() {
	return validation;
    }

    /**
     * @param validation
     *            the validation to set
     */
    public final void setValidation(String validation) {
	this.validation = validation;
    }

    /**
     * @return the defaultValue
     */
    public final String getDefaultValue() {
	return defaultValue;
    }

    /**
     * @param defaultValue
     *            the defaultValue to set
     */
    public final void setDefaultValue(String defaultValue) {
	this.defaultValue = defaultValue;
    }

    /**
     * @return the constantValue
     */
    public final String getConstantValue() {
	return constantValue;
    }

    /**
     * @param constantValue
     *            the constantValue to set
     */
    public final void setConstantValue(String constantValue) {
	this.constantValue = constantValue;
    }

    /**
     * @return the fieldDesc
     */
    public final String getFieldDesc() {
	return fieldDesc;
    }

    /**
     * @param fieldDesc
     *            the fieldDesc to set
     */
    public final void setFieldDesc(String fieldDesc) {
	this.fieldDesc = fieldDesc;
    }
}
