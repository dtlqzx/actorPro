import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * DOM����������
 *
 * @author sjw
 * @version [�汾��, 2017��12��17��]
 * @see [�����/����]
 * @since [��Ʒ/ģ��汾]
 */
public class DOMUtil {
    /**
     * Serialise the supplied W3C DOM subtree.
     * <p/>
     * The output is unformatted.
     *
     * @param nodeList The DOM subtree as a NodeList.
     * @return The subtree in serailised form.
     * @throws DOMException Unable to serialise the DOM.
     */
    public static String serialize(NodeList nodeList) throws DOMException {
        return serialize(nodeList, false);
    }

    /**
     * Serialise the supplied W3C DOM subtree.
     *
     * @param node   The DOM node to be serialized.
     * @param format Format the output.
     * @return The subtree in serailised form.
     * @throws DOMException Unable to serialise the DOM.
     */
    public static String serialize(final Node node, boolean format) throws DOMException {
        StringWriter writer = new StringWriter();
        serialize(node, format, writer);
        return writer.toString();
    }

    /**
     * the supplied W3C DOM subtree.
     *
     * @param node   The DOM node to be serialized.
     * @param format Format the output.
     * @param writer The target writer for serialization.
     * @throws DOMException Unable to serialise the DOM.
     */
    public static void serialize(final Node node, boolean format, Writer writer) throws DOMException {
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            serialize(node.getChildNodes(), format, writer);
        } else {
            serialize(new NodeList() {
                @Override
                public Node item(int index) {
                    return node;
                }

                @Override
                public int getLength() {
                    return 1;
                }
            }, format, writer);
        }
    }

    /**
     * Serialise the supplied W3C DOM subtree.
     *
     * @param nodeList The DOM subtree as a NodeList.
     * @param format   Format the output.
     * @return The subtree in serailised form.
     * @throws DOMException Unable to serialise the DOM.
     */
    public static String serialize(NodeList nodeList, boolean format) throws DOMException {
        StringWriter writer = new StringWriter();
        serialize(nodeList, format, writer);
        return writer.toString();
    }

    /**
     * Serialise the supplied W3C DOM subtree.
     *
     * @param nodeList The DOM subtree as a NodeList.
     * @param format   Format the output.
     * @param writer   The target writer for serialization.
     * @throws DOMException Unable to serialise the DOM.
     */
    public static void serialize(NodeList nodeList, boolean format, Writer writer) throws DOMException {
        try {
            // nodeListΪ��
            if (nodeList == null) {
                throw new IllegalArgumentException(
                        "XmlUtil.serialize(NodeList nodeList, boolean format, Writer writer)�в���nodeListΪ");
            }
            TransformerFactory factory = TransformerFactory.newInstance();
            // ���ø�ʽ��
            if (format) {
                try {
                    // (����jdk1.5)�������������������1.4�ϻ��׳��쳣
                    factory.setAttribute("indent-number", new Integer(4));
                } catch (Exception e) {
                    throw new RuntimeException("����TransformerFactory��������Ϊ4ʧ��:" + e.getMessage());
                }
            }
            // ȡ��transformer
            Transformer transformer = factory.newTransformer();
            // ���ñ����ʽ
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            // �����Ƿ����xml����Ƭ��
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            if (format) {
                // ����xml�Ƿ������������
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                // (����jdk1.4)Ҳ��������дhttp://xml.apache.org/xslt}indent-amount������ֻ�������ռ䲻һ��
                transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
            }
            // �������еĽ��
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (isTextNode(node)) {
                    // ������ı���㣬��ֱ�����
                    writer.write(node.getNodeValue());
                } else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                    writer.write(((Attr) node).getValue());
                } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    transformer.transform(new DOMSource(node), new StreamResult(writer));
                }
            }
        } catch (Exception e) {
            DOMException domExcep = new DOMException(DOMException.INVALID_ACCESS_ERR,
                    "Unable to serailise DOM subtree.");
            domExcep.initCause(e);
            throw domExcep;
        }
    }

    /**
     * �ж�node�Ƿ�Ϊ�ı����
     *
     * @param node
     * @return
     */
    public static boolean isTextNode(Node node) {
        short nodeType;

        if (node == null) {
            return false;
        }
        nodeType = node.getNodeType();

        return nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE;
    }

    /**
     * �жϽ��������Ƿ����
     *
     * @param node
     * @param attributeName
     * @return
     */
    public static boolean assertNodeAttributeExist(Node node, String attributeName) {
        boolean result = false;
        if (node != null) {
            NamedNodeMap attributeMap = node.getAttributes();
            Node attributeNode = attributeMap.getNamedItem(attributeName);
            if (attributeNode != null) {
                if (StringUtils.isNotEmpty(attributeNode.getNodeValue())) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * ��NodeListת��ΪList<Node>
     *
     * @param nodeList
     * @return
     * @throws Exception
     */
    public static List<Node> covertNodeListToList(NodeList nodeList) throws Exception {
        List<Node> list = new ArrayList<Node>();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                list.add(nodeList.item(i));
            }
        }
        return list;
    }

    /**
     * �õ���������ֵ
     *
     * @param node
     * @param attributeName
     * @return
     * @throws Exception
     */
    public static String getAttributeValue(Node node, String attributeName) throws Exception {
        String attributeValue = "";
        if (node != null) {
            NamedNodeMap attributeMap = node.getAttributes();
            Node attributeNode = attributeMap.getNamedItem(attributeName);
            if (attributeNode != null) {
                attributeValue = attributeNode.getNodeValue();
            }
        }
        return attributeValue;
    }

    /**
     * �õ�����name����ֵ
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static String getNodeName(Node node) throws Exception {
        return getAttributeValue(node, "name");
    }

    /**
     * �õ�����type����ֵ
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static String getNodeType(Node node) throws Exception {
        String type = getAttributeValue(node, "type");
        if (StringUtils.isNotEmpty(type)) {
            if (type.indexOf(":") >= 0) {
                return type.split(":")[1];
            } else {
                return type;
            }
        }
        return "";
    }

    /**
     * �õ�����base����ֵ��Ŀǰ������simpletype�����restriction���
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static String getNodeBase(Node node) throws Exception {
        String type = getAttributeValue(node, "base");
        if (StringUtils.isNotEmpty(type)) {
            if (type.indexOf(":") >= 0) {
                return type.split(":")[1];
            } else {
                return type;
            }
        }
        return "";
    }

    /**
     * �õ�����maxOccurs����ֵ
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static String getNodeMaxOccurs(Node node) throws Exception {
        return getAttributeValue(node, "maxOccurs");
    }

    /**
     * �õ�����minOccurs����ֵ
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static String getNodeMinOccurs(Node node) throws Exception {
        return getAttributeValue(node, "minOccurs");
    }

    /**
     * �ж�type�Ƿ�ΪschemaĬ�ϵ�����
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static boolean isDefaultType(Node node) throws Exception {
        boolean result = false;
        if (node != null) {
            String type = DOMUtil.getNodeType(node);
            SchemaDefaultType[] defaultTypes = SchemaDefaultType.values();
            for (int i = 0; i < defaultTypes.length; i++) {
                SchemaDefaultType defaultType = defaultTypes[i];
                if (type.equals(defaultType.getType())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * �ж�element�Ƿ�Ϊ��������
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static boolean isArray(Node node) throws Exception {
        boolean result = false;
        if (node != null) {
            String minOccurs = DOMUtil.getNodeMinOccurs(node);
            String maxOccurs = DOMUtil.getNodeMaxOccurs(node);
            boolean marker = maxOccurs != null && !"".equals(maxOccurs)
                    && ("unbounded".equals(maxOccurs) || Integer.valueOf(maxOccurs) > 1);
            if (marker) {
                result = true;
            }
        }
        return result;
    }

    public static void removeTextNode(Node root) {
        if (root.hasChildNodes()) {
            NodeList children = root.getChildNodes();
            int count = children.getLength();
            for (int i = count - 1; i >= 0; i--) {
                // ��Ҫ�Ӻ���ǰɾ������ֹ����ɳ©ЧӦ
                Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.COMMENT_NODE) {
                    child.getParentNode().removeChild(child);
                } else {
                    removeTextNode(child);
                }
            }
        }
    }
}
