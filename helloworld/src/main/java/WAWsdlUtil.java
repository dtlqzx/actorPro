import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * ��һ����ʹ��wsdl4j����wsdl��wsdl�ĵ��ṹ�Ƽ��ο�http://blog.csdn.net/wudouguerwxx/article/details/2036821
 *
 * @author wangleai
 */
public class WAWsdlUtil {

    private static Logger log = Logger.getLogger(WAWsdlUtil.class);

    private static WSDLFactory wsdlFactory;
    private static WSDLReader wsdlReader;

    private static DocumentBuilder documentBuilder;

    private static XPath xPath;

    /**
     * @return
     * @throws WSDLException
     */
    private static WSDLFactory getWsdlFactory() throws WSDLException {
        if (wsdlFactory == null) {
            wsdlFactory = WSDLFactory.newInstance();
        }
        return wsdlFactory;
    }

    /**
     * @return
     * @throws WSDLException
     */
    private static WSDLReader getWsdlReader() throws WSDLException {
        if (wsdlReader == null) {
            wsdlReader = getWsdlFactory().newWSDLReader();
            wsdlReader.setFeature("javax.wsdl.verbose", true);
            wsdlReader.setFeature("javax.wsdl.importDocuments", true);
        }
        return wsdlReader;
    }

    /**
     * @return
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder getDBBuilder() throws ParserConfigurationException {
        if (documentBuilder == null) {
            // �õ�DOM�������Ĺ���ʵ��
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            // ��DOM�����л��DOM������
            documentBuilder = dbFactory.newDocumentBuilder();
        }
        return documentBuilder;
    }

    /**
     * @param wsdlUrl
     * @return
     * @throws WSDLException
     */
    public static Document getDefinitionDocument(String wsdlUrl) throws WSDLException {
        // ��ȡwsdl����
        Definition def = getWsdlReader().readWSDL(wsdlUrl);
        // ת�ĵ���
        WSDLWriter writer = getWsdlFactory().newWSDLWriter();
        Document document = writer.getDocument(def);
        return document;
    }

    /**
     * �õ�document�Ĳ��ҹ���xpath,��֧�������ռ�
     *
     * @return
     */
    public static XPath getXPath() {
        if (xPath == null) {
            xPath = XPathFactory.newInstance().newXPath();
        }
        return xPath;
    }

    /**
     * �õ�wsdl�ĵ��з�����
     *
     * @param wsdlUrl       wsdl��ַ
     * @param operationList ��������
     * @throws WSDLException
     */
    public static void getOperationList(String wsdlUrl, List<String> operationList) throws WSDLException {
        // ��ȡwsdl����
        Definition def = getWsdlReader().readWSDL(wsdlUrl);

        // ����bindings
        Map bindings = def.getBindings();
        Iterator<Map.Entry> iterator = bindings.entrySet().iterator();
        while (iterator.hasNext()) {
            Binding binding = (Binding) iterator.next().getValue();
            if (binding != null) {
                List extEles = binding.getExtensibilityElements();
                if (extEles != null && extEles.size() > 0) {
                    ExtensibilityElement extensibilityElement = (ExtensibilityElement) extEles.get(0);
                    if (extensibilityElement != null) {
                        String namespaceUri = extensibilityElement.getElementType().getNamespaceURI();
                        // Ĭ��ʹ��soap1.1��binding����soapui����һ��
                        if (WAIXPathConstant.SOAPBINDING11.equals(namespaceUri)
                                || WAIXPathConstant.SOAPBINDING12.equals(namespaceUri)) {
                            List<Operation> operations = binding.getPortType().getOperations();
                            for (Operation ope : operations) {
                                operationList.add(ope.getName());
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * ���ݲ������Ʋ�ѯ������� �е㵰�ۣ�wsdl4j������ֱ��Ѱ��operation��api
     *
     * @param wsdlUrl
     * @param operationName
     * @return
     * @throws WSDLException
     */
    public static Operation getOperationByName(String wsdlUrl, String operationName) throws WSDLException {
        Operation targetOp = null;
        // ��ȡwsdl����
        Definition def = getWsdlReader().readWSDL(wsdlUrl);

        // ����bindings
        Map bindings = def.getBindings();
        Iterator<Map.Entry> iterator = bindings.entrySet().iterator();
        while (iterator.hasNext()) {
            Binding binding = (Binding) iterator.next().getValue();
            if (binding != null) {
                List extEles = binding.getExtensibilityElements();
                if (extEles != null && extEles.size() > 0) {
                    ExtensibilityElement extensibilityElement = (ExtensibilityElement) extEles.get(0);
                    if (extensibilityElement != null) {
                        String namespaceUri = extensibilityElement.getElementType().getNamespaceURI();
                        // Ĭ��ʹ��soap1.1��binding����soapui����һ��
                        if (WAIXPathConstant.SOAPBINDING11.equals(namespaceUri)) {
                            List<Operation> operations = binding.getPortType().getOperations();
                            for (Operation operation : operations) {
                                if (operation.getName().equals(operationName)) {
                                    targetOp = operation;
                                    break;
                                }
                            }
                            break;
                        }
                        if (WAIXPathConstant.SOAPBINDING12.equals(namespaceUri)) {
                            List<Operation> operations = binding.getPortType().getOperations();
                            for (Operation operation : operations) {
                                if (operation.getName().equals(operationName)) {
                                    targetOp = operation;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return targetOp;
    }

    /**
     * ��ȡ��Ӧ��������ϸ������������
     *
     * @param wsdlUrl
     * @param operationName ������
     * @return
     * @throws WSDLException
     */
    public static List<ParameterInfo> getMethodParams(String wsdlUrl, String operationName) throws WSDLException {
        Operation operation = getOperationByName(wsdlUrl, operationName);
        List<ParameterInfo> parameterInfoList = null;
        if (operation == null) {
            log.error("can not find operation " + operationName + " , please check again");
            throw new RuntimeException("can not find operation " + operationName + " , please check again");
        } else {
            // ����
            Map inputParts = operation.getInput().getMessage().getParts();
            parameterInfoList = findParamsByOperation(wsdlUrl, inputParts);
        }
        return parameterInfoList;
    }

    /**
     * ��ȡ��Ӧ�����ķ�������
     *
     * @param wsdlUrl
     * @param operationName ������
     * @return
     * @throws WSDLException
     */
    public static List<ParameterInfo> getMethodReturn(String wsdlUrl, String operationName) throws WSDLException {
        // �Ҹ�ϣ������ֵ�޶�Ϊjson�����᷽��ܶ�
        Operation operation = getOperationByName(wsdlUrl, operationName);
        List<ParameterInfo> parameterInfoList = null;
        if (operation == null) {
            log.error("can not find operation " + operationName + " , please check again");
            throw new RuntimeException("can not find operation " + operationName + " , please check again");
        } else {
            // ���
            Map outputParts = operation.getOutput().getMessage().getParts();
            parameterInfoList = findParamsByOperation(wsdlUrl, outputParts);
        }
        return parameterInfoList;
    }

    /**
     * ��ȡwsdl�������Ͷ�������Types��XML Schemaǰ׺������ƴ���γ�������ǩ Ŀǰ����Ҫʹ�ã���Ϊ��XML Schema�����������ռ�
     *
     * @param wsdlUrl
     * @return
     * @throws WSDLException
     */
    @Deprecated
    public static String getSchemaPrefix(String wsdlUrl) throws WSDLException {
        String prefix = "";
        // ��ȡwsdl����
        Definition def = getWsdlReader().readWSDL(wsdlUrl);

        // ��ȡTypes XML Schema�ĵ�����
        Types types = def.getTypes();

        // ��ȡ��ǩǰ׺����
        ExtensibilityElement extensibilityElement = (ExtensibilityElement) types.getExtensibilityElements().get(0);
        Schema schema = (Schema) extensibilityElement;
        prefix = schema.getElement().getPrefix();
        if (!StringUtils.isBlank(prefix)) {
            // ǰ׺�ǿ�ʱ����:
            prefix += ":";
        } else {
            prefix = "";
        }
        return prefix;
    }

    /**
     * �������е�import���
     *
     * @param document
     * @param xpath
     * @return
     * @throws Exception
     */
    public static List<Document> getImportDocumentList(Document document, XPath xpath) throws Exception {
        List<Document> importDocumentList = new ArrayList<>();
        // ����def�����е�import
        NodeList importNodeList = (NodeList) xpath.evaluate(WAIXPathConstant.IMPORTXPATH, document,
                XPathConstants.NODESET);
        if (importNodeList != null) {
            for (int i = 0; i < importNodeList.getLength(); i++) {
                Node importNode = importNodeList.item(i);
                String location = DOMUtil.getAttributeValue(importNode, "location");
                // ��Ҫ������xml�ĵ�����DOM������
                if (!StringUtils.isBlank(location)) {
                    Document importDocument = getDefinitionDocument(location);
                    importDocumentList.add(importDocument);
                }
            }
        }

        // ����schema�����е�import
        NodeList importSchemaNodeList = (NodeList) xpath.evaluate(WAIXPathConstant.SCHEMAIMPORTPATH, document,
                XPathConstants.NODESET);
        if (importSchemaNodeList != null) {
            for (int i = 0; i < importSchemaNodeList.getLength(); i++) {
                Node importNode = importSchemaNodeList.item(i);
                String location = DOMUtil.getAttributeValue(importNode, "schemaLocation");
                // ��Ҫ������xml�ĵ�����DOM������
                if (!StringUtils.isBlank(location)) {
                    Document importDocument = getDBBuilder().parse(location);
                    importDocumentList.add(importDocument);
                }
            }
        }
        return importDocumentList;
    }

    /**
     * ����import Ŀǰ��ʹ��
     *
     * @param definition
     * @param operationList
     * @throws WSDLException
     */
    private static void findImport(Definition definition, List<String> operationList) throws WSDLException {
        Map imports = definition.getImports();
        Iterator<Map.Entry> iterator = imports.entrySet().iterator();
        while (iterator.hasNext()) {
            Import anImport = (Import) iterator.next().getValue();
            log.info("import nameSpace:" + anImport.getNamespaceURI() + ",location:" + anImport.getLocationURI());
            // �ݹ�
            getOperationList(anImport.getLocationURI(), operationList);
        }
    }

    /**
     * ����partѰ�Ҷ�Ӧ��������
     *
     * @param wsdlUrl
     * @param parts
     * @return
     * @throws WSDLException
     */
    private static List<ParameterInfo> findParamsByOperation(String wsdlUrl, Map parts) throws WSDLException {
        Document document = getDefinitionDocument(wsdlUrl);
        // ����list
        List<ParameterInfo> paramsList = new ArrayList<>();
        String schemaXPath = WAIXPathConstant.SCHEMAXPATH;

        Iterator<Map.Entry> entryIterator = parts.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Part part = (Part) entryIterator.next().getValue();
            // RPC��ʽ��ȡtype����ʱ���󲢷�soapЭ�飬��ʹ��
            if (part.getTypeName() != null) {
                String typeName = part.getTypeName().getLocalPart();
            }
            // �ĵ���ʽ��ȡelement
            if (part.getElementName() != null) {
                String typeName = part.getElementName().getLocalPart();
                try {
                    WADOMUtil.getInputParams(paramsList, document, typeName, schemaXPath, null, true);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return paramsList;
    }

    /**
     * ����XPath�Ƿ���ڽ�㣬һ������
     *
     * @param wsdlUrl
     * @param path
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws SAXException
     * @throws WSDLException
     */
    @Deprecated
    public static void testXpath(String wsdlUrl, String path)
            throws ParserConfigurationException, XPathExpressionException, IOException, SAXException, WSDLException {
        Document document = getDefinitionDocument(wsdlUrl);
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node node = (Node) xpath.evaluate(path, document, XPathConstants.NODE);
        if (node != null) {
            System.out.println("ok");
        }
    }
}
