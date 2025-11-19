package net.swordie.ms.tracekill;

/**
 * worker信息
 *
 * @author dwang
 * @version 1.0
 * @since 2025/11/19 17:20
 */
public class TraceKillWorker {

    private int index;
    private int npcId;
    private int speedAdd;
    private int jumpAdd;
    private int weightAdd;


    public TraceKillWorker(int index, int npcId, int speedAdd, int jumpAdd, int weightAdd) {
        this.index = index;
        this.npcId = npcId;
        this.speedAdd = speedAdd;
        this.jumpAdd = jumpAdd;
        this.weightAdd = weightAdd;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public int getSpeedAdd() {
        return speedAdd;
    }

    public void setSpeedAdd(int speedAdd) {
        this.speedAdd = speedAdd;
    }

    public int getJumpAdd() {
        return jumpAdd;
    }

    public void setJumpAdd(int jumpAdd) {
        this.jumpAdd = jumpAdd;
    }

    public int getWeightAdd() {
        return weightAdd;
    }

    public void setWeightAdd(int weightAdd) {
        this.weightAdd = weightAdd;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
