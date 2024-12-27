import lombok.extern.slf4j.Slf4j;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.regex.Pattern;


/**
 *  汉化使用
 *
 *
 *  img记得转class才能带资源
 *
 */
@Slf4j
public class WZDataTranslate {

    private static final Pattern CHINESE_CHAR_PATTERN = Pattern.compile("[\\u4E00-\\u9FFF]");


    static String wzName = "UI";
    static String filePath = "E:\\oldmxd\\gms214\\中文\\";

    static String outPath = filePath    + File.separator + wzName + "\\out\\";
    static String sourcePath = filePath + File.separator + wzName +"\\英语\\";
    static String targetPath = filePath + File.separator +  wzName + "\\简体\\";


    // todo 测试发现只改bytedata会爆错
    // v2 只改变backgrnd 测试看看
    // UI 拿的是 canvas 的 name=""  bytedata=""
    private static String translateNode = "canvas";
    private static String translateNodeName = "name";
    private static String translateNodeNameDefault = "backgrnd";
    private static String translateNodeParam = "bytedata";
//  <canvas name="0" width="83" height="26" pixFormat="1" magLevel="0" bytedata="eJzMlz9W4zAQxn0DjpCSMkfgC">


    // 其他WZ 拿的String 的 name =""  value=""
    //	<imgdir name="24">
    //					<int name="id" value="2044501"/>
    //					<int name="count" value="1"/>
    //					<int name="prop" value="1"/>
    //					<string name="potentialGrade" value="C级"/>
    //				</imgdir>


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

        // 递归转
        createDirectoryStructure(new File(sourcePath), new File(outPath));

    }

    /**
     * 创建文件夹结构
     * @param sourceDir
     * @param outDir
     * @throws Exception
     */
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
                    log.info("创建文件夹: " + newTargetDir.getAbsolutePath());
                }
                // 递归处理子文件夹
                createDirectoryStructure(file, newTargetDir);
            } else if (file.isFile()) {
                String name = file.getName();
                String absolutePath = file.getAbsolutePath();
                String replace = absolutePath.replace("英语", "简体");
                File targetFile = new File(replace);
//                File targetFile = new File(targetPath + File.separator + name);

                Document document = updateWZ(file, targetFile);
                if (document != null) {

                    // 将修改后的内容写回文件
                    saveXML(document, new File(outDir, name));
                } else {
                    // 直接复制源文件
                    copyFile2(file, new File(outDir, name));
                }
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
        // 没有对应的中文就用源文件
        if (!targetFile.exists()) {
            log.debug(" cant find  " + targetFile.getAbsolutePath() + " !");
            log.info("=================================================================");
            return parseXML(sourceFile);
        }
        log.info("sourceFile:  " + sourceFile.getAbsolutePath());
        log.info("updateFile:  " + targetFile.getAbsolutePath());

        // 解析两个 XML 文件成对应WZ节点树。
        Document sourceDoc = parseXML(sourceFile);
        Document targetDoc = parseXML(targetFile);


        // v2 考虑遍历
        /**
         * 获取 中文的WZ节点对应的值StringMap
         */
        HashMap<String, String> targetStringMap = getStringMap(targetDoc);

        /**
         * 遍历source更新
         */
        updateSource(sourceDoc, targetStringMap);

        return sourceDoc;

    }




    // 判断是否是汉字
    private static boolean isChineseCharacter(char c) {
        return CHINESE_CHAR_PATTERN.matcher(String.valueOf(c)).matches();
    }

    // 解析 XML 文件为 Document
    private static Document parseXML(File file) throws Exception {

        // 如果不是xml。直接写处出去
        if (file.getName().indexOf(".xml") == -1) {
            return null;
        }




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








    private static void copyFile2(File sourceFile, File destinationFile) {
        // 创建目标目录（如果不存在）
        if (!destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs();
        }

        try {
            // 使用 Files.copy 方法复制文件
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("文件复制成功！");
        } catch (IOException e) {
            System.out.println("文件复制失败：" + e.getMessage());
        }
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
                    if (child.getNodeName().equals(translateNode)) {
                        String name = child.getAttribute(translateNodeName);
                        String value = child.getAttribute(translateNodeParam);
                        String uniqueKey = newPrefix + "/" + name;
//                        System.out.println("Unique Key: " + uniqueKey);
                        if (translateNodeNameDefault.equals(name)) {
                            stringStringHashMap.put(uniqueKey, value);
                        }
                    }

                    // 如果是嵌套的 <imgdir>，递归处理
                    if (child.getNodeName().equals("imgdir")) {
                        parseImgdir(stringStringHashMap, child, newPrefix);
                    }
                }
            }
        }
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
                    if (child.getNodeName().equals(translateNode)) {
                        String name = child.getAttribute(translateNodeName);
                        String value = child.getAttribute(translateNodeParam);
                        String uniqueKey = newPrefix + "/" + name;
//                        System.out.println("Unique Key: " + uniqueKey);

                        if (!translateNodeNameDefault.equals(name)) {
                            continue;
                        }

                        // 更新
                        String targetValue = targetMap.get(uniqueKey);
                        if (targetValue == null || "".equals(targetValue)) {
                            log.info("old name: ["  + uniqueKey + "] use oldValue: " + value );
                            continue;
                        }

                        // 更新
                        child.setAttribute(translateNodeParam, targetValue);
//                        log.info("oldValue: " + value + "    --- > newValue: " + targetValue);
                    }

                    // 如果是嵌套的 <imgdir>，递归处理
                    if (child.getNodeName().equals("imgdir")) {
                        updateImgdir(targetMap, child, newPrefix);
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
