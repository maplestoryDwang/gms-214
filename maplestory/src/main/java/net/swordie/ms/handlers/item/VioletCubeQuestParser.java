package net.swordie.ms.handlers.item;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName QuestParser.java
 * @Description TODO
 * @createTime 2025-11-30 22:43
 */

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VioletCubeQuestParser {

    public static VioletCubeQuestBean  parseQuestData(String data) {
        VioletCubeQuestBean  bean = new VioletCubeQuestBean ();
        String[] pairs = data.split(";");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                switch (key) {
                    case "p":
                        bean.setEpos(Integer.parseInt(value));
                        break;
                    case "c":
                        bean.setLine(Integer.parseInt(value));
                        break;
                    case "og":
                        bean.setOptionGrade(Integer.parseInt(value));
                        break;
                    case "i":
                        bean.setItemId(Integer.parseInt(value));
                        break;
                    case "n":
                        bean.setSnId(Integer.parseInt(value));
                        break;
                    case "o":
                        // 解析选项列表，将逗号分隔的字符串转换为Integer列表
                        List<Integer> options = Arrays.stream(value.split(","))
                                .map(String::trim)
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                        bean.setOptions(options);
                        break;
                }
            }
        }
        return bean;
    }
}