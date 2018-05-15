import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;
import javax.wsdl.WSDLException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangleai on 2018/3/1.
 */
public class Test {
    public static void main(String[] args) throws WSDLException 
    {
//		DOMConfigurator.configure("log4j.xml");//加载.xml文件
//		PropertyConfigurator.configure("log4j.properties");//加载.properties522文件
//		Logger log=Logger.getLogger("org.zblog.test");
		
        String wsdluri = "F:/研究生/实验室/WSDL文件/MobileCodeWS.xml";
        List<String> operations = new ArrayList<>();
        WAWsdlUtil.getOperationList(wsdluri, operations);//getOperationList返回值是将操作名传入operations这个List
        List<Ope> operationList = new ArrayList<Ope>();
        createDataModel(operationList,operations,wsdluri);
        generateCodeFile(operationList,"./template","Actor1.ftl",new OutputStreamWriter(System.out));

    }

    private static void printParams(List<ParameterInfo> parameterInfos, String parentName) {
        if (parameterInfos != null) {
            for (ParameterInfo parameterInfo : parameterInfos) {
                System.out.println("parentname : " + parentName + " ; name : " + parameterInfo.getName() + " ; type :" +
                        " " + parameterInfo.getType() + " ;" +
                        " childtype : " + parameterInfo.getChildType());
                printParams(parameterInfo.getChildren(), parameterInfo.getName());
            }
        }
    }
    private static void findChildren(List<ParameterInfo> Children,List<ParameterInfo> parameterInfos, String parentName)
    {
        if (parameterInfos != null) {
            for (ParameterInfo parameterInfo : parameterInfos) {
            	if(parameterInfo.getType()!=null)
            	{
            		Children.add(parameterInfo);
            	}
            	findChildren(Children,parameterInfo.getChildren(), parameterInfo.getName());
            }
        }
     	
    }
    private static void createDataModel(List operationList,List<String> operations,String wsdluri) throws WSDLException
    {
        List<ParameterInfo> parameterChildren = new ArrayList<ParameterInfo>();
        List<ParameterInfo> returnChildren = new ArrayList<ParameterInfo>();
        for (String operationName : operations) 
        {
            System.out.println("-----------------operation----------------");
            System.out.println(operationName);
            List<ParameterInfo> parameterInfos = WAWsdlUtil.getMethodParams(wsdluri, operationName);
            List<ParameterInfo> returnInfos = WAWsdlUtil.getMethodReturn(wsdluri, operationName);
            printParams(parameterInfos, "");
            printParams(returnInfos, "");
            
            Ope buf = new Ope();
            buf.setOpName(operationName);
            findChildren(parameterChildren, parameterInfos, "");
            findChildren(returnChildren, returnInfos, "");            
            buf.setOpparams(parameterChildren);
            buf.setOpreturn(returnChildren);
            operationList.add(buf);
            
            parameterChildren = new ArrayList<ParameterInfo>();
            returnChildren = new ArrayList<ParameterInfo>();
        }
        System.out.println("over");
    }
    //generateCodeFile(operationList,"./template","wsftl.ftl",System.out)
    private static void generateCodeFile(List<Ope> modelList,String templatePath,String ftlFile,Writer out)
    {
        File javaFile = null;
        Serv serv = new Serv();
        Configuration cfg = new Configuration();
        try {
            // 步骤一：指定 模板文件从何处加载的数据源，这里设置一个文件目录
            cfg.setDirectoryForTemplateLoading(new File(templatePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            // 步骤二：获取 模板文件
            Template template = cfg.getTemplate(ftlFile);

            // 步骤三：创建 数据模型
            Map<String,Object> root = new HashMap<String, Object>();
            serv.setOpes(modelList);
            root.put("serv", serv);
            // 步骤四：合并 模板 和 数据模型
            // 创建.java类文件
            if(javaFile != null){
                Writer javaWriter = new FileWriter(javaFile);
                template.process(root, javaWriter);
                javaWriter.flush();
                System.out.println("文件生成路径：" + javaFile.getCanonicalPath());
                javaWriter.close();
            }
            // 输出到Console控制台
//            Writer out = new OutputStreamWriter(System.out);
            template.process(root, out);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
}
