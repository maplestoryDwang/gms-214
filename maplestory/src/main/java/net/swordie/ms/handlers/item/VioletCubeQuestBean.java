package net.swordie.ms.handlers.item;

import java.util.List;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName VioletCubeQuestBean.java
 * @Description TODO
 * @createTime 2025-11-30 22:43
 */
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VioletCubeQuestBean {


    private int epos;           // 位置
    private int line;           // 行数
    private int optionGrade;    // 选项等级
    private int itemId;         // 物品ID
    private int snId;           // 数字
    private List<Integer> options; // 选项列表

    // 无参构造函数
    public VioletCubeQuestBean() {
    }

    // 全参构造函数
    public VioletCubeQuestBean(int epos, int line, int optionGrade, int itemId, int snId, List<Integer> options) {
        this.epos = epos;
        this.line = line;
        this.optionGrade = optionGrade;
        this.itemId = itemId;
        this.snId = snId;
        this.options = options;
    }

    // Getter和Setter方法
    public int getEpos() {
        return epos;
    }

    public void setEpos(int epos) {
        this.epos = epos;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getOptionGrade() {
        return optionGrade;
    }

    public void setOptionGrade(int optionGrade) {
        this.optionGrade = optionGrade;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getSnId() {
        return snId;
    }

    public void setSnId(int snId) {
        this.snId = snId;
    }

    public List<Integer> getOptions() {
        return options;
    }

    public void setOptions(List<Integer> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "QuestBean{" +
                "epos=" + epos +
                ", line=" + line +
                ", optionGrade=" + optionGrade +
                ", itemId=" + itemId +
                ", snId=" + snId +
                ", options=" + options +
                '}';
    }
}
