package net.swordie.ms.traceking;

/**
 * riding
 *
 * @author dwang
 * @version 1.0
 * @since 2025/11/19 17:20
 */
public class TraceKingRiding {

    private int index;
    private int ridSkill;


    public TraceKingRiding(int index, int ridSkill) {
        this.index = index;
        this.ridSkill = ridSkill;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRidSkill() {
        return ridSkill;
    }

    public void setRidSkill(int ridSkill) {
        this.ridSkill = ridSkill;
    }
}
