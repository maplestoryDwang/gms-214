import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created on 3/2/2018.
 */
public class QuestDataTest {

    static final Logger log = LogManager.getLogger(QuestDataTest.class);


    private static final Pattern CHINESE_CHAR_PATTERN = Pattern.compile("[\\u4E00-\\u9FFF]");

    // 判断是否是汉字
    private static boolean isChineseCharacter(char c) {
        return CHINESE_CHAR_PATTERN.matcher(String.valueOf(c)).matches();
    }

    // 解析 XML 文件为 Document
    private static Document parseXML(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    // 将修改后的 Document 写回文件
    private static void saveXML(Document doc, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }


    // 更新 <imgdir> 节点内的 <string> 值
    private static void updateStringValues(Element sourceImgDir, Element targetImgDir) {
        NodeList targetStrings = targetImgDir.getElementsByTagName("string");

        HashMap<String, String> targetNameValue = new HashMap<>();

        // 存target的就好了
        for (int i = 0; i < targetStrings.getLength(); i++) {
            Element targetString = (Element) targetStrings.item(i);
            String targetName = targetString.getAttribute("name");
            String targetValue = targetString.getAttribute("value");
            targetNameValue.put(targetName, targetValue);
        }

        NodeList sourceStrings = sourceImgDir.getElementsByTagName("string");
        for (int i = 0; i < sourceStrings.getLength(); i++) {
            Element sourceString = (Element) sourceStrings.item(i);
            String sourceName = sourceString.getAttribute("name");
            String sourceValue = sourceString.getAttribute("value");

            // 判断繁体才替换
            Boolean checkResult = checkUpdate(sourceValue);
            if (!checkResult) {
                continue;
            }


            String targetValue = targetNameValue.get(sourceName);
            if (targetValue == null) {
                continue;
            }

            sourceString.setAttribute("value", targetValue);
            log.debug("oldValue: " + sourceValue + "    --- > newValue: " + targetValue);


        }
    }

    private static Boolean checkUpdate(String sourceValue) {
        for (char c : sourceValue.toCharArray()) {
            boolean chineseCharacter = isChineseCharacter(c);
            if (chineseCharacter) {
                return true;
            }

        }
        return false;
    }




    static String wzName = "Etc";
    static String outPath = "E:\\oldmxd\\gms214\\中文\\" + wzName + "\\out\\";
    static String sourcePath = "E:\\oldmxd\\gms214\\中文\\" + wzName +"\\英语\\";
    static String targetPath = "E:\\oldmxd\\gms214\\中文\\" +  wzName + "\\简体\\";

    public static void main(String[] args) throws Exception {
//        String fileName = "QuestInfo.img.xml";

//        Path folder = Paths.get("E:\\oldmxd\\gms214\\中文\\tms236\\wz\\Etc.wz\\");
//
//        Files.list(folder).filter(Files::isRegularFile).forEach(file ->{
//            log.info("fileName " + file.getFileName());
////            try {
////                updateWZ(file.getFileName().toString());
////            } catch (Exception e) {
////                throw new RuntimeException(e);
////            }
//
//        });
//        updateWZ(fileName);


        createDirectoryStructure(new File(sourcePath), new File(outPath));


//        testWZ();
    }

    private static void testWZ() throws Exception {
        String filePath = "E:\\oldmxd\\gms214\\中文\\Quest\\out\\PQuest.img.xml";
        Document document = parseXML(new File(filePath));
        getStringMap(document);
    }


    public static void createDirectoryStructure(File sourceDir, File outDir) throws Exception {
        if (!sourceDir.isDirectory()) {
            throw new IllegalArgumentException("源路径必须是文件夹！");
        }


        // 遍历源文件夹
        for (File file : sourceDir.listFiles()) {
            if (file.isDirectory()) {
                // 创建目标文件夹
                File newTargetDir = new File(outDir, file.getName());
                if (!newTargetDir.exists() && newTargetDir.mkdirs()) {
                    System.out.println("创建文件夹: " + newTargetDir.getAbsolutePath());
                }
                // 递归处理子文件夹
                createDirectoryStructure(file, newTargetDir);
            } else if (file.isFile()) {
                String name = file.getName();
//                String absolutePath = file.getAbsolutePath();
//                String replace = absolutePath.replace("英语", "简体");
                File targetFile = new File(targetPath + File.separator + name);

                Document document = updateWZ(file, targetFile);
                if (document != null) {

                    // 将修改后的内容写回文件
                    saveXML(document, new File(outDir, name));

                }


                // 在目标路径中创建文件空壳（非复制内容）
//                File newFile = new File(targetDir, name);
//                if (newFile.createNewFile()) {
//                    System.out.println("创建文件: " + newFile.getAbsolutePath());
//                }


            }
        }
    }


    /**
     * 需要考虑一个img下 name有重复的情况，小心被覆盖
     *
     * @param sourceFile
     * @param targetFile
     * @return
     * @throws Exception
     */
    private static Document updateWZ(File sourceFile, File targetFile) throws Exception {
        String fileName = sourceFile.getName();
        // 就用源文件
        if (!targetFile.exists()) {
            log.debug(" cant find  " + sourceFile.getAbsolutePath() + " !");
            log.info("=================================================================");
            return parseXML(sourceFile);
        }
        log.info("sourceFile:  " + sourceFile.getAbsolutePath());
        log.info("updateFile:  " + targetFile.getAbsolutePath());

        if ("expressionOnFace.img.xml".equals(fileName)) {
            return parseXML(targetFile);
        }


        HashMap<String, Element> sourceMap = new HashMap<>();
        HashMap<String, Element> targetMap = new HashMap<>();


        // 解析两个 XML 文件
        Document sourceDoc = parseXML(sourceFile);
        Document targetDoc = parseXML(targetFile);


//        // 处理 v1
//        NodeList sourceImgDirs  = sourceDoc.getElementsByTagName("imgdir");
//        NodeList targetImgDirs  = targetDoc.getElementsByTagName("imgdir");
//
//        String ignoreImg = fileName.replace(".xml", "");
//
//        getImgDirsMap(sourceImgDirs, sourceMap, ignoreImg);
//        getImgDirsMap(targetImgDirs, targetMap, ignoreImg);
//
//        sourceMap.forEach((name, sourceImgDir) ->{
//            Element targetImgDir = targetMap.get(name);
//            if (targetImgDir != null) {
//                updateStringValues(sourceImgDir, targetImgDir);
//
//            }
//        });
//


        // v2 考虑遍历
        /**
         * 获取StringMap
         */
        HashMap<String, String> targetStringMap = getStringMap(targetDoc);


        /**
         * 遍历source更新
         */
        updateSource(sourceDoc, targetStringMap);






        return sourceDoc;

    }

    private static void updateSource(Document ele, HashMap<String, String> targetStringMap) {
        // 根img
        Element rootChild = (Element)ele.getFirstChild();
        System.out.println("root " + rootChild.getAttribute("name"));

        updateImgdir(targetStringMap, rootChild,"");

    }


    /**
     * 递归哪String的值
     *
     * @param ele
     * @return
     */
    private static HashMap<String, String> getStringMap(Document ele) {

        // 根img
        Element rootChild = (Element)ele.getFirstChild();
        System.out.println("root " + rootChild.getAttribute("name"));
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        parseImgdir(stringStringHashMap, rootChild, "");

        return stringStringHashMap;
    }


    private static void updateImgdir(HashMap<String, String> targetMap, Element element, String prefix) {
        // 检查是否为 <imgdir> 节点
        if (element.getNodeName().equals("imgdir")) {
            String imgdirName = element.getAttribute("name");
            String newPrefix = prefix.isEmpty() ? imgdirName : prefix + "/" + imgdirName;

            // 解析 <string> 节点
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                // 就是差这一步 -0 -
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element) node;

                    // 如果是 <string> 标签，打印 name 和 value
                    if (child.getNodeName().equals("string")) {
                        String name = child.getAttribute("name");
                        String value = child.getAttribute("value");
                        String uniqueKey = newPrefix + "/" + name;
//                        System.out.println("Unique Key: " + uniqueKey);

                        // 更新
                        String targetValue = targetMap.get(uniqueKey);
                        if (targetValue == null || "".equals(targetValue)) {
                            log.info("old name: ["  + uniqueKey + "] use oldValue: " + value );
                            continue;
                        }

                        // 更新
                        child.setAttribute("value", targetValue);
                        log.info("oldValue: " + value + "    --- > newValue: " + targetValue);
                    }

                    // 如果是嵌套的 <imgdir>，递归处理
                    if (child.getNodeName().equals("imgdir")) {
                        updateImgdir(targetMap, child, newPrefix);
                    }
                }
            }
        }
    }


    private static void parseImgdir(HashMap<String, String> stringStringHashMap, Element element, String prefix) {
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
                    if (child.getNodeName().equals("string")) {
                        String name = child.getAttribute("name");
                        String value = child.getAttribute("value");
                        String uniqueKey = newPrefix + "/" + name;
//                        System.out.println("Unique Key: " + uniqueKey);

                        stringStringHashMap.put(uniqueKey, value);
                    }

                    // 如果是嵌套的 <imgdir>，递归处理
                    if (child.getNodeName().equals("imgdir")) {
                        parseImgdir(stringStringHashMap, child, newPrefix);
                    }
                }
            }
        }
    }

    /**
     * 获取Name值  String 一定在imgDir前面
     * 层序遍历
     *
     * @param stringStringHashMap
     * @param imgDirs
     * @param nowImgName
     */
    private static void getNodeString(HashMap<String, String> stringStringHashMap, Element imgDirs, String nowImgName) {

        NodeList childNodes = imgDirs.getChildNodes();




        NodeList sourceStrings = imgDirs.getElementsByTagName("string");
        NodeList sourceImgDirs = imgDirs.getElementsByTagName("imgdir");





    }


    private static void getImgDirsMap(NodeList imgDirs, HashMap<String, Element> map, String ignoreImg) {
        for (int i = 0; i < imgDirs.getLength(); i++) {
            Element sourceImgDir = (Element) imgDirs.item(i);
            String sourceImgDirName = sourceImgDir.getAttribute("name");
            if (ignoreImg.equals(sourceImgDirName)) {
                continue;
            }

            map.put(sourceImgDirName, sourceImgDir);
        }

    }
}
