import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;

/**
 * TODO
 *
 * @author dwang
 * @version 1.0
 * @since 2025/11/4 17:44
 */
public class PrintUINode {

    private static String translateNode = "canvas";
    private static String translateNodeName = "name";
    private static String translateNodeNameDefault = "backgrnd";
    private static String translateNodeParam = "bytedata";


    public static void main(String[] args) throws Exception {

        File sourceFile = new File("E:\\javaguide\\214\\en\\UI.wz\\UIWindowBT.img.xml");

        Document document = WZDataTranslate.parseXML(sourceFile);


        HashMap<String, Element> canvasMap = getCanvasMap(document);


        canvasMap.forEach((k,v)->{
            System.out.println(k + " : " + v.toString());
        });
    }


    private static HashMap<String, Element> getCanvasMap(Document ele) {

        // 根img
        Element rootChild = (Element)ele.getFirstChild();
        System.out.println("root " + rootChild.getAttribute("name"));
        HashMap<String, Element> canvasMap = new HashMap<>();
        parseImgdir(canvasMap, rootChild, "", translateNode);

        return canvasMap;
    }

    private static void parseImgdir(HashMap<String, Element> map, Element element, String prefix, String translateNode) {
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

                    // 如果是 需要的标签就放入
                    if (child.getNodeName().equals(translateNode)) {
                        String name = child.getAttribute(translateNodeName);
                        if (name.indexOf("backgrnd") != -1) {
                            String uniqueKey = newPrefix + "/" + name;
                            map.put(uniqueKey, child);
                        }



//                        System.out.println("Unique Key: " + uniqueKey);
//                        if (translateNodeNameDefault.equals(name)) {
//                            stringStringHashMap.put(uniqueKey, value);
//                        }
                    } else {
                        parseImgdir(map, child, newPrefix, translateNode);

                    }
                }
            }
        }
    }
}
