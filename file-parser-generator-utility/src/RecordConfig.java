import java.util.List;

public class RecordConfig {

    private String recordClassName;

    private List<FieldConfig> fieldConfigs;

    /**
     * @param recordClassName
     * @param fieldConfigs
     */
    public RecordConfig(String recordClassName, List<FieldConfig> fieldConfigs) {
	if (recordClassName == null) {
	    throw new RuntimeException("Record Class Name is mandatory.");
	}
	this.recordClassName = recordClassName;
	this.fieldConfigs = fieldConfigs;
    }

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
    public final void setRecordClassName(String recordClassName) {
	this.recordClassName = recordClassName;
    }

    /**
     * @return the fieldConfigs
     */
    public final List<FieldConfig> getFieldConfigs() {
	return fieldConfigs;
    }

    /**
     * @param fieldConfigs
     *            the fieldConfigs to set
     */
    public final void setFieldConfigs(List<FieldConfig> fieldConfigs) {
	this.fieldConfigs = fieldConfigs;
    }

}
