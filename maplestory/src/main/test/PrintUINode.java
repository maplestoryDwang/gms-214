import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 1. 修改byteData、_hash
 * 2. 跳过源文件和目标文件有 String 标签name为 _outlink 、_inlink 的 canvas 标签
 * <p>
 * 步骤
 * 1. 拿出所有参照文件的canvas标签，必须符合没有_outlink 、_inlink
 * 2. 需要替换的canvas标签，检查该节点在参照文件map中有
 * 3. 判断需要替换的canvas标签没有 _outlink 、_inlink
 * 4. 替换canvas标签
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

    static List<String> blockImg = List.of("WorldMap");
    static List<String> agreeCanvasName = List.of("backgrnd");

    public static void main(String[] args) throws Exception {


        String sourcePath = "E:\\oldmxd\\gms214\\中文\\UI\\20251104\\英语";
        String refPath = "E:\\oldmxd\\gms214\\中文\\UI\\20251104\\简体";
        String outPath = "E:\\oldmxd\\gms214\\中文\\UI\\20251104\\out";


//        File sourceRoot = new File(sourcePath);
//        File chineseRoot = new File(refPath);
//        File outputRoot = new File(outPath);
//
//        processDirectory(sourceRoot, chineseRoot, outputRoot);


        String fileName = "UIWindow2.img.xml";
        File sourceFile = new File(sourcePath, fileName);
        File refFile = new File(refPath, fileName);
        File out = new File(outPath, fileName);
        runCopy(sourceFile, refFile, out);


//
//        Document refDoc = WZDataTranslate.parseXML(refFile);
//        Document sourceDoc = WZDataTranslate.parseXML(sourceFile);
//
//        HashMap<String, Element> canvasMap = getCanvasMap(refDoc);
//
//        // 替换
//        Document updateDoc = replaceDoc(sourceDoc, canvasMap);
//
//        // save
//        saveXML(updateDoc, out);


    }

    private static void runCopy(File sourceFile, File refFile, File out) throws Exception {
        Document refDoc = WZDataTranslate.parseXML(refFile);
        Document sourceDoc = WZDataTranslate.parseXML(sourceFile);
        HashMap<String, Element> canvasMap = getCanvasMap(refDoc);
        // 替换
        Document updateDoc = replaceDoc(sourceDoc, canvasMap);
        // save
        saveXML(updateDoc, out);
    }


    private static void processDirectory(File sourceDir, File chineseDir, File outDir) throws Exception {
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        File[] files = sourceDir.listFiles();
        if (files == null) return;

        for (File sourceFile : files) {
            File chineseFile = new File(chineseDir, sourceFile.getName());
            File outFile = new File(outDir, sourceFile.getName());

            if (sourceFile.isDirectory()) {
                processDirectory(sourceFile, chineseFile, outFile);
            } else if (sourceFile.getName().endsWith(".xml")) {

                if (!chineseFile.exists()) {
                    System.out.println("⚠️ 中文文件不存在，复制源文件到输出目录: " + sourceFile.getName());
                    copyFile(sourceFile, outFile);
                    continue;
                }

                System.out.println("正在处理文件: " + sourceFile.getAbsolutePath());

                try {
                    runCopy(sourceFile, chineseFile, outFile);
                    System.out.println("✅ 输出完成: " + outFile.getAbsolutePath());
                } catch (Exception e) {
                    System.err.println("❌ 处理失败: " + sourceFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyFile(File source, File target) throws IOException {
        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }
        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }


    // 将修改后的 Document 写回文件
    private static void saveXML(Document doc, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }


    private static Document replaceDoc(Document ele, HashMap<String, Element> canvasMap) {

        // 根img
        Element rootChild = (Element) ele.getFirstChild();
        System.out.println("root " + rootChild.getAttribute("name"));
        updateImgdir(canvasMap, rootChild, "", translateNode);

        return ele;
    }


    private static HashMap<String, Element> getCanvasMap(Document ele) {

        // 根img
        Element rootChild = (Element) ele.getFirstChild();
        System.out.println("root " + rootChild.getAttribute("name"));
        HashMap<String, Element> canvasMap = new HashMap<>();
        parseImgdir(canvasMap, rootChild, "", translateNode);

        return canvasMap;
    }


    private static void parseImgdir(HashMap<String, Element> refCanvasMap, Element element, String prefix, String translateNode) {
        if (element.getNodeName().equals("imgdir")) {
            String imgdirName = element.getAttribute("name");
            String newPrefix = prefix.isEmpty() ? imgdirName : prefix + "/" + imgdirName;

            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element child = (Element) node;

                // 只处理 <canvas> 节点
                if (child.getNodeName().equals(translateNode)) {
                    Element canvas = child;

                    String name = canvas.getAttribute("name");
                    String uniqueKey = newPrefix + "/" + name;

                    if (hasInOrOutLink(canvas)) {
                        System.out.println("⏭ 跳过（ref存在_inlink/_outlink）: " + uniqueKey);
                        continue;
                    }
                    // 没有就加入
                    refCanvasMap.put(uniqueKey, child);

                }

                // 递归子目录
                if (child.getNodeName().equals("imgdir")) {
                    parseImgdir(refCanvasMap, child, newPrefix, translateNode);
                }
            }
        }
    }


    private static void updateImgdir(HashMap<String, Element> refCanvasMap, Element element, String prefix, String translateNode) {
        if (element.getNodeName().equals("imgdir")) {
            String imgdirName = element.getAttribute("name");
            if(blockImg.contains(imgdirName)) {
                return;
            }
            String newPrefix = prefix.isEmpty() ? imgdirName : prefix + "/" + imgdirName;

            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element child = (Element) node;

                // 只处理 <canvas> 节点
                if (child.getNodeName().equals(translateNode)) {
                    Element canvas = child;

                    String name = canvas.getAttribute("name");
                    if (name.indexOf("backgrnd") == -1) {
                        return;
                    }



                    String uniqueKey = newPrefix + "/" + name;

                    Element refCanvas = refCanvasMap.get(uniqueKey);
                    if (refCanvas == null) continue; // 没有对应参照节点，跳过

                    // 1️⃣ 检查当前 <canvas> 所在的 <imgdir> 是否含有 _outlink/_inlink
                    if (hasInOrOutLink(canvas)) {
                        System.out.println("⏭ 跳过（target存在_inlink/_outlink）: " + uniqueKey);
                        continue;
                    }

                    // 3️⃣ 替换 bytedata 和 _hash
                    replaceByteDataAndHash(refCanvas, canvas, uniqueKey);
                }

                // 递归子目录
                if (child.getNodeName().equals("imgdir")) {
                    updateImgdir(refCanvasMap, child, newPrefix, translateNode);
                }
            }
        }
    }


    /**
     * 从 refCanvas 拷贝 bytedata 与 _hash 到 targetCanvas（只拷贝值，不移动节点）
     */
    private static void replaceByteDataAndHash(Element refCanvas, Element targetCanvas, String uniqueKey) {
        // 1) 拷贝 bytedata 属性（如果有）
        String refByteData = refCanvas.getAttribute("bytedata");
        if (refByteData != null && !refByteData.isEmpty()) {
            targetCanvas.setAttribute("bytedata", refByteData);
        }

        // 2) 拷贝 _hash：参照的 _hash 可能在属性上，也可能是 <string name="_hash" value="..."/>
        String refHash = null;
//        // 优先看属性
//        if (refCanvas.hasAttribute("_hash")) {
//            refHash = refCanvas.getAttribute("_hash");
//        } else {
        // 查找子节点 string name="_hash"
        NodeList refChildren = refCanvas.getChildNodes();
        for (int i = 0; i < refChildren.getLength(); i++) {
            Node n = refChildren.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element e = (Element) n;
            if ("string".equals(e.getNodeName()) && "_hash".equals(e.getAttribute("name"))) {
                refHash = e.getAttribute("value");
                break;
            }
        }
//        }

        if (refHash != null && !refHash.isEmpty()) {
            // 目标 canvas 可能也有属性形式或子节点形式的 _hash，我们做两步尝试：
//            if (targetCanvas.hasAttribute("_hash")) {
//                targetCanvas.setAttribute("_hash", refHash);
//            } else {
            // 尝试查找子节点 <string name="_hash" value="..."/>
            Element hashNode = findChildStringByName(targetCanvas, "_hash");
            if (hashNode != null) {
                hashNode.setAttribute("value", refHash);
            } else {
                System.err.println("没有找到_hash节点");

                // 都没有则创建一个新的 string 节点并插入（放到 canvas 的末尾）
//                    Document doc = targetCanvas.getOwnerDocument();
//                    Element newHash = doc.createElement("string");
//                    newHash.setAttribute("name", "_hash");
//                    newHash.setAttribute("value", refHash);
//                    targetCanvas.appendChild(newHash);
            }
//            }
        }

        System.out.println("✅ 替换完成: " + uniqueKey);
    }

    /**
     * 在 canvas 的直接子节点中查找 <string name="..."> 并返回该 Element 或 null
     */
    private static Element findChildStringByName(Element canvas, String name) {
        NodeList nl = canvas.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element e = (Element) n;
            if ("string".equals(e.getNodeName()) && name.equals(e.getAttribute("name"))) {
                return e;
            }
        }
        return null;
    }

    /**
     * 检查节点内是否存在 <string name="_outlink"> 或 <string name="_inlink">
     */
    private static boolean hasInOrOutLink(Node node) {
        if (node == null) return false;
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node n = childNodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if ("string".equals(e.getNodeName())) {
                    String name = e.getAttribute("name");
                    if ("_outlink".equals(name) || "_inlink".equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


//    private static void parseImgdir(HashMap<String, Element> map, Element element, String prefix, String translateNode) {
//        // 检查是否为 <imgdir> 节点
//        if (element.getNodeName().equals("imgdir")) {
//            String imgdirName = element.getAttribute("name");
//            String newPrefix = prefix.isEmpty() ? imgdirName : prefix + "/" + imgdirName;
//
//            // 解析 <string> 节点
//            NodeList children = element.getChildNodes();
//            for (int i = 0; i < children.getLength(); i++) {
//                Node node = children.item(i);
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element child = (Element) node;
//
//                    // 如果是 需要的标签就放入
//                    if (child.getNodeName().equals(translateNode)) {
//                        String name = child.getAttribute(translateNodeName);
//                        if (name.indexOf("backgrnd") != -1) {
//                            String uniqueKey = newPrefix + "/" + name;
//                            map.put(uniqueKey, child);
//                        }
//
//
//
////                        System.out.println("Unique Key: " + uniqueKey);
////                        if (translateNodeNameDefault.equals(name)) {
////                            stringStringHashMap.put(uniqueKey, value);
////                        }
//                    } else {
//                        parseImgdir(map, child, newPrefix, translateNode);
//
//                    }
//                }
//            }
//        }
//    }
}
