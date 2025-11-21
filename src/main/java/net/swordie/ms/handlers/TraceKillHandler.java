package net.swordie.ms.handlers;

import lombok.extern.slf4j.Slf4j;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.quest.Quest;
import net.swordie.ms.client.character.quest.QuestManager;
import net.swordie.ms.client.character.skills.info.SkillInfo;
import net.swordie.ms.client.character.skills.info.SkillUseInfo;
import net.swordie.ms.client.jobs.Job;
import net.swordie.ms.client.party.PartyMember;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.connection.packet.Effect;
import net.swordie.ms.connection.packet.UserPacket;
import net.swordie.ms.connection.packet.UserRemote;
import net.swordie.ms.connection.packet.WvsContext;
import net.swordie.ms.constants.SkillConstants;
import net.swordie.ms.enums.MessageType;
import net.swordie.ms.enums.QuestStatus;
import net.swordie.ms.handlers.header.InHeader;
import net.swordie.ms.handlers.header.OutHeader;
import net.swordie.ms.loaders.SkillData;
import net.swordie.ms.tracekill.TraceKillItemInfo;
import net.swordie.ms.tracekill.TraceKillUserInfo;
import net.swordie.ms.util.Rect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author dwang
 * @version 1.0.0
 * @Title todo 需要确认名字
 * @ClassName TraceKillHandler.java
 * @Description 跑商的两个接口
 * @createTime 2025-11-18 15:03
 */

@Slf4j
public class TraceKillHandler {


    // 对应 qrkey value
    private static Map<Integer, String> shopMessageQr = new HashMap<>();

    // 每个商店卖的东西 npcID，
    private static HashMap<Integer, List<TraceKillItemInfo>> merchantItemsBuy = new HashMap<>();
    private static HashMap<Integer, List<TraceKillItemInfo>> merchantItemsSell = new HashMap<>();

    private static HashMap<Integer, TraceKillUserInfo> userInfoMap = new HashMap<>();
    private static Integer skillID = 80001950;


    static {
        // 构建qr
        // !qr 15323 1=0;0=0;3=0;2=0;5=0;4=0;7=0;6=0
        // !qr 15322 5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0
        // !qr 15347 4=0;1=0;0=0;3=0;2=0
        // !qr 15346 5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0
        // !qr 15345 5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0
        // !qr 15344 5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0
        shopMessageQr.put(15323, "1=0;0=0;3=0;2=0;5=0;4=0;7=0;6=0");
        shopMessageQr.put(15322, "5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0");
        shopMessageQr.put(15347, "4=0;1=0;0=0;3=0;2=0");
        shopMessageQr.put(15346, "5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0");
        shopMessageQr.put(15345, "5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0");
        shopMessageQr.put(15344, "5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0");


// 创建所有物品的TraceKillItemInfo对象（包含QR和QREx信息）
//        TraceKillItemInfo item4034804 = new TraceKillItemInfo(4034804, "桔梗", 0, 0, 15322, 0);
//        TraceKillItemInfo item4034805 = new TraceKillItemInfo(4034805, "布片", 0, 0, 15322, 1);
//        TraceKillItemInfo item4034806 = new TraceKillItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2);
//        TraceKillItemInfo item4034807 = new TraceKillItemInfo(4034807, "矿泉水", 0, 0, 15322, 3);
//        TraceKillItemInfo item4034808 = new TraceKillItemInfo(4034808, "棉花团", 0, 0, 15322, 4);
//        TraceKillItemInfo item4034809 = new TraceKillItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5);
//        TraceKillItemInfo item4034810 = new TraceKillItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6);
//        TraceKillItemInfo item4034811 = new TraceKillItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7);
//        TraceKillItemInfo item4034812 = new TraceKillItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0);
//        TraceKillItemInfo item4034813 = new TraceKillItemInfo(4034813, "猪肉", 0, 0, 15323, 1);
//        TraceKillItemInfo item4034814 = new TraceKillItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2);
//        TraceKillItemInfo item4034815 = new TraceKillItemInfo(4034815, "银原石", 0, 0, 15323, 3);
//        TraceKillItemInfo item4034816 = new TraceKillItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4);
//        TraceKillItemInfo item4034817 = new TraceKillItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5);
//        TraceKillItemInfo item4034818 = new TraceKillItemInfo(4034818, "金原石", 0, 0, 15323, 6);
//        TraceKillItemInfo item4034819 = new TraceKillItemInfo(4034819, "天然蜂蜜", 0, 0, 15323, 7);
//        TraceKillItemInfo item4034820 = new TraceKillItemInfo(4034820, "棉花", 0, 0, 15344, 0);
//        TraceKillItemInfo item4034821 = new TraceKillItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1);
//        TraceKillItemInfo item4034822 = new TraceKillItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2);
//        TraceKillItemInfo item4034823 = new TraceKillItemInfo(4034823, "水獭皮", 0, 0, 15344, 3);
//        TraceKillItemInfo item4034824 = new TraceKillItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4);
//        TraceKillItemInfo item4034825 = new TraceKillItemInfo(4034825, "仙桃", 0, 0, 15344, 5);
//        TraceKillItemInfo item4034826 = new TraceKillItemInfo(4034826, "合成石油", 0, 0, 15344, 6);
//        TraceKillItemInfo item4034827 = new TraceKillItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7);
//        TraceKillItemInfo item4034828 = new TraceKillItemInfo(4034828, "白糖", 0, 0, 15345, 0);
//        TraceKillItemInfo item4034829 = new TraceKillItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1);
//        TraceKillItemInfo item4034830 = new TraceKillItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2);
//        TraceKillItemInfo item4034831 = new TraceKillItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3);
//        TraceKillItemInfo item4034832 = new TraceKillItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4);
//        TraceKillItemInfo item4034833 = new TraceKillItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5);
//        TraceKillItemInfo item4034834 = new TraceKillItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6);
//        TraceKillItemInfo item4034835 = new TraceKillItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7);
//        TraceKillItemInfo item4034836 = new TraceKillItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0);
//        TraceKillItemInfo item4034837 = new TraceKillItemInfo(4034837, "怪物肉", 0, 0, 15346, 1);
//        TraceKillItemInfo item4034838 = new TraceKillItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2);
//        TraceKillItemInfo item4034839 = new TraceKillItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3);
//        TraceKillItemInfo item4034840 = new TraceKillItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4);
//        TraceKillItemInfo item4034841 = new TraceKillItemInfo(4034841, "虎皮", 0, 0, 15346, 5);
//        TraceKillItemInfo item4034842 = new TraceKillItemInfo(4034842, "金块", 0, 0, 15346, 6);
//        TraceKillItemInfo item4034843 = new TraceKillItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7);
//        TraceKillItemInfo item4034844 = new TraceKillItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0);
//        TraceKillItemInfo item4034845 = new TraceKillItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1);
//        TraceKillItemInfo item4034846 = new TraceKillItemInfo(4034846, "山参", 0, 0, 15347, 2);
//        TraceKillItemInfo item4034847 = new TraceKillItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3);
//        TraceKillItemInfo item4034848 = new TraceKillItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4);

// 石铁 (9001072) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001072 = new ArrayList<>();
        list9001072.add(new TraceKillItemInfo(4034804, "桔梗", 0, 0, 15322, 0)); // 桔梗 - 可购买
        list9001072.add(new TraceKillItemInfo(4034819, "天然蜂蜜", 0, 0, 15323, 7)); // 天然蜂蜜 - 可购买
        list9001072.add(new TraceKillItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6)); // 传说中的补药 - 可购买
        merchantItemsBuy.put(9001072, list9001072);

        list9001072 = new ArrayList<>();
        list9001072.add(new TraceKillItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2)); // 柔顺的丝绸 - 可出售
        list9001072.add(new TraceKillItemInfo(4034815, "银原石", 0, 0, 15323, 3)); // 银原石 - 可出售
        list9001072.add(new TraceKillItemInfo(4034828, "白糖", 0, 0, 15345, 0)); // 白糖 - 可出售
        list9001072.add(new TraceKillItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1)); // 毛毡布料 - 可出售
        list9001072.add(new TraceKillItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0)); // 贵族的绸缎 - 可出售
        list9001072.add(new TraceKillItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1)); // 提炼的锂 - 可出售
        merchantItemsSell.put(9001072, list9001072);

// 奇里文 (9001073) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001073 = new ArrayList<>();
        list9001073.add(new TraceKillItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2)); // 矿石碎片 - 可购买
        list9001073.add(new TraceKillItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1)); // 蓝宝石碎片 - 可购买
        list9001073.add(new TraceKillItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0)); // 顶级紫水晶 - 可购买
        merchantItemsBuy.put(9001073, list9001073);

        list9001073 = new ArrayList<>();
        list9001073.add(new TraceKillItemInfo(4034813, "猪肉", 0, 0, 15323, 1)); // 猪肉 - 可出售
        list9001073.add(new TraceKillItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2)); // 柔顺的丝绸 - 可出售
        list9001073.add(new TraceKillItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2)); // 黑水晶碎片 - 可出售
        list9001073.add(new TraceKillItemInfo(4034828, "白糖", 0, 0, 15345, 0)); // 白糖 - 可出售
        list9001073.add(new TraceKillItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7)); // 黄金桃子 - 可出售
        list9001073.add(new TraceKillItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0)); // 贵族的绸缎 - 可出售
        merchantItemsSell.put(9001073, list9001073);

// 兰明 (9001074) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001074 = new ArrayList<>();
        list9001074.add(new TraceKillItemInfo(4034805, "布片", 0, 0, 15322, 1)); // 布片 - 可购买
        list9001074.add(new TraceKillItemInfo(4034820, "棉花", 0, 0, 15344, 0)); // 棉花 - 可购买
        list9001074.add(new TraceKillItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7)); // 精致丝绸 - 可购买
        merchantItemsBuy.put(9001074, list9001074);

        list9001074 = new ArrayList<>();
        list9001074.add(new TraceKillItemInfo(4034813, "猪肉", 0, 0, 15323, 1)); // 猪肉 - 可出售
        list9001074.add(new TraceKillItemInfo(4034815, "银原石", 0, 0, 15323, 3)); // 银原石 - 可出售
        list9001074.add(new TraceKillItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2)); // 黑水晶碎片 - 可出售
        list9001074.add(new TraceKillItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1)); // 毛毡布料 - 可出售
        list9001074.add(new TraceKillItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7)); // 黄金桃子 - 可出售
        list9001074.add(new TraceKillItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1)); // 提炼的锂 - 可出售
        merchantItemsSell.put(9001074, list9001074);

// 史莱 (9001075) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001075 = new ArrayList<>();
        list9001075.add(new TraceKillItemInfo(4034807, "矿泉水", 0, 0, 15322, 3)); // 矿泉水 - 可购买
        list9001075.add(new TraceKillItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2)); // 沙漠石榴 - 可购买
        list9001075.add(new TraceKillItemInfo(4034837, "怪物肉", 0, 0, 15346, 1)); // 怪物肉 - 可购买
        merchantItemsBuy.put(9001075, list9001075);

        list9001075 = new ArrayList<>();
        list9001075.add(new TraceKillItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5)); // 沙漠的围巾 - 可出售
        list9001075.add(new TraceKillItemInfo(4034818, "金原石", 0, 0, 15323, 6)); // 金原石 - 可出售
        list9001075.add(new TraceKillItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3)); // 沙漠番茄 - 可出售
        list9001075.add(new TraceKillItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4)); // 华丽的布料碎片 - 可出售
        list9001075.add(new TraceKillItemInfo(4034846, "山参", 0, 0, 15347, 2)); // 山参 - 可出售
        list9001075.add(new TraceKillItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4)); // 光辉钻石 - 可出售
        merchantItemsSell.put(9001075, list9001075);

// 工作人员O (9001076) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001076 = new ArrayList<>();
        list9001076.add(new TraceKillItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5)); // 钢铁碎片 - 可购买
        list9001076.add(new TraceKillItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4)); // 黄晶碎片 - 可购买
        list9001076.add(new TraceKillItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3)); // 精炼黑水晶 - 可购买
        merchantItemsBuy.put(9001076, list9001076);

        list9001076 = new ArrayList<>();
        list9001076.add(new TraceKillItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4)); // 动物性油脂 - 可出售
        list9001076.add(new TraceKillItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5)); // 沙漠的围巾 - 可出售
        list9001076.add(new TraceKillItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3)); // 沙漠番茄 - 可出售
        list9001076.add(new TraceKillItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5)); // 钻石碎片 - 可出售
        list9001076.add(new TraceKillItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3)); // 皇帝的绸缎 - 可出售
        list9001076.add(new TraceKillItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4)); // 光辉钻石 - 可出售
        merchantItemsSell.put(9001076, list9001076);

// 玛帕 (9001077) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001077 = new ArrayList<>();
        list9001077.add(new TraceKillItemInfo(4034808, "棉花团", 0, 0, 15322, 4)); // 棉花团 - 可购买
        list9001077.add(new TraceKillItemInfo(4034823, "水獭皮", 0, 0, 15344, 3)); // 水獭皮 - 可购买
        list9001077.add(new TraceKillItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2)); // 丝绸布料 - 可购买
        merchantItemsBuy.put(9001077, list9001077);

        list9001077 = new ArrayList<>();
        list9001077.add(new TraceKillItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4)); // 动物性油脂 - 可出售
        list9001077.add(new TraceKillItemInfo(4034818, "金原石", 0, 0, 15323, 6)); // 金原石 - 可出售
        list9001077.add(new TraceKillItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4)); // 华丽的布料碎片 - 可出售
        list9001077.add(new TraceKillItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5)); // 钻石碎片 - 可出售
        list9001077.add(new TraceKillItemInfo(4034846, "山参", 0, 0, 15347, 2)); // 山参 - 可出售
        list9001077.add(new TraceKillItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3)); // 皇帝的绸缎 - 可出售
        merchantItemsSell.put(9001077, list9001077);

// 豆尔 (9001078) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001078 = new ArrayList<>();
        list9001078.add(new TraceKillItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6)); // 仙人掌树液 - 可购买
        list9001078.add(new TraceKillItemInfo(4034825, "仙桃", 0, 0, 15344, 5)); // 仙桃 - 可购买
        list9001078.add(new TraceKillItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4)); // 顶级维他命 - 可购买
        merchantItemsBuy.put(9001078, list9001078);

        list9001078 = new ArrayList<>();
        list9001078.add(new TraceKillItemInfo(4034805, "布片", 0, 0, 15322, 1)); // 布片 - 可出售
        list9001078.add(new TraceKillItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2)); // 矿石碎片 - 可出售
        list9001078.add(new TraceKillItemInfo(4034820, "棉花", 0, 0, 15344, 0)); // 棉花 - 可出售
        list9001078.add(new TraceKillItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1)); // 蓝宝石碎片 - 可出售
        list9001078.add(new TraceKillItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7)); // 精致丝绸 - 可出售
        list9001078.add(new TraceKillItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0)); // 顶级紫水晶 - 可出售
        merchantItemsSell.put(9001078, list9001078);

// 卡乐卡萨 (9001079) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001079 = new ArrayList<>();
        list9001079.add(new TraceKillItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0)); // 闪耀的钢铁碎片 - 可购买
        list9001079.add(new TraceKillItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7)); // 祖母绿碎片 - 可购买
        list9001079.add(new TraceKillItemInfo(4034842, "金块", 0, 0, 15346, 6)); // 金块 - 可购买
        merchantItemsBuy.put(9001079, list9001079);

        list9001079 = new ArrayList<>();
        list9001079.add(new TraceKillItemInfo(4034804, "桔梗", 0, 0, 15322, 0)); // 桔梗 - 可出售
        list9001079.add(new TraceKillItemInfo(4034805, "布片", 0, 0, 15322, 1)); // 布片 - 可出售
        list9001079.add(new TraceKillItemInfo(4034819, "天然蜂蜜", 0, 0, 15323, 7)); // 天然蜂蜜 - 可出售
        list9001079.add(new TraceKillItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1)); // 蓝宝石碎片 - 可出售
        list9001079.add(new TraceKillItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6)); // 传说中的补药 - 可出售
        list9001079.add(new TraceKillItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7)); // 精致丝绸 - 可出售
        merchantItemsSell.put(9001079, list9001079);

// 柯比 (9001080) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001080 = new ArrayList<>();
        list9001080.add(new TraceKillItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7)); // 狐狸尾巴 - 可购买
        list9001080.add(new TraceKillItemInfo(4034826, "合成石油", 0, 0, 15344, 6)); // 合成石油 - 可购买
        list9001080.add(new TraceKillItemInfo(4034841, "虎皮", 0, 0, 15346, 5)); // 虎皮 - 可购买
        merchantItemsBuy.put(9001080, list9001080);

        list9001080 = new ArrayList<>();
        list9001080.add(new TraceKillItemInfo(4034804, "桔梗", 0, 0, 15322, 0)); // 桔梗 - 可出售
        list9001080.add(new TraceKillItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2)); // 矿石碎片 - 可出售
        list9001080.add(new TraceKillItemInfo(4034820, "棉花", 0, 0, 15344, 0)); // 棉花 - 可出售
        list9001080.add(new TraceKillItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1)); // 蓝宝石碎片 - 可出售
        list9001080.add(new TraceKillItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6)); // 传说中的补药 - 可出售
        list9001080.add(new TraceKillItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0)); // 顶级紫水晶 - 可出售
        merchantItemsSell.put(9001080, list9001080);

// 财务大臣伍德万 (9001081) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001081 = new ArrayList<>();
        list9001081.add(new TraceKillItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2)); // 柔顺的丝绸 - 可购买
        list9001081.add(new TraceKillItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1)); // 毛毡布料 - 可购买
        list9001081.add(new TraceKillItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0)); // 贵族的绸缎 - 可购买
        merchantItemsBuy.put(9001081, list9001081);

        list9001081 = new ArrayList<>();
        list9001081.add(new TraceKillItemInfo(4034807, "矿泉水", 0, 0, 15322, 3)); // 矿泉水 - 可出售
        list9001081.add(new TraceKillItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5)); // 钢铁碎片 - 可出售
        list9001081.add(new TraceKillItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2)); // 沙漠石榴 - 可出售
        list9001081.add(new TraceKillItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4)); // 黄晶碎片 - 可出售
        list9001081.add(new TraceKillItemInfo(4034837, "怪物肉", 0, 0, 15346, 1)); // 怪物肉 - 可出售
        list9001081.add(new TraceKillItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3)); // 精炼黑水晶 - 可出售
        merchantItemsSell.put(9001081, list9001081);

// 扎比埃尔 (9001082) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001082 = new ArrayList<>();
        list9001082.add(new TraceKillItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5)); // 沙漠的围巾 - 可购买
        list9001082.add(new TraceKillItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4)); // 华丽的布料碎片 - 可购买
        list9001082.add(new TraceKillItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3)); // 皇帝的绸缎 - 可购买
        merchantItemsBuy.put(9001082, list9001082);

        list9001082 = new ArrayList<>();
        list9001082.add(new TraceKillItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6)); // 仙人掌树液 - 可出售
        list9001082.add(new TraceKillItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0)); // 闪耀的钢铁碎片 - 可出售
        list9001082.add(new TraceKillItemInfo(4034825, "仙桃", 0, 0, 15344, 5)); // 仙桃 - 可出售
        list9001082.add(new TraceKillItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7)); // 祖母绿碎片 - 可出售
        list9001082.add(new TraceKillItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4)); // 顶级维他命 - 可出售
        list9001082.add(new TraceKillItemInfo(4034842, "金块", 0, 0, 15346, 6)); // 金块 - 可出售
        merchantItemsSell.put(9001082, list9001082);

// 罗森 (9001083) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001083 = new ArrayList<>();
        list9001083.add(new TraceKillItemInfo(4034815, "银原石", 0, 0, 15323, 3)); // 银原石 - 可购买
        list9001083.add(new TraceKillItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2)); // 黑水晶碎片 - 可购买
        list9001083.add(new TraceKillItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1)); // 提炼的锂 - 可购买
        merchantItemsBuy.put(9001083, list9001083);

        list9001083 = new ArrayList<>();
        list9001083.add(new TraceKillItemInfo(4034807, "矿泉水", 0, 0, 15322, 3)); // 矿泉水 - 可出售
        list9001083.add(new TraceKillItemInfo(4034808, "棉花团", 0, 0, 15322, 4)); // 棉花团 - 可出售
        list9001083.add(new TraceKillItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2)); // 沙漠石榴 - 可出售
        list9001083.add(new TraceKillItemInfo(4034823, "水獭皮", 0, 0, 15344, 3)); // 水獭皮 - 可出售
        list9001083.add(new TraceKillItemInfo(4034837, "怪物肉", 0, 0, 15346, 1)); // 怪物肉 - 可出售
        list9001083.add(new TraceKillItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2)); // 丝绸布料 - 可出售
        merchantItemsSell.put(9001083, list9001083);

// 哲里 (9001084) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001084 = new ArrayList<>();
        list9001084.add(new TraceKillItemInfo(4034818, "金原石", 0, 0, 15323, 6)); // 金原石 - 可购买
        list9001084.add(new TraceKillItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5)); // 钻石碎片 - 可购买
        list9001084.add(new TraceKillItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4)); // 光辉钻石 - 可购买
        merchantItemsBuy.put(9001084, list9001084);

        list9001084 = new ArrayList<>();
        list9001084.add(new TraceKillItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6)); // 仙人掌树液 - 可出售
        list9001084.add(new TraceKillItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7)); // 狐狸尾巴 - 可出售
        list9001084.add(new TraceKillItemInfo(4034825, "仙桃", 0, 0, 15344, 5)); // 仙桃 - 可出售
        list9001084.add(new TraceKillItemInfo(4034826, "合成石油", 0, 0, 15344, 6)); // 合成石油 - 可出售
        list9001084.add(new TraceKillItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4)); // 顶级维他命 - 可出售
        list9001084.add(new TraceKillItemInfo(4034841, "虎皮", 0, 0, 15346, 5)); // 虎皮 - 可出售
        merchantItemsSell.put(9001084, list9001084);

// 雅思敏 (9001085) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001085 = new ArrayList<>();
        list9001085.add(new TraceKillItemInfo(4034813, "猪肉", 0, 0, 15323, 1)); // 猪肉 - 可购买
        list9001085.add(new TraceKillItemInfo(4034828, "白糖", 0, 0, 15345, 0)); // 白糖 - 可购买
        list9001085.add(new TraceKillItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7)); // 黄金桃子 - 可购买
        merchantItemsBuy.put(9001085, list9001085);

        list9001085 = new ArrayList<>();
        list9001085.add(new TraceKillItemInfo(4034808, "棉花团", 0, 0, 15322, 4)); // 棉花团 - 可出售
        list9001085.add(new TraceKillItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5)); // 钢铁碎片 - 可出售
        list9001085.add(new TraceKillItemInfo(4034823, "水獭皮", 0, 0, 15344, 3)); // 水獭皮 - 可出售
        list9001085.add(new TraceKillItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4)); // 黄晶碎片 - 可出售
        list9001085.add(new TraceKillItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2)); // 丝绸布料 - 可出售
        list9001085.add(new TraceKillItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3)); // 精炼黑水晶 - 可出售
        merchantItemsSell.put(9001085, list9001085);

// 萨哥特 (9001086) - 可购买的商品 + 可出售的商品
        List<TraceKillItemInfo> list9001086 = new ArrayList<>();
        list9001086.add(new TraceKillItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4)); // 动物性油脂 - 可购买
        list9001086.add(new TraceKillItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3)); // 沙漠番茄 - 可购买
        list9001086.add(new TraceKillItemInfo(4034846, "山参", 0, 0, 15347, 2)); // 山参 - 可购买
        merchantItemsBuy.put(9001086, list9001086);

        list9001086 = new ArrayList<>();
        list9001086.add(new TraceKillItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7)); // 狐狸尾巴 - 可出售
        list9001086.add(new TraceKillItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0)); // 闪耀的钢铁碎片 - 可出售
        list9001086.add(new TraceKillItemInfo(4034826, "合成石油", 0, 0, 15344, 6)); // 合成石油 - 可出售
        list9001086.add(new TraceKillItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7)); // 祖母绿碎片 - 可出售
        list9001086.add(new TraceKillItemInfo(4034841, "虎皮", 0, 0, 15346, 5)); // 虎皮 - 可出售
        list9001086.add(new TraceKillItemInfo(4034842, "金块", 0, 0, 15346, 6)); // 金块 - 可出售
        merchantItemsSell.put(9001086, list9001086);

        updatePriceBuy(merchantItemsBuy);
        updatePriceSell(merchantItemsSell);

//        // 石铁
//        int npcId = 9001072;
//        List<TraceKillItemInfo> traceKillItemInfos = new ArrayList<>();
//        traceKillItemInfos.add(new TraceKillItemInfo(4034804, 10, 50, 15322, 0)); // 不卖写0
//        traceKillItemInfos.add(new TraceKillItemInfo(4034819, 30, 0, 15323, 7)); // 不卖写0
//        traceKillItemInfos.add(new TraceKillItemInfo(4034814, 0, 60, 15323, 2)); // 不卖写0
//        traceKillItemInfos.add(new TraceKillItemInfo(4034815, 0, 60, 15323, 3)); // 不卖写0
//        traceKillItemInfos.add(new TraceKillItemInfo(4034829, 0, 60, 15345, 1)); // 不卖写0
//        npcItemInfo.put(npcId, traceKillItemInfos);

    }

    /**
     * 简单更新时间
     *
     * @param merchantItems
     */
    private static void updatePriceBuy(HashMap<Integer, List<TraceKillItemInfo>> merchantItems) {
        merchantItems.forEach((k, traceKillItemInfos) -> {
            for (TraceKillItemInfo traceKillItemInfo : traceKillItemInfos) {
                int buy = new Random().nextInt(10, 20);
                traceKillItemInfo.setBuyPrices(buy);
            }

        });
    }


    private static void updatePriceSell(HashMap<Integer, List<TraceKillItemInfo>> merchantItems) {
        merchantItems.forEach((k, traceKillItemInfos) -> {
            for (TraceKillItemInfo traceKillItemInfo : traceKillItemInfos) {
                int sell = new Random().nextInt(10, 50);
                traceKillItemInfo.setSellPrices(sell);
            }

        });
    }

    public static List<TraceKillItemInfo> getTradeKillItems(int npcId) {
        List<TraceKillItemInfo> traceKillItemInfos = merchantItemsBuy.get(npcId);
        List<TraceKillItemInfo> traceKillItemInfos1 = merchantItemsSell.get(npcId);
        ArrayList<TraceKillItemInfo> objects = new ArrayList<>();
        objects.addAll(traceKillItemInfos);
        objects.addAll(traceKillItemInfos1);
        return objects;
    }


    /**
     * 购买
     *
     * @param c
     * @param inPacket
     */
    @Handler(op = InHeader.TRADE_KING_SHOP_REQ)
    public static void handleTradeKingShopReq(Client c, InPacket inPacket) {
        //00                  // 表示卖出
        //03 00 00 00         // index
        //02 00 00 00         // 数量
        byte buySellTag = inPacket.decodeByte();
        boolean buy = buySellTag == 1; //0 表示卖 1 表示买
        int itemIndex = inPacket.decodeInt();
        int count = inPacket.decodeInt();

        Integer chrId = c.getChr().getId();
        TraceKillUserInfo userInfo = userInfoMap.get(chrId);

        // 当前绑定的NPC
        int shopNpcId = userInfo.getShopNpc();

        // 找到shop卖的东西
        List<TraceKillItemInfo> traceKillItemInfos = getTradeKillItems(shopNpcId);


        // 找到商品
        TraceKillItemInfo traceKillItemInfo = traceKillItemInfos.get(itemIndex);
        int itemId = traceKillItemInfo.getId();

        // 找到对应的qr
        int qr = traceKillItemInfo.getQr();


        // 处理自己的逻辑
        if (buy) {
            // 扣掉金币
            int buyPrices = traceKillItemInfo.getBuyPrices() * count;
            userInfo.setCount(userInfo.getCount() - buyPrices);

            // 171  传扣钱
            sendUserQR(c.getChr(), userInfo);


            // 用户增加商品
            Map<String, Integer> itemNum = userInfo.getItemNum();
            String key = traceKillItemInfo.getQr() + "_" + traceKillItemInfo.getQrEx();
            Integer i = itemNum.get(key);
            //  itemNum.merge(key, count, Integer::sum);
            if (i == null) {
                itemNum.put(key, count);
            } else {
                itemNum.put(key, i + count);
            }


            // 171  刷新对应qrvalue
            String flushQRValue = flushQRValue(userInfo, traceKillItemInfo);
            c.getChr().write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, qr, flushQRValue, (byte) 0));


            // 增加重量
            userInfo.setcWeight(userInfo.getcWeight() + count);
            // 171  传正确的
            sendUserQR(c.getChr(), userInfo);


        } else {

            // 增加金币
            int buyPrices = traceKillItemInfo.getSellPrices() * count;
            userInfo.setCount(userInfo.getCount() + buyPrices);

            // 171  传扣钱
            sendUserQR(c.getChr(), userInfo);


            // 用户增加商品
            Map<String, Integer> itemNum = userInfo.getItemNum();
            String key = traceKillItemInfo.getQr() + "_" + traceKillItemInfo.getQrEx();
            Integer i = itemNum.get(key);
            //  itemNum.merge(key, count, Integer::sum);
            if (i == null) {
                itemNum.put(key, count);
            } else {
                itemNum.put(key, Math.max(i - count, 0));
            }


            // 171  刷新对应qrvalue
            String flushQRValue = flushQRValue(userInfo, traceKillItemInfo);
            c.getChr().write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, qr, flushQRValue, (byte) 0));


            // 减少重量
            userInfo.setcWeight(userInfo.getcWeight() - count);
            // 171  传正确的
            sendUserQR(c.getChr(), userInfo);


        }


        // 171  传结果
        // sendUserQR(c.getChr(), userInfo);


        //00 00 00 00    // 固定？
        //01             // 买入 卖出
        //F4 90 3D 00   // 购买物品 0348004
        //0B 00 00 00   // 上次购买价格
        OutPacket outpacket = new OutPacket(OutHeader.TRADE_KING_SHOP_RES);
        outpacket.encodeInt(0);
        outpacket.encodeByte(buySellTag);
        outpacket.encodeInt(itemId);
        outpacket.encodeInt(traceKillItemInfo.getBuyPrices());
        c.getChr().write(outpacket);
    }

    private static String flushQRValue(TraceKillUserInfo userInfo, TraceKillItemInfo traceKillItemInfo) {
        int qr = traceKillItemInfo.getQr(); // 15322
        // 标准串 "5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0"
        String standardQrValue = shopMessageQr.get(qr);
        int qrEx = traceKillItemInfo.getQrEx();
        String userInfoKey = qr + "_" + qrEx;
        Integer traceKillItemCount = userInfo.getItemNum().get(userInfoKey);


        // 切分 key=value 项
        String[] parts = standardQrValue.split(";");

        // 替换对应 key 的 value
        for (int i = 0; i < parts.length; i++) {
            String[] kv = parts[i].split("=");
            if (kv.length == 2 && kv[0].equals(String.valueOf(qrEx))) {
                parts[i] = qrEx + "=" + traceKillItemCount;
            }
        }

        // 拼回字符串
        String updated = String.join(";", parts);
        return updated;

    }

    /**
     * 到时间刷新了：获取信息
     *
     * @param c
     * @param inPacket
     */
    @Handler(op = InHeader.TRADE_KING_SHOP_INFO_REQ)
    public static void handleTradeKingSHopINfoReq(Client c, InPacket inPacket) {
        // 客户端发送的0内容

        sendExpiredTime(c.getChr());

        updatePriceBuy(merchantItemsBuy);
        updatePriceSell(merchantItemsSell);
        clickTradeKingNPC(c.getChr(), 0);


    }


    /**
     * 初始化
     *
     * @param chrId
     * @return
     */
    public static TraceKillUserInfo initTradeKingUser(Integer chrId) {
        TraceKillUserInfo userInfo = new TraceKillUserInfo();
        userInfo.setShopNpc(-1);
        userInfo.setcWeight(0);
        userInfo.setCount(999);
        userInfo.setmWeight(125);
        userInfo.setScount(0);

        userInfoMap.put(chrId, userInfo);

        return userInfo;
    }


    /**
     * 设置message的qr value
     *
     * @param qrKey
     * @param qrValue
     */
    public static void sendQRValue(Char chr, int qrKey, String qrValue) {
        QuestManager qm = chr.getQuestManager();
        Quest q = qm.getQuests().getOrDefault(qrKey, null);
        if (q == null) {
            q = new Quest(qrKey, QuestStatus.Started);
            qm.addQuest(q);
        }
        q.setQrValue(qrValue);
        chr.write(WvsContext.questRecordMessage(q));
        chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, q.getQRKey(), q.getQRValue(), (byte) 0));
        chr.chatMessage(String.format("Sent QRValue with  QuestId %d, QrValue %s", qrKey, q.getQRValue()));
    }


    // NPC点击
    public static void clickTradeKingNPC(Char chr, int npcId) {
        Integer chrId = chr.getId();
        TraceKillUserInfo userInfo = userInfoMap.get(chrId);
        if (npcId == 0) {
            npcId = userInfo.getShopNpc();
        } else {
            // 设置绑定
            userInfo.setShopNpc(npcId);
        }


        // 发送一次自己的
        sendUserQR(chr, userInfo);

        //发过期时间
//        String sendExpiredTimeStr = sendExpiredTime(chr);

        // 找到shop卖的东西
        List<TraceKillItemInfo> traceKillItemInfos = getTradeKillItems(npcId);


        OutPacket outpacket = new OutPacket(OutHeader.TRADE_KING_SHOP_ITEM);
        outpacket.encodeInt(npcId);

        // 发的下次更新时间
        String time = "0";
        outpacket.encodeInt(15317);  // quest_id ？？？
        outpacket.encodeString(time);


//        outpacket.encodeInt(15324);  // quest_id
//        String format = String.format("shop=%d;cWeight=%d;count=%d;mWeight=%d", userInfo.getShopNpc(), userInfo.getcWeight(), userInfo.getCount(), userInfo.getmWeight());
//        outpacket.encodeString(time);


        // 写个map控制每个npc的商品
        outpacket.encodeInt(traceKillItemInfos.size());
        for (TraceKillItemInfo traceKillItemInfo : traceKillItemInfos) {
            //id
            outpacket.encodeInt(traceKillItemInfo.getId());
            // buy
            outpacket.encodeInt(traceKillItemInfo.getBuyPrices());
            // sell
            outpacket.encodeInt(traceKillItemInfo.getSellPrices());
        }
        chr.write(outpacket);
    }


    public static void getTradeKingEnd(Char chr) {
        chr.getTemporaryStatManager().removeStatsBySkill(skillID);

    }
    public static void getTradeKingInit(Char chr) {
        Integer id = chr.getId();
        TraceKillUserInfo userInfo = TraceKillHandler.initTradeKingUser(id);
        sendUserQR(chr, userInfo);
//        sendUserQRInit(chr, userInfo);

        // UI显示满 4和尚+ 1狐狸  DD 3B 1=4;4=1
        TraceKillHandler.sendQRValue(chr, 15325, "1=4;4=1");
//        TraceKillHandler.sendQRValue(chr, 15325, "0");

        // todo 发送45次shop的message？
        shopMessageQr.forEach((key, value) -> {
            // 每个发一次就够了 客户端会拿到的
            chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, key, value, (byte) 0));
        });
        sendExpiredTime(chr);


        // 上坐骑
        // 骆驼
        int slv = 1;
        InPacket inPacket = null;
        SkillUseInfo skillUseInfo = null;
        Job sourceJobHandler = chr.getJobHandler();
        sourceJobHandler.handleSkill(chr, chr.getTemporaryStatManager(), skillID, slv, inPacket, skillUseInfo);

//        chr.getTemporaryStatManager().sendResetStatPacket(true);




//        shopMessageQr.forEach((key, value) -> {
//            String[] split = value.split(";");
//            int length = split.length;
//            for (int i = 0; i < length; i++) {
////                TraceKillHandler.sendQRValue(chr, key, value);
//                chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE,
//                        key, value, (byte) 0));
//            }
//        });

//        int qr = 15322;
//        String qrValue = shopMessageQr.get(qr);
//        String[] split = qrValue.split(";");
//        int length = split.length;
//        StringBuilder sb = new StringBuilder(split[0]);
//        for (int i = 1; i <= length; i++) {
//            String value = sb.toString();
//            chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE,
//                    qr, value, (byte) 0));
//            if (i != length) {
//                sb.append(";");
//                sb.append(split[i]);
//            }
//        }

    }


    private static void sendUserQR(Char chr, TraceKillUserInfo userInfo) {
        String format = String.format("shop=%d;cWeight=%d;count=%d;mWeight=%d", userInfo.getShopNpc(), userInfo.getcWeight(), userInfo.getCount(), userInfo.getmWeight());
        // 满背包125    15324 shop=-1;cWeight=0;count=50;mWeight=125
//        TraceKillHandler.sendQRValue(chr, 15324, format);

        chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, 15324, format, (byte) 0));
        chr.chatMessage(String.format("Sent QRValue with  QuestId %d, QrValue %s", 15324, format));
    }


    private static void sendUserQRInit(Char chr, TraceKillUserInfo userInfo) {
        String format = String.format("shop=%d;cWeight=%d;count=%d;mWeight=%d", userInfo.getShopNpc(), userInfo.getcWeight(), userInfo.getCount(), userInfo.getmWeight());
        // 满背包125    15324 shop=-1;cWeight=0;count=50;mWeight=125
        TraceKillHandler.sendQRValue(chr, 15324, format);
    }

    // UTCshijian?
    private static String sendExpiredTime(Char chr) {
        // 比实际慢一个小时
        LocalDateTime now = LocalDateTime.now().plusMinutes(1).minusHours(6);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String s = "0=" + now.format(fmt);
        chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, 15317, s, (byte) 0));
        return s;
    }
}
