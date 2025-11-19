package net.swordie.ms.tracekill;

/**
 * 拓展任务的任务码
 */
public enum TraceKillQuestRxCode {
    WORKER(15325),
    RIDING(0),


    ;



    int val;

    TraceKillQuestRxCode(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
