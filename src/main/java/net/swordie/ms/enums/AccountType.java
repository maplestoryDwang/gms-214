package net.swordie.ms.enums;

/**
 * 玩家类型 user.accountType
 * 4 是管理员
 */
public enum AccountType {
    Player(0),
    Tester(1 << 5),
    Intern(1 << 3),
    GameMaster(1 << 4),
    Admin(1 << 4);

    private int val;

    AccountType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
