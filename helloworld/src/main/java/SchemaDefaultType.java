/**
 * @author wangleai
 * @date 2018/3/1
 * ���������Ը��ݹ���xml schema�ĵ��ṩ����������������ͣ�����֧�ָ������ݣ������ֻ���java��صĻ�������
 */
public enum SchemaDefaultType {
    type_string("string"),
    type_decimal("decimal"),
    type_integer("integer"),
    type_int("int"),
    type_float("float"),
    type_long("long"),
    type_boolean("boolean"),
    type_time("time"),
    type_date("date"),
    type_datetime("datetime"),
    type_array("array"),
    type_any("any"),
    type_arrayOfString("ArrayOfString"),
    type_object("Object"),
    type_anyType("anyType");

    private String type;

    private SchemaDefaultType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
