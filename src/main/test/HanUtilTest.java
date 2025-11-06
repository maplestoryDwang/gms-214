import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName HanUtilTest.java
 * @Description 漢化
 * @createTime 2025-11-04 22:25
 */

public class HanUtilTest {

    public static void main(String[] args) throws Exception {

        String sourcePath = "E:\\oldmxd\\gms214\\中文\\UI\\20251104\\英语";
        String chinesePath = "E:\\oldmxd\\gms214\\中文\\UI\\20251104\\简体";
        String outDir = "E:\\oldmxd\\gms214\\中文\\UI\\20251104\\out";




        String fileName = "UIWindow2.img.xml";

        File sourceFile = new File(sourcePath, fileName);
        File chineseFile = new File(chinesePath, fileName);


        Document sourceDoc = parseXML(sourceFile);
        Document chineDoc = parseXML(chineseFile);

        // 获取canvas节点
        HashMap<String, Element> stringMap = getStringMap(chineDoc);


        // update
        updateSource(sourceDoc, stringMap);

        saveXML(sourceDoc, new File(outDir, fileName));

    }

    private static void saveXML(Document doc, File file) throws Exception {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
        System.out.println("download xml " + file.getAbsolutePath());
    }


    private static void updateSource(Document ele, HashMap<String, Element> targetStringMap) {
        // 根img
        Element rootChild = (Element) ele.getFirstChild();
        System.out.println("root " + rootChild.getAttribute("name"));

        updateImgdir(targetStringMap, rootChild, "", "canvas");

    }

    private static void updateImgdir(HashMap<String, Element> elementHashMap, Element element, String prefix, String translateNode) {
        // 检查是否为 <imgdir> 节点
        if (element.getNodeName().equals("imgdir")) {
            String imgdirName = element.getAttribute("name");
            String newPrefix = prefix.isEmpty() ? imgdirName : prefix + "/" + imgdirName;


            // 解析 <string> 节点
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element) node;

                    if (child.getNodeName().equals(translateNode) && imgdirName.equals("Shop")) {
                        String name = child.getAttribute("name");
                        String uniqueKey = newPrefix + "/" + name;
                        if (name.indexOf("backgrnd") != -1) {

                            Element chineseNode = elementHashMap.get(uniqueKey);
                            if (chineseNode != null) {
                                Node imported = element.getOwnerDocument().importNode(chineseNode, true);
                                element.replaceChild(imported, child);
                                System.out.println("Replaced node at: " + uniqueKey);

                            }
                        }
                    }

                    // 如果是嵌套的 <imgdir>，递归处理
                    if (child.getNodeName().equals("imgdir")) {
                        updateImgdir(elementHashMap, child, newPrefix, translateNode);
                    }
                }
            }
        }
    }



    private static HashMap<String, Element> getStringMap(Document ele) {

        // 根img
        Element rootChild = (Element) ele.getFirstChild();
        System.out.println("root " + rootChild.getAttribute("name"));
        HashMap<String, Element> elementMap = new HashMap<>();
        parseImgdir(elementMap, rootChild, "", "canvas");

        return elementMap;
    }


    private static void parseImgdir(HashMap<String, Element> elementHashMap, Element element, String prefix, String translateNode) {
        // 检查是否为 <imgdir> 节点
        if (element.getNodeName().equals("imgdir")) {
            String imgdirName = element.getAttribute("name");
            String newPrefix = prefix.isEmpty() ? imgdirName : prefix + "/" + imgdirName;

            // 解析 <string> 节点
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element) node;

                    // 如果是 <string> 标签，打印 name 和 value
                    if (child.getNodeName().equals(translateNode)) {
                        String name = child.getAttribute("name");
                        String uniqueKey = newPrefix + "/" + name;
                        if (name.indexOf("backgrnd") != -1) {
                            elementHashMap.put(uniqueKey, child);
                        }
                    }

                    // 如果是嵌套的 <imgdir>，递归处理
                    if (child.getNodeName().equals("imgdir")) {
                        parseImgdir(elementHashMap, child, newPrefix, translateNode);
                    }
                }
            }
        }
    }


    private static Document parseXML(File file) throws Exception {

        // 如果不是xml。直接写处出去
        if (file.getName().indexOf(".xml") == -1) {
            return null;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }
}
