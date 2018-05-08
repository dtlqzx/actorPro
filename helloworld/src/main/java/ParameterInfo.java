import java.util.ArrayList;
import java.util.List;

/**
 * @author wangleai
 * @date 2018/3/1
 */
public class ParameterInfo {
    /**
     * ����
     */
    private String name;

    /**
     * ֵ�����������ͻᱻ����
     */
    private String value;

    /**
     * ���ͣ�����δ��ʱ��ʾ�Լ������complexType
     */
    private String type;

    /**
     * �����ͣ�����ʶ��array�����µ�type��Ӧ���ǲ��õ���arrayǶ��array�ģ�Ŀǰ�Ҳ��Ե�java
     * webservice����޷��Զ����ɶ�Ӧ��wsdl�ĵ�
     */
    private String childType;

    /**
     * �ӽ��
     */
    private List<ParameterInfo> children = new ArrayList();

    /**
     * �Ҹ��˽����Ǽ����������㣬�����Ժ�����ã������������Ҳ�δʹ�õ�
     */
    private ParameterInfo parentParam;

    public ParameterInfo() {
    }

    public ParameterInfo(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public ParameterInfo(String name, String value, String type, String childType, List<ParameterInfo> children) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.childType = childType;
        this.children = children;
        this.parentParam = null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ParameterInfo> getChildren() {
        return this.children;
    }

    public void setChildren(List<ParameterInfo> children) {
        this.children = children;
    }

    public String getChildType() {
        return this.childType;
    }

    public void setChildType(String childType) {
        this.childType = childType;
    }

    public void addChild(ParameterInfo param) {
        this.children.add(param);
    }

    public ParameterInfo getParentParam() {
        return parentParam;
    }

    public void setParentParam(ParameterInfo parentParam) {
        this.parentParam = parentParam;
    }
}
