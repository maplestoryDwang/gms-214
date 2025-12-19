package net.swordie.ms.enums;

import java.util.Arrays;

public enum EventType {
    NormalBalrog(0, "Normal Balrog"),
    MysticBalrog(1, "Mystic Balrog"),
    EasyZakum(2, "Easy Zakum"),
    NormalZakum(3, "Normal Zakum"),
    ChaosZakum(4, "Chaos Zakum"),
    Queen(5, "Normal Crimson Queen"),
    CQueen(6, "Chaos Crimson Queen"),
    Clown(7, "Normal Pierre"),
    CClown(8, "Chaos Pierre"),
    VonBon(9, "Normal VonBon"),
    CVonBon(10, "Chaos VonBon"),
    Vellum(11, "Normal Vellum"),
    CVellum(12, "Chaos Vellum"),
    EHorntail(13, "Easy horntail"),
    Horntail(14, "Normal horntail"),
    CHorntail(15, "Chaos horntail"),
    EVonLeon(16, "Easy VonLeon"),
    VonLeon(17, "Normal VonLeon"),
    EArkarium(18, "Easy VonLeon"),
    Arkarium(19, "Normal VonLeon"),
    EMagnus(20, "Easy Magnus"),
    NMagnus(20, "Normal Magnus"),
    HMagnus(20, "Chaos Magnus"),
    Pinkbean(21, "Normal Pinkbean"),
    CPinkbean(22, "Chaos Pinkbean"),
    Cygnus(23, "Cygnus"),
    CCygnus(24, "Chaos Cygnus"),
    TRADE_40(40, "Chaos Cygnus"),
    TRADE_41(41, "Chaos Cygnus"),
    TRADE_42(42, "Chaos Cygnus"),
    TRADE_43(43, "Chaos Cygnus"),
    TRADE_44(44, "Chaos Cygnus"),

    Gollux(999, "Gollux")
    ;

    private int val;
    private String name;

    EventType(int val) {
        this.val = val;
    }

    EventType(int val, String name) {
        this.val = val;
        this.name = name;
    }

    public static EventType getByVal(int val) {
        return Arrays.stream(values()).filter(gdt -> gdt.getVal() == val).findAny().orElse(null);
    }

    public String getName() {
        return name;
    }

    public int getVal() {
        return val;
    }

}