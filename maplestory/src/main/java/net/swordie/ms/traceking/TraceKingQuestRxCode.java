package net.swordie.ms.traceking;

/**
 * 拓展任务的任务码
 */
public enum TraceKingQuestRxCode {
    WORKER(15325),
    RIDING(0),
    ITEM_15322(15322),
    ITEM_15323(15323),
    ITEM_15347(15347),
    ITEM_15346(15346),
    ITEM_15345(15345),
    ITEM_15344(15344),
    GOLD(15324),


    ;



    int val;

    TraceKingQuestRxCode(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
