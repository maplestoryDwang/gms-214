import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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


    static String outPath = "E:\\oldmxd\\gms214\\中文\\etc\\out\\";


    static String sourcePath = "E:\\oldmxd\\gms214\\中文\\etc\\繁体\\";
//    static String targetPath = "E:\\oldmxd\\gms214\\中文\\string\\简体\\";

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

    }





    public static void createDirectoryStructure(File sourceDir, File outDir) throws Exception {
        if (!sourceDir.isDirectory()) {
            throw new IllegalArgumentException("源路径必须是文件夹！");
        }


        // 遍历源文件夹
        for (File file : sourceDir.listFiles()) {
            if (file.isDirectory()) {
//                // 创建目标文件夹
//                File newTargetDir = new File(outDir, file.getName());
//                if (!newTargetDir.exists() && newTargetDir.mkdirs()) {
//                    System.out.println("创建文件夹: " + newTargetDir.getAbsolutePath());
//                }
//                // 递归处理子文件夹
//                createDirectoryStructure(file, newTargetDir);
            } else if (file.isFile()) {
                String name = file.getName();
                String absolutePath = file.getAbsolutePath();
                String replace = absolutePath.replace("繁体", "简体");
                File targetFile = new File(replace);

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

    private static Document updateWZ( File sourceFile, File targetFile) throws Exception {
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

        NodeList sourceImgDirs  = sourceDoc.getElementsByTagName("imgdir");
        NodeList targetImgDirs  = targetDoc.getElementsByTagName("imgdir");

        String ignoreImg = fileName.replace(".xml", "");

        getImgDirsMap(sourceImgDirs, sourceMap, ignoreImg);
        getImgDirsMap(targetImgDirs, targetMap, ignoreImg);

        sourceMap.forEach((name, sourceImgDir) ->{
            Element targetImgDir = targetMap.get(name);
            if (targetImgDir != null) {
                updateStringValues(sourceImgDir, targetImgDir);

            }
        });

        return sourceDoc;

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
