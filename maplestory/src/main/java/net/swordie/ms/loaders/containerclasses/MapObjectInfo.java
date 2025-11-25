package net.swordie.ms.loaders.containerclasses;

import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName MapObjectInfo.java
 * @Description wz的文件信息包装类
 * @createTime 2025-11-25 20:20
 */
@Data
public class MapObjectInfo {

    private List<Integer> npc = new ArrayList<>();
    private List<Integer> mob = new ArrayList<>();

    public MapObjectInfo() {
    }

    public MapObjectInfo(List<Integer> npc, List<Integer> mob) {
        this.npc = npc;
        this.mob = mob;
    }

    public List<Integer> getNpc() {
        return npc;
    }

    public List<Integer> getMob() {
        return mob;
    }

}
