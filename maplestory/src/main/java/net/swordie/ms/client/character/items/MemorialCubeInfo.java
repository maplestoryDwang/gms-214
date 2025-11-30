package net.swordie.ms.client.character.items;

import net.swordie.ms.connection.OutPacket;

/**
 * Created on 12/20/2017.
 */
public class MemorialCubeInfo {
    private Equip equip;
    private Equip oldEquip;
    private int cubeItemID;

    // equip pos
    private short ePos;
    private short pos;



    public MemorialCubeInfo() {
    }

    /**
     * Creates a new MemorialCubeInfo with a given equip and cube id.
     * @param equip the equip of this MemorialCubeInfo
     * @param oldEquip the old equip, which should be a copy of the normal equip
     * @param cubeItemID the cube's item id
     */
    public MemorialCubeInfo(Equip equip, int cubeItemID) {
        this.equip = equip;
        this.cubeItemID = cubeItemID;
    }

    public MemorialCubeInfo(Equip equip, Equip oldEquip, int cubeItemID, short ePos, short pos) {
        this.equip = equip;
        this.oldEquip = oldEquip;
        this.cubeItemID = cubeItemID;
        this.ePos = ePos;
        this.pos = pos;
    }

    public void encode(OutPacket outPacket) {
        Equip equip = getEquip();
        outPacket.encodeByte(equip != null);
        if (equip != null) {
            equip.encode(outPacket);
            outPacket.encodeInt(getCubeItemID());
            outPacket.encodeInt(equip.getBagIndex());
        }
    }

    public Equip getEquip() {
        return equip;
    }

    public Equip getOldEquip() {
        return oldEquip;
    }

    public void setEquip(Equip equip) {
        this.equip = equip;
    }

    public int getCubeItemID() {
        return cubeItemID;
    }

    public void setCubeItemID(int cubeItemID) {
        this.cubeItemID = cubeItemID;
    }

    public void setOldEquip(Equip oldEquip) {
        this.oldEquip = oldEquip;
    }

    public short getePos() {
        return ePos;
    }

    public void setePos(short ePos) {
        this.ePos = ePos;
    }

    public short getPos() {
        return pos;
    }

    public void setPos(short pos) {
        this.pos = pos;
    }
}
