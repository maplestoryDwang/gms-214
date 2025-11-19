package net.swordie.ms.tracekill;

/**
 * riding
 *
 * @author dwang
 * @version 1.0
 * @since 2025/11/19 17:20
 */
public class TraceKillRiding {

    private int index;
    private int ridSkill;


    public TraceKillRiding(int index, int ridSkill) {
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
