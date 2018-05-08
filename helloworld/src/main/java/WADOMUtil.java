import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * �ڶ�����ʹ��DOM����XML Schema����ȡԪ�����ƺ�����
 *
 * @author wangleai
 */
public class WADOMUtil {

    private static Logger log = Logger.getLogger(WADOMUtil.class);

    /**
     * ����ĳ���������еĲ���
     *
     * @param inputParamList
     * @param document
     * @param typeName         ��������
     * @param xPath            ��element�ϸ�����xpath
     * @param parentParam
     * @param isSelfDefinition �Ƿ��Լ�����
     * @throws Exception
     */
    public static void getInputParams(List<ParameterInfo> inputParamList, Document document, String typeName,
                                      String xPath, ParameterInfo parentParam, boolean isSelfDefinition) throws
            Exception {
        // �Ƿ���ӽ��
        boolean canAddParam = true;

        // ����
        ParameterInfo param = new ParameterInfo();
        param.setName(typeName);

        // Ѱ��element�ڵ�
        String elementXPath = getElementXPath(xPath, typeName);
        Node elementNode = WADOMUtil.findNode(document, elementXPath);

        // �ж�element�Ƿ�Ϊ�գ�Ϊ��˵�����������ƿ�����ֱ�Ӷ�����simpleType��complexType��
        // �ж��Ƿ����Լ����壬��ֹͬ�����µĶ�ջ�������
        if (elementNode != null && isSelfDefinition) {
            // �Ƿ�type����
            if (DOMUtil.assertNodeAttributeExist(elementNode, "type")) {
                String type = DOMUtil.getNodeType(elementNode);

                // �Ƿ������飬XML Schema��map�ᶨ���List<Object>����ʽ
                if (DOMUtil.isArray(elementNode)) {
                    param.setType(SchemaDefaultType.type_array.getType());
                    param.setChildType(type);

                    // ������������
                    if (!DOMUtil.isDefaultType(elementNode)) {
                        getInputParams(inputParamList, document, type, xPath, param, false);
                    } else {
                        param.setValue(WAIXPathConstant.VALUEDEF);
                    }
                } else {
                    param.setType(type);

                    // ��������
                    if (!DOMUtil.isDefaultType(elementNode)) {
                        getInputParams(inputParamList, document, type, WAIXPathConstant.SCHEMAXPATH, param, false);
                    } else {
                        param.setValue(WAIXPathConstant.VALUEDEF);
                    }
                }
            } else {
                // �Ƿ������飬XML Schema��map�ᶨ���List<Object>����ʽ
                if (DOMUtil.isArray(elementNode)) {
                    param.setType(SchemaDefaultType.type_array.getType());
                }

                // ���type���Բ����ڣ�˵���ý������������ӽ���ж���
                // �ж���simpleType����complexType
                Node simpleTypeNode = getSimpleTypeNode(document, typeName, elementXPath, true);
                if (simpleTypeNode != null) {
                    String type = DOMUtil.getNodeBase(simpleTypeNode);
                    if (DOMUtil.isArray(elementNode)) {
                        param.setChildType(type);
                    } else {
                        param.setType(type);
                    }
                    param.setValue(WAIXPathConstant.VALUEDEF);
                    // TODO: 2018/2/24
                    // �����޶�����
                } else {
                    // Ѱ��complexType
                    List<Node> nodeList = getComplexTypeSequenceElement(document, typeName, elementXPath, true);
                    if (nodeList.size() != 0) {
                        for (Node tempNode : nodeList) {
                        	/*���any���������⴦��*/
                        	String anyXpath = elementXPath + "/*[local-name()='complexType']/*[local-name()='sequence']/*[local-name()='any']";
                        	NodeList anyNodeList = findNodeList(document,anyXpath);
                        	List<Node> anyList = new ArrayList<Node>();
                        	anyList = DOMUtil.covertNodeListToList(anyNodeList);
                        	if(!anyList.isEmpty())
                        	{
                        		inputParamList.add(new ParameterInfo("any","Object"));
                        	}
                        	/*���any���������⴦�� ����*/
                            // ����element
                            String elementName = DOMUtil.getNodeName(tempNode);
                            String sequenceXPath = getSequenceXPathByName(document, typeName, elementXPath, true,
                                    elementName);
                            getInputParams(inputParamList, document, elementName, sequenceXPath, param, true);
                        }
                    } else {
                        log.warn("unknown type " + typeName + ",please check your document");
                    }
                }
            }
        } else {
            // ���Լ������Type��ӵ������
            canAddParam = false;

            // �ж���simpleType����complexType
            Node simpleTypeNode = getSimpleTypeNode(document, typeName, xPath, false);
            if (simpleTypeNode != null) {
                String type = DOMUtil.getNodeBase(simpleTypeNode);
                parentParam.setType(type);
                parentParam.setValue(WAIXPathConstant.VALUEDEF);
                // TODO: 2018/2/24
                // �����޶�����
            } else {
                // Ѱ��complexType
                List<Node> nodeList = getComplexTypeSequenceElement(document, typeName, elementXPath, false);
                if (nodeList.size() != 0) {
                    for (Node tempNode : nodeList) {
                        // ����element
                        String elementName = DOMUtil.getNodeName(tempNode);
                        String sequenceXPath = getSequenceXPathByName(document, typeName, elementXPath, false,
                                elementName);
                        getInputParams(inputParamList, document, elementName, sequenceXPath, parentParam, true);
                    }
                } else {
                    log.warn("unknown type " + typeName + ",please check your document");
                }
            }
        }
        if (canAddParam) {
            if (parentParam == null) {
                inputParamList.add(param);
            } else {
                parentParam.addChild(param);
            }
        }
    }

    /**
     * ��ȡelement��xpath
     *
     * @param xPath    �����xpath
     * @param typeName ����
     * @return
     */
    private static String getElementXPath(String xPath, String typeName) {
        String elementXPath = xPath + "/*[local-name()='element' and @name='" + typeName + "']";
        return elementXPath;
    }

    /**
     * ��ȡsimpleType���
     *
     * @param document
     * @param simpleTypeName   simpleType���ƣ���Ϊ�Լ�����ʱ����Ϊ��
     * @param elementXPath     XPath·���������Լ�����ʱ����Ϊ��
     * @param isSelfDefinition �Ƿ�Ϊ�Լ�����
     * @return
     * @throws Exception
     */
    private static Node getSimpleTypeNode(Document document, String simpleTypeName, String elementXPath,
                                          boolean isSelfDefinition) throws Exception {
        String simpleTypeXPath = isSelfDefinition
                ? elementXPath + "/*[local-name()='simpleType']/*[local-name()='restriction']"
                : WAIXPathConstant.SCHEMAXPATH + "/*[local-name()='simpleType' and @name='" + simpleTypeName
                + "']/*[local-name()='restriction']";
        Node node = WADOMUtil.findNode(document, simpleTypeXPath);
        return node;
    }

    /**
     * ��ȡcomplexType��element
     *
     * @param document
     * @param complexTypeName  complexType���ƣ���Ϊ�Լ�����ʱ����Ϊ��
     * @param elementXPath     XPath·���������Լ�����ʱ����Ϊ��
     * @param isSelfDefinition �Ƿ�Ϊ�Լ�����
     * @return
     * @throws Exception
     */
    private static List<Node> getComplexTypeSequenceElement(Document document, String complexTypeName,
                                                            String elementXPath, boolean isSelfDefinition) throws
            Exception {
        List<Node> nodeList = new ArrayList<Node>();
        // �ж��Ƿ��м̳�
        String extensionXpath = isSelfDefinition
                ? elementXPath + "/*[local-name()='complexType']/*[local-name()='complexContent']/*[local-name()"
                + "='extension']"
                : WAIXPathConstant.SCHEMAXPATH + "/*[local-name()='complexType' and @name='" + complexTypeName
                + "']/*[local-name()='complexContent']/*[local-name()='extension']";
        Node extension = WADOMUtil.findNode(document, extensionXpath);
        if (extension != null) {
            // ���ڼ̳�
            // ��Ӹ���
            String parentTypeName = DOMUtil.getAttributeValue(extension, "base").split(":")[1];
            List<Node> parentElements = getComplexTypeSequenceElement(document, parentTypeName, elementXPath, false);
            if (parentElements != null && parentElements.size() > 0) {
                nodeList.addAll(parentElements);
            }
            // �����Լ�
            String selfXpath = extensionXpath + "/*[local-name()='sequence']/*[local-name()" + "='element']";
            NodeList selfList = WADOMUtil.findNodeList(document, selfXpath);
            if (selfList != null && selfList.getLength() > 0) {
                nodeList.addAll(DOMUtil.covertNodeListToList(selfList));
            }
        } else {
            // �����Լ�������
            String elementsOfSequenceXpath = isSelfDefinition
                    ? elementXPath + "/*[local-name()='complexType']/*[local-name()='sequence']/*[local-name()"
                    + "='element']"
                    : WAIXPathConstant.SCHEMAXPATH + "/*[local-name()='complexType' and @name='" + complexTypeName
                    + "']/*[local-name()='sequence']/*[local-name()='element']";
            NodeList elementsOfSequence = WADOMUtil.findNodeList(document, elementsOfSequenceXpath);
            nodeList = DOMUtil.covertNodeListToList(elementsOfSequence);
        }
        return nodeList;
    }

    /**
     * ��ȡcomplexType��element��һ��sequence��xpath
     *
     * @param document
     * @param complexTypeName  complexType���ƣ���Ϊ�Լ�����ʱ����Ϊ��
     * @param elementXPath     XPath·���������Լ�����ʱ����Ϊ��
     * @param isSelfDefinition �Ƿ�Ϊ�Լ�����
     * @param elementName      �������
     * @return
     * @throws Exception
     */
    private static String getSequenceXPathByName(Document document, String complexTypeName, String elementXPath,
                                                 boolean isSelfDefinition, String elementName) throws Exception {
        String result = "";
        // �ж��Ƿ��м̳�
        String extensionXpath = isSelfDefinition
                ? elementXPath + "/*[local-name()='complexType']/*[local-name()='complexContent']/*[local-name()"
                + "='extension']"
                : WAIXPathConstant.SCHEMAXPATH + "/*[local-name()='complexType' and @name='" + complexTypeName
                + "']/*[local-name()='complexContent']/*[local-name()='extension']";
        Node extension = WADOMUtil.findNode(document, extensionXpath);
        if (extension != null) {
            // ���ڼ̳�
            // �ڸ������
            String parentTypeName = DOMUtil.getAttributeValue(extension, "base").split(":")[1];
            result = getSequenceXPathByName(document, parentTypeName, elementXPath, false, elementName);

            // ���Լ�����
            String sequenceXPath = extensionXpath + "/*[local-name()='sequence']";
            String eleXPath = sequenceXPath + "/*[local-name()='element' and @name='" + elementName + "']";
            Node selfNode = WADOMUtil.findNode(document, eleXPath);
            if (selfNode != null) {
                result = sequenceXPath;
            }
        } else {
            // ���Լ�����
            String sequenceXPath = isSelfDefinition
                    ? elementXPath + "/*[local-name()='complexType']/*[local-name()='sequence']"
                    : WAIXPathConstant.SCHEMAXPATH + "/*[local-name()='complexType' and @name='" + complexTypeName
                    + "']/*[local-name()='sequence']";
            String eleXpath = sequenceXPath + "/*[local-name()='element' and @name='" + elementName + "']";
            Node selfNode = WADOMUtil.findNode(document, eleXpath);
            if (selfNode != null) {
                result = sequenceXPath;
            }
        }
        return result;
    }

    /**
     * ��document�в��ҽ�㣬������Ҳ����������import����еݹ����
     *
     * @param document
     * @param xpathStr
     * @return
     * @throws Exception
     */
    public static Node findNode(Document document, String xpathStr) throws Exception {
        XPath xpath = WAWsdlUtil.getXPath();
        Node node = (Node) xpath.evaluate(xpathStr, document, XPathConstants.NODE);
        if (node == null) {
            List<Document> importDocumentList = WAWsdlUtil.getImportDocumentList(document, xpath);
            for (Document importDoucment : importDocumentList) {
                node = findNode(importDoucment, xpathStr);
                if (node != null) {
                    return node;
                }
            }
        }
        return node;
    }

    /**
     * ��document�в��ҽ�㣬������Ҳ����������import����еݹ����
     *
     * @param document
     * @param xpathStr
     * @return
     * @throws Exception
     */
    public static NodeList findNodeList(Document document, String xpathStr) throws Exception {
        XPath xpath = WAWsdlUtil.getXPath();
        NodeList nodeList = (NodeList) xpath.evaluate(xpathStr, document, XPathConstants.NODESET);
        if (nodeList.getLength() == 0) {
            List<Document> importDocumentList = WAWsdlUtil.getImportDocumentList(document, xpath);
            for (Document importDoucment : importDocumentList) {
                nodeList = findNodeList(importDoucment, xpathStr);
                if (nodeList.getLength() > 0) {
                    return nodeList;
                }
            }
        }
        return nodeList;
    }
}
