package net.swordie.ms.handlers;

import com.dwang.MapperProxy;
import com.dwang.dao.TraceKingUserInfo;
import com.dwang.data.mapper.TraceKingUserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.quest.Quest;
import net.swordie.ms.client.character.quest.QuestManager;
import net.swordie.ms.client.character.skills.info.SkillUseInfo;
import net.swordie.ms.client.jobs.Job;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.connection.packet.WvsContext;
import net.swordie.ms.enums.MessageType;
import net.swordie.ms.enums.QuestStatus;
import net.swordie.ms.handlers.header.InHeader;
import net.swordie.ms.handlers.header.OutHeader;
import net.swordie.ms.traceking.TraceKingItemInfo;
import net.swordie.ms.traceking.TraceKingQuestRxCode;
import org.checkerframework.checker.units.qual.A;

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
public class TraceKingHandler {


    // 对应 qrkey value
    private static Map<Integer, String> shopMessageQr = new HashMap<>();

    // 每个商店卖的东西 npcID，
    private static final HashMap<Integer, List<TraceKingItemInfo>> merchantItemsBuy = new HashMap<>();
    private static final HashMap<Integer, List<TraceKingItemInfo>> merchantItemsSell = new HashMap<>();

    //
    private static final List<TraceKingItemInfo> buyAll = addAllItemInfo();
    private static final List<TraceKingItemInfo> sellAll = addAllItemInfo();


    private static HashMap<Integer, TraceKingUserInfo> userInfoCache = new HashMap<>();
    private static Integer initRidSkill = 80001950;
    private static Integer initCount = 50;
    private static String initWorker = "0";

    /**
     * 数据库操作代理类
     */
    private static TraceKingUserInfoMapper userInfoMapper = MapperProxy.create(TraceKingUserInfoMapper.class);


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


// 创建所有物品的TraceKingItemInfo对象（包含QR和QREx信息）
/*        TraceKingItemInfo item4034804 = new TraceKingItemInfo(4034804, "桔梗", 0, 0, 15322, 0);
        TraceKingItemInfo item4034805 = new TraceKingItemInfo(4034805, "布片", 0, 0, 15322, 1);
        TraceKingItemInfo item4034806 = new TraceKingItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2);
        TraceKingItemInfo item4034807 = new TraceKingItemInfo(4034807, "矿泉水", 0, 0, 15322, 3);
        TraceKingItemInfo item4034808 = new TraceKingItemInfo(4034808, "棉花团", 0, 0, 15322, 4);
        TraceKingItemInfo item4034809 = new TraceKingItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5);
        TraceKingItemInfo item4034810 = new TraceKingItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6);
        TraceKingItemInfo item4034811 = new TraceKingItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7);
        TraceKingItemInfo item4034812 = new TraceKingItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0);
        TraceKingItemInfo item4034813 = new TraceKingItemInfo(4034813, "猪肉", 0, 0, 15323, 1);
        TraceKingItemInfo item4034814 = new TraceKingItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2);
        TraceKingItemInfo item4034815 = new TraceKingItemInfo(4034815, "银原石", 0, 0, 15323, 3);
        TraceKingItemInfo item4034816 = new TraceKingItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4);
        TraceKingItemInfo item4034817 = new TraceKingItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5);
        TraceKingItemInfo item4034818 = new TraceKingItemInfo(4034818, "金原石", 0, 0, 15323, 6);
        TraceKingItemInfo item4034819 = new TraceKingItemInfo(4034819, "天然蜂蜜", 0, 0, 15323, 7);
        TraceKingItemInfo item4034820 = new TraceKingItemInfo(4034820, "棉花", 0, 0, 15344, 0);
        TraceKingItemInfo item4034821 = new TraceKingItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1);
        TraceKingItemInfo item4034822 = new TraceKingItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2);
        TraceKingItemInfo item4034823 = new TraceKingItemInfo(4034823, "水獭皮", 0, 0, 15344, 3);
        TraceKingItemInfo item4034824 = new TraceKingItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4);
        TraceKingItemInfo item4034825 = new TraceKingItemInfo(4034825, "仙桃", 0, 0, 15344, 5);
        TraceKingItemInfo item4034826 = new TraceKingItemInfo(4034826, "合成石油", 0, 0, 15344, 6);
        TraceKingItemInfo item4034827 = new TraceKingItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7);
        TraceKingItemInfo item4034828 = new TraceKingItemInfo(4034828, "白糖", 0, 0, 15345, 0);
        TraceKingItemInfo item4034829 = new TraceKingItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1);
        TraceKingItemInfo item4034830 = new TraceKingItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2);
        TraceKingItemInfo item4034831 = new TraceKingItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3);
        TraceKingItemInfo item4034832 = new TraceKingItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4);
        TraceKingItemInfo item4034833 = new TraceKingItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5);
        TraceKingItemInfo item4034834 = new TraceKingItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6);
        TraceKingItemInfo item4034835 = new TraceKingItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7);
        TraceKingItemInfo item4034836 = new TraceKingItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0);
        TraceKingItemInfo item4034837 = new TraceKingItemInfo(4034837, "怪物肉", 0, 0, 15346, 1);
        TraceKingItemInfo item4034838 = new TraceKingItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2);
        TraceKingItemInfo item4034839 = new TraceKingItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3);
        TraceKingItemInfo item4034840 = new TraceKingItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4);
        TraceKingItemInfo item4034841 = new TraceKingItemInfo(4034841, "虎皮", 0, 0, 15346, 5);
        TraceKingItemInfo item4034842 = new TraceKingItemInfo(4034842, "金块", 0, 0, 15346, 6);
        TraceKingItemInfo item4034843 = new TraceKingItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7);
        TraceKingItemInfo item4034844 = new TraceKingItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0);
        TraceKingItemInfo item4034845 = new TraceKingItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1);
        TraceKingItemInfo item4034846 = new TraceKingItemInfo(4034846, "山参", 0, 0, 15347, 2);
        TraceKingItemInfo item4034847 = new TraceKingItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3);
        TraceKingItemInfo item4034848 = new TraceKingItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4);*/


// 石铁 (9001072) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001072 = new ArrayList<>();
        list9001072.add(new TraceKingItemInfo(4034804, "桔梗", 0, 0, 15322, 0)); // 桔梗 - 可购买
        list9001072.add(new TraceKingItemInfo(4034819, "天然蜂蜜", 0, 0, 15323, 7)); // 天然蜂蜜 - 可购买
        list9001072.add(new TraceKingItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6)); // 传说中的补药 - 可购买
        merchantItemsBuy.put(9001072, list9001072);

        list9001072 = new ArrayList<>();
        list9001072.add(new TraceKingItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2)); // 柔顺的丝绸 - 可出售
        list9001072.add(new TraceKingItemInfo(4034815, "银原石", 0, 0, 15323, 3)); // 银原石 - 可出售
        list9001072.add(new TraceKingItemInfo(4034828, "白糖", 0, 0, 15345, 0)); // 白糖 - 可出售
        list9001072.add(new TraceKingItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1)); // 毛毡布料 - 可出售
        list9001072.add(new TraceKingItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0)); // 贵族的绸缎 - 可出售
        list9001072.add(new TraceKingItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1)); // 提炼的锂 - 可出售
        merchantItemsSell.put(9001072, list9001072);

// 奇里文 (9001073) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001073 = new ArrayList<>();
        list9001073.add(new TraceKingItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2)); // 矿石碎片 - 可购买
        list9001073.add(new TraceKingItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1)); // 蓝宝石碎片 - 可购买
        list9001073.add(new TraceKingItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0)); // 顶级紫水晶 - 可购买
        merchantItemsBuy.put(9001073, list9001073);

        list9001073 = new ArrayList<>();
        list9001073.add(new TraceKingItemInfo(4034813, "猪肉", 0, 0, 15323, 1)); // 猪肉 - 可出售
        list9001073.add(new TraceKingItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2)); // 柔顺的丝绸 - 可出售
        list9001073.add(new TraceKingItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2)); // 黑水晶碎片 - 可出售
        list9001073.add(new TraceKingItemInfo(4034828, "白糖", 0, 0, 15345, 0)); // 白糖 - 可出售
        list9001073.add(new TraceKingItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7)); // 黄金桃子 - 可出售
        list9001073.add(new TraceKingItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0)); // 贵族的绸缎 - 可出售
        merchantItemsSell.put(9001073, list9001073);

// 兰明 (9001074) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001074 = new ArrayList<>();
        list9001074.add(new TraceKingItemInfo(4034805, "布片", 0, 0, 15322, 1)); // 布片 - 可购买
        list9001074.add(new TraceKingItemInfo(4034820, "棉花", 0, 0, 15344, 0)); // 棉花 - 可购买
        list9001074.add(new TraceKingItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7)); // 精致丝绸 - 可购买
        merchantItemsBuy.put(9001074, list9001074);

        list9001074 = new ArrayList<>();
        list9001074.add(new TraceKingItemInfo(4034813, "猪肉", 0, 0, 15323, 1)); // 猪肉 - 可出售
        list9001074.add(new TraceKingItemInfo(4034815, "银原石", 0, 0, 15323, 3)); // 银原石 - 可出售
        list9001074.add(new TraceKingItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2)); // 黑水晶碎片 - 可出售
        list9001074.add(new TraceKingItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1)); // 毛毡布料 - 可出售
        list9001074.add(new TraceKingItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7)); // 黄金桃子 - 可出售
        list9001074.add(new TraceKingItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1)); // 提炼的锂 - 可出售
        merchantItemsSell.put(9001074, list9001074);

// 史莱 (9001075) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001075 = new ArrayList<>();
        list9001075.add(new TraceKingItemInfo(4034807, "矿泉水", 0, 0, 15322, 3)); // 矿泉水 - 可购买
        list9001075.add(new TraceKingItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2)); // 沙漠石榴 - 可购买
        list9001075.add(new TraceKingItemInfo(4034837, "怪物肉", 0, 0, 15346, 1)); // 怪物肉 - 可购买
        merchantItemsBuy.put(9001075, list9001075);

        list9001075 = new ArrayList<>();
        list9001075.add(new TraceKingItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5)); // 沙漠的围巾 - 可出售
        list9001075.add(new TraceKingItemInfo(4034818, "金原石", 0, 0, 15323, 6)); // 金原石 - 可出售
        list9001075.add(new TraceKingItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3)); // 沙漠番茄 - 可出售
        list9001075.add(new TraceKingItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4)); // 华丽的布料碎片 - 可出售
        list9001075.add(new TraceKingItemInfo(4034846, "山参", 0, 0, 15347, 2)); // 山参 - 可出售
        list9001075.add(new TraceKingItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4)); // 光辉钻石 - 可出售
        merchantItemsSell.put(9001075, list9001075);

// 工作人员O (9001076) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001076 = new ArrayList<>();
        list9001076.add(new TraceKingItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5)); // 钢铁碎片 - 可购买
        list9001076.add(new TraceKingItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4)); // 黄晶碎片 - 可购买
        list9001076.add(new TraceKingItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3)); // 精炼黑水晶 - 可购买
        merchantItemsBuy.put(9001076, list9001076);

        list9001076 = new ArrayList<>();
        list9001076.add(new TraceKingItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4)); // 动物性油脂 - 可出售
        list9001076.add(new TraceKingItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5)); // 沙漠的围巾 - 可出售
        list9001076.add(new TraceKingItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3)); // 沙漠番茄 - 可出售
        list9001076.add(new TraceKingItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5)); // 钻石碎片 - 可出售
        list9001076.add(new TraceKingItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3)); // 皇帝的绸缎 - 可出售
        list9001076.add(new TraceKingItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4)); // 光辉钻石 - 可出售
        merchantItemsSell.put(9001076, list9001076);

// 玛帕 (9001077) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001077 = new ArrayList<>();
        list9001077.add(new TraceKingItemInfo(4034808, "棉花团", 0, 0, 15322, 4)); // 棉花团 - 可购买
        list9001077.add(new TraceKingItemInfo(4034823, "水獭皮", 0, 0, 15344, 3)); // 水獭皮 - 可购买
        list9001077.add(new TraceKingItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2)); // 丝绸布料 - 可购买
        merchantItemsBuy.put(9001077, list9001077);

        list9001077 = new ArrayList<>();
        list9001077.add(new TraceKingItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4)); // 动物性油脂 - 可出售
        list9001077.add(new TraceKingItemInfo(4034818, "金原石", 0, 0, 15323, 6)); // 金原石 - 可出售
        list9001077.add(new TraceKingItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4)); // 华丽的布料碎片 - 可出售
        list9001077.add(new TraceKingItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5)); // 钻石碎片 - 可出售
        list9001077.add(new TraceKingItemInfo(4034846, "山参", 0, 0, 15347, 2)); // 山参 - 可出售
        list9001077.add(new TraceKingItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3)); // 皇帝的绸缎 - 可出售
        merchantItemsSell.put(9001077, list9001077);

// 豆尔 (9001078) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001078 = new ArrayList<>();
        list9001078.add(new TraceKingItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6)); // 仙人掌树液 - 可购买
        list9001078.add(new TraceKingItemInfo(4034825, "仙桃", 0, 0, 15344, 5)); // 仙桃 - 可购买
        list9001078.add(new TraceKingItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4)); // 顶级维他命 - 可购买
        merchantItemsBuy.put(9001078, list9001078);

        list9001078 = new ArrayList<>();
        list9001078.add(new TraceKingItemInfo(4034805, "布片", 0, 0, 15322, 1)); // 布片 - 可出售
        list9001078.add(new TraceKingItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2)); // 矿石碎片 - 可出售
        list9001078.add(new TraceKingItemInfo(4034820, "棉花", 0, 0, 15344, 0)); // 棉花 - 可出售
        list9001078.add(new TraceKingItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1)); // 蓝宝石碎片 - 可出售
        list9001078.add(new TraceKingItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7)); // 精致丝绸 - 可出售
        list9001078.add(new TraceKingItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0)); // 顶级紫水晶 - 可出售
        merchantItemsSell.put(9001078, list9001078);

// 卡乐卡萨 (9001079) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001079 = new ArrayList<>();
        list9001079.add(new TraceKingItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0)); // 闪耀的钢铁碎片 - 可购买
        list9001079.add(new TraceKingItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7)); // 祖母绿碎片 - 可购买
        list9001079.add(new TraceKingItemInfo(4034842, "金块", 0, 0, 15346, 6)); // 金块 - 可购买
        merchantItemsBuy.put(9001079, list9001079);

        list9001079 = new ArrayList<>();
        list9001079.add(new TraceKingItemInfo(4034804, "桔梗", 0, 0, 15322, 0)); // 桔梗 - 可出售
        list9001079.add(new TraceKingItemInfo(4034805, "布片", 0, 0, 15322, 1)); // 布片 - 可出售
        list9001079.add(new TraceKingItemInfo(4034819, "天然蜂蜜", 0, 0, 15323, 7)); // 天然蜂蜜 - 可出售
        list9001079.add(new TraceKingItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1)); // 蓝宝石碎片 - 可出售
        list9001079.add(new TraceKingItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6)); // 传说中的补药 - 可出售
        list9001079.add(new TraceKingItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7)); // 精致丝绸 - 可出售
        merchantItemsSell.put(9001079, list9001079);

// 柯比 (9001080) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001080 = new ArrayList<>();
        list9001080.add(new TraceKingItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7)); // 狐狸尾巴 - 可购买
        list9001080.add(new TraceKingItemInfo(4034826, "合成石油", 0, 0, 15344, 6)); // 合成石油 - 可购买
        list9001080.add(new TraceKingItemInfo(4034841, "虎皮", 0, 0, 15346, 5)); // 虎皮 - 可购买
        merchantItemsBuy.put(9001080, list9001080);

        list9001080 = new ArrayList<>();
        list9001080.add(new TraceKingItemInfo(4034804, "桔梗", 0, 0, 15322, 0)); // 桔梗 - 可出售
        list9001080.add(new TraceKingItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2)); // 矿石碎片 - 可出售
        list9001080.add(new TraceKingItemInfo(4034820, "棉花", 0, 0, 15344, 0)); // 棉花 - 可出售
        list9001080.add(new TraceKingItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1)); // 蓝宝石碎片 - 可出售
        list9001080.add(new TraceKingItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6)); // 传说中的补药 - 可出售
        list9001080.add(new TraceKingItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0)); // 顶级紫水晶 - 可出售
        merchantItemsSell.put(9001080, list9001080);

// 财务大臣伍德万 (9001081) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001081 = new ArrayList<>();
        list9001081.add(new TraceKingItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2)); // 柔顺的丝绸 - 可购买
        list9001081.add(new TraceKingItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1)); // 毛毡布料 - 可购买
        list9001081.add(new TraceKingItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0)); // 贵族的绸缎 - 可购买
        merchantItemsBuy.put(9001081, list9001081);

        list9001081 = new ArrayList<>();
        list9001081.add(new TraceKingItemInfo(4034807, "矿泉水", 0, 0, 15322, 3)); // 矿泉水 - 可出售
        list9001081.add(new TraceKingItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5)); // 钢铁碎片 - 可出售
        list9001081.add(new TraceKingItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2)); // 沙漠石榴 - 可出售
        list9001081.add(new TraceKingItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4)); // 黄晶碎片 - 可出售
        list9001081.add(new TraceKingItemInfo(4034837, "怪物肉", 0, 0, 15346, 1)); // 怪物肉 - 可出售
        list9001081.add(new TraceKingItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3)); // 精炼黑水晶 - 可出售
        merchantItemsSell.put(9001081, list9001081);

// 扎比埃尔 (9001082) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001082 = new ArrayList<>();
        list9001082.add(new TraceKingItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5)); // 沙漠的围巾 - 可购买
        list9001082.add(new TraceKingItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4)); // 华丽的布料碎片 - 可购买
        list9001082.add(new TraceKingItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3)); // 皇帝的绸缎 - 可购买
        merchantItemsBuy.put(9001082, list9001082);

        list9001082 = new ArrayList<>();
        list9001082.add(new TraceKingItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6)); // 仙人掌树液 - 可出售
        list9001082.add(new TraceKingItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0)); // 闪耀的钢铁碎片 - 可出售
        list9001082.add(new TraceKingItemInfo(4034825, "仙桃", 0, 0, 15344, 5)); // 仙桃 - 可出售
        list9001082.add(new TraceKingItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7)); // 祖母绿碎片 - 可出售
        list9001082.add(new TraceKingItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4)); // 顶级维他命 - 可出售
        list9001082.add(new TraceKingItemInfo(4034842, "金块", 0, 0, 15346, 6)); // 金块 - 可出售
        merchantItemsSell.put(9001082, list9001082);

// 罗森 (9001083) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001083 = new ArrayList<>();
        list9001083.add(new TraceKingItemInfo(4034815, "银原石", 0, 0, 15323, 3)); // 银原石 - 可购买
        list9001083.add(new TraceKingItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2)); // 黑水晶碎片 - 可购买
        list9001083.add(new TraceKingItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1)); // 提炼的锂 - 可购买
        merchantItemsBuy.put(9001083, list9001083);

        list9001083 = new ArrayList<>();
        list9001083.add(new TraceKingItemInfo(4034807, "矿泉水", 0, 0, 15322, 3)); // 矿泉水 - 可出售
        list9001083.add(new TraceKingItemInfo(4034808, "棉花团", 0, 0, 15322, 4)); // 棉花团 - 可出售
        list9001083.add(new TraceKingItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2)); // 沙漠石榴 - 可出售
        list9001083.add(new TraceKingItemInfo(4034823, "水獭皮", 0, 0, 15344, 3)); // 水獭皮 - 可出售
        list9001083.add(new TraceKingItemInfo(4034837, "怪物肉", 0, 0, 15346, 1)); // 怪物肉 - 可出售
        list9001083.add(new TraceKingItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2)); // 丝绸布料 - 可出售
        merchantItemsSell.put(9001083, list9001083);

// 哲里 (9001084) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001084 = new ArrayList<>();
        list9001084.add(new TraceKingItemInfo(4034818, "金原石", 0, 0, 15323, 6)); // 金原石 - 可购买
        list9001084.add(new TraceKingItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5)); // 钻石碎片 - 可购买
        list9001084.add(new TraceKingItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4)); // 光辉钻石 - 可购买
        merchantItemsBuy.put(9001084, list9001084);

        list9001084 = new ArrayList<>();
        list9001084.add(new TraceKingItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6)); // 仙人掌树液 - 可出售
        list9001084.add(new TraceKingItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7)); // 狐狸尾巴 - 可出售
        list9001084.add(new TraceKingItemInfo(4034825, "仙桃", 0, 0, 15344, 5)); // 仙桃 - 可出售
        list9001084.add(new TraceKingItemInfo(4034826, "合成石油", 0, 0, 15344, 6)); // 合成石油 - 可出售
        list9001084.add(new TraceKingItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4)); // 顶级维他命 - 可出售
        list9001084.add(new TraceKingItemInfo(4034841, "虎皮", 0, 0, 15346, 5)); // 虎皮 - 可出售
        merchantItemsSell.put(9001084, list9001084);

// 雅思敏 (9001085) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001085 = new ArrayList<>();
        list9001085.add(new TraceKingItemInfo(4034813, "猪肉", 0, 0, 15323, 1)); // 猪肉 - 可购买
        list9001085.add(new TraceKingItemInfo(4034828, "白糖", 0, 0, 15345, 0)); // 白糖 - 可购买
        list9001085.add(new TraceKingItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7)); // 黄金桃子 - 可购买
        merchantItemsBuy.put(9001085, list9001085);

        list9001085 = new ArrayList<>();
        list9001085.add(new TraceKingItemInfo(4034808, "棉花团", 0, 0, 15322, 4)); // 棉花团 - 可出售
        list9001085.add(new TraceKingItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5)); // 钢铁碎片 - 可出售
        list9001085.add(new TraceKingItemInfo(4034823, "水獭皮", 0, 0, 15344, 3)); // 水獭皮 - 可出售
        list9001085.add(new TraceKingItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4)); // 黄晶碎片 - 可出售
        list9001085.add(new TraceKingItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2)); // 丝绸布料 - 可出售
        list9001085.add(new TraceKingItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3)); // 精炼黑水晶 - 可出售
        merchantItemsSell.put(9001085, list9001085);

// 萨哥特 (9001086) - 可购买的商品 + 可出售的商品
        List<TraceKingItemInfo> list9001086 = new ArrayList<>();
        list9001086.add(new TraceKingItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4)); // 动物性油脂 - 可购买
        list9001086.add(new TraceKingItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3)); // 沙漠番茄 - 可购买
        list9001086.add(new TraceKingItemInfo(4034846, "山参", 0, 0, 15347, 2)); // 山参 - 可购买
        merchantItemsBuy.put(9001086, list9001086);

        list9001086 = new ArrayList<>();
        list9001086.add(new TraceKingItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7)); // 狐狸尾巴 - 可出售
        list9001086.add(new TraceKingItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0)); // 闪耀的钢铁碎片 - 可出售
        list9001086.add(new TraceKingItemInfo(4034826, "合成石油", 0, 0, 15344, 6)); // 合成石油 - 可出售
        list9001086.add(new TraceKingItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7)); // 祖母绿碎片 - 可出售
        list9001086.add(new TraceKingItemInfo(4034841, "虎皮", 0, 0, 15346, 5)); // 虎皮 - 可出售
        list9001086.add(new TraceKingItemInfo(4034842, "金块", 0, 0, 15346, 6)); // 金块 - 可出售
        merchantItemsSell.put(9001086, list9001086);

        updatePrice();


    }

    private static void updatePrice() {
        updatePriceBuy(merchantItemsBuy);
        updatePriceSell(merchantItemsSell);

        updatePriceBuyAll(buyAll);
        updatePriceSellAll(sellAll);

    }

    private static List<TraceKingItemInfo> addAllItemInfo() {
        List<TraceKingItemInfo> traceKingItemInfos = new ArrayList<>();
        traceKingItemInfos.add(new TraceKingItemInfo(4034804, "桔梗", 0, 0, 15322, 0));
        traceKingItemInfos.add(new TraceKingItemInfo(4034805, "布片", 0, 0, 15322, 1));
        traceKingItemInfos.add(new TraceKingItemInfo(4034806, "矿石碎片", 0, 0, 15322, 2));
        traceKingItemInfos.add(new TraceKingItemInfo(4034807, "矿泉水", 0, 0, 15322, 3));
        traceKingItemInfos.add(new TraceKingItemInfo(4034808, "棉花团", 0, 0, 15322, 4));
        traceKingItemInfos.add(new TraceKingItemInfo(4034809, "钢铁碎片", 0, 0, 15322, 5));
        traceKingItemInfos.add(new TraceKingItemInfo(4034810, "仙人掌树液", 0, 0, 15322, 6));
        traceKingItemInfos.add(new TraceKingItemInfo(4034811, "狐狸尾巴", 0, 0, 15322, 7));
        traceKingItemInfos.add(new TraceKingItemInfo(4034812, "闪耀的钢铁碎片", 0, 0, 15323, 0));
        traceKingItemInfos.add(new TraceKingItemInfo(4034813, "猪肉", 0, 0, 15323, 1));
        traceKingItemInfos.add(new TraceKingItemInfo(4034814, "柔顺的丝绸", 0, 0, 15323, 2));
        traceKingItemInfos.add(new TraceKingItemInfo(4034815, "银原石", 0, 0, 15323, 3));
        traceKingItemInfos.add(new TraceKingItemInfo(4034816, "动物性油脂", 0, 0, 15323, 4));
        traceKingItemInfos.add(new TraceKingItemInfo(4034817, "沙漠的围巾", 0, 0, 15323, 5));
        traceKingItemInfos.add(new TraceKingItemInfo(4034818, "金原石", 0, 0, 15323, 6));
        traceKingItemInfos.add(new TraceKingItemInfo(4034819, "天然蜂蜜", 0, 0, 15323, 7));
        traceKingItemInfos.add(new TraceKingItemInfo(4034820, "棉花", 0, 0, 15344, 0));
        traceKingItemInfos.add(new TraceKingItemInfo(4034821, "蓝宝石碎片", 0, 0, 15344, 1));
        traceKingItemInfos.add(new TraceKingItemInfo(4034822, "沙漠石榴", 0, 0, 15344, 2));
        traceKingItemInfos.add(new TraceKingItemInfo(4034823, "水獭皮", 0, 0, 15344, 3));
        traceKingItemInfos.add(new TraceKingItemInfo(4034824, "黄晶碎片", 0, 0, 15344, 4));
        traceKingItemInfos.add(new TraceKingItemInfo(4034825, "仙桃", 0, 0, 15344, 5));
        traceKingItemInfos.add(new TraceKingItemInfo(4034826, "合成石油", 0, 0, 15344, 6));
        traceKingItemInfos.add(new TraceKingItemInfo(4034827, "祖母绿碎片", 0, 0, 15344, 7));
        traceKingItemInfos.add(new TraceKingItemInfo(4034828, "白糖", 0, 0, 15345, 0));
        traceKingItemInfos.add(new TraceKingItemInfo(4034829, "毛毡布料", 0, 0, 15345, 1));
        traceKingItemInfos.add(new TraceKingItemInfo(4034830, "黑水晶碎片", 0, 0, 15345, 2));
        traceKingItemInfos.add(new TraceKingItemInfo(4034831, "沙漠番茄", 0, 0, 15345, 3));
        traceKingItemInfos.add(new TraceKingItemInfo(4034832, "华丽的布料碎片", 0, 0, 15345, 4));
        traceKingItemInfos.add(new TraceKingItemInfo(4034833, "钻石碎片", 0, 0, 15345, 5));
        traceKingItemInfos.add(new TraceKingItemInfo(4034834, "传说中的补药", 0, 0, 15345, 6));
        traceKingItemInfos.add(new TraceKingItemInfo(4034835, "精致丝绸", 0, 0, 15345, 7));
        traceKingItemInfos.add(new TraceKingItemInfo(4034836, "顶级紫水晶", 0, 0, 15346, 0));
        traceKingItemInfos.add(new TraceKingItemInfo(4034837, "怪物肉", 0, 0, 15346, 1));
        traceKingItemInfos.add(new TraceKingItemInfo(4034838, "丝绸布料", 0, 0, 15346, 2));
        traceKingItemInfos.add(new TraceKingItemInfo(4034839, "精炼黑水晶", 0, 0, 15346, 3));
        traceKingItemInfos.add(new TraceKingItemInfo(4034840, "顶级维他命", 0, 0, 15346, 4));
        traceKingItemInfos.add(new TraceKingItemInfo(4034841, "虎皮", 0, 0, 15346, 5));
        traceKingItemInfos.add(new TraceKingItemInfo(4034842, "金块", 0, 0, 15346, 6));
        traceKingItemInfos.add(new TraceKingItemInfo(4034843, "黄金桃子", 0, 0, 15346, 7));
        traceKingItemInfos.add(new TraceKingItemInfo(4034844, "贵族的绸缎", 0, 0, 15347, 0));
        traceKingItemInfos.add(new TraceKingItemInfo(4034845, "提炼的锂", 0, 0, 15347, 1));
        traceKingItemInfos.add(new TraceKingItemInfo(4034846, "山参", 0, 0, 15347, 2));
        traceKingItemInfos.add(new TraceKingItemInfo(4034847, "皇帝的绸缎", 0, 0, 15347, 3));
        traceKingItemInfos.add(new TraceKingItemInfo(4034848, "光辉钻石", 0, 0, 15347, 4));
        return traceKingItemInfos;
    }

    /**
     * 简单更新物价
     *
     * @param merchantItems
     */
    private static void updatePriceBuy(HashMap<Integer, List<TraceKingItemInfo>> merchantItems) {
        merchantItems.forEach((k, TraceKingItemInfos) -> {
            for (TraceKingItemInfo traceKingItemInfo : TraceKingItemInfos) {
                int buy = new Random().nextInt(10, 20);
                traceKingItemInfo.setBuyPrices(buy);
            }

        });
    }

    private static void updatePriceBuyAll(List<TraceKingItemInfo> merchantItems) {
        for (TraceKingItemInfo traceKingItemInfo : merchantItems) {
            int buy = new Random().nextInt(1, 10);
            traceKingItemInfo.setBuyPrices(buy);
        }
    }

    private static void updatePriceSell(HashMap<Integer, List<TraceKingItemInfo>> merchantItems) {
        merchantItems.forEach((k, TraceKingItemInfos) -> {
            for (TraceKingItemInfo traceKingItemInfo : TraceKingItemInfos) {
                int sell = new Random().nextInt(10, 50);
                traceKingItemInfo.setSellPrices(sell);
            }

        });
    }

    private static void updatePriceSellAll(List<TraceKingItemInfo> merchantItems) {
        for (TraceKingItemInfo traceKingItemInfo : merchantItems) {
            int buy = new Random().nextInt(50, 100);
            traceKingItemInfo.setSellPrices(buy);
        }
    }

    public static List<TraceKingItemInfo> getTradeKillItems(int npcId) {
        if (npcId == 9001088) {
            return buyAll;
        } else if (npcId == 9001087) {
            return sellAll;
        }

        List<TraceKingItemInfo> traceKingItemInfos = merchantItemsBuy.get(npcId);
        List<TraceKingItemInfo> traceKingItemInfos1 = merchantItemsSell.get(npcId);
        ArrayList<TraceKingItemInfo> objects = new ArrayList<>();
        objects.addAll(traceKingItemInfos);
        objects.addAll(traceKingItemInfos1);
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
        TraceKingUserInfo userInfo = userInfoCache.get(chrId);

        // 当前绑定的NPC
        int shopNpcId = userInfo.getShopNpc();

        // 找到shop卖的东西
        List<TraceKingItemInfo> traceKingItemInfos = getTradeKillItems(shopNpcId);


        // 找到商品
        TraceKingItemInfo traceKingItemInfo = traceKingItemInfos.get(itemIndex);
        int itemId = traceKingItemInfo.getId();

        // 找到对应的qr
        int qr = traceKingItemInfo.getQr();


        // 处理自己的逻辑
        if (buy) {
            // 扣掉金币
            int buyPrices = traceKingItemInfo.getBuyPrices() * count;
            userInfo.setCount(userInfo.getCount() - buyPrices);

            // 171  传扣钱
            sendUserQR(c.getChr(), userInfo);


            // 用户增加商品
            Map<String, Integer> itemNum = userInfo.getItemNum();
            String key = traceKingItemInfo.getQr() + "_" + traceKingItemInfo.getQrEx();
            Integer i = itemNum.get(key);
            //  itemNum.merge(key, count, Integer::sum);
            if (i == null) {
                itemNum.put(key, count);
            } else {
                itemNum.put(key, i + count);
            }


            // 171  刷新对应qrvalue
            String flushQRValue = flushUserItemQRValue(userInfo, traceKingItemInfo);
            c.getChr().write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, qr, flushQRValue, (byte) 0));


            // 增加重量
            userInfo.setcWeight(userInfo.getcWeight() + count);
            // 171  传正确的
            sendUserQR(c.getChr(), userInfo);


        } else {

            // 增加金币
            int buyPrices = traceKingItemInfo.getSellPrices() * count;
            userInfo.setCount(userInfo.getCount() + buyPrices);

            // 171  传扣钱
            sendUserQR(c.getChr(), userInfo);


            // 用户增加商品
            Map<String, Integer> itemNum = userInfo.getItemNum();
            String key = traceKingItemInfo.getQr() + "_" + traceKingItemInfo.getQrEx();
            Integer i = itemNum.get(key);
            //  itemNum.merge(key, count, Integer::sum);
            if (i == null) {
                itemNum.put(key, count);
            } else {
                itemNum.put(key, Math.max(i - count, 0));
            }


            // 171  刷新对应qrvalue
            String flushQRValue = flushUserItemQRValue(userInfo, traceKingItemInfo);
            c.getChr().write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, qr, flushQRValue, (byte) 0));


            // 减少重量
            userInfo.setcWeight(userInfo.getcWeight() - count);
            // 171  传正确的
            sendUserQR(c.getChr(), userInfo);


        }


        // 171  传结果
        // sendUserQR(c.getChr(), userInfo);


        //00 00 00 00    // success
        //01             // 买入 卖出
        //F4 90 3D 00   // 购买物品 0348004
        //0B 00 00 00   // 上次购买价格
        OutPacket outpacket = new OutPacket(OutHeader.TRADE_KING_SHOP_RES);
        outpacket.encodeInt(0);
        outpacket.encodeByte(buySellTag);
        outpacket.encodeInt(itemId);
        outpacket.encodeInt(traceKingItemInfo.getBuyPrices());
        c.getChr().write(outpacket);
    }

    /**
     * 用户购买的个数
     * bugfix: 刷新要带上当前exValue所有的值，而不是只有当前物品
     *
     * @param userInfo
     * @param traceKingItemInfo
     * @return
     */
    private static String flushUserItemQRValue(TraceKingUserInfo userInfo, TraceKingItemInfo traceKingItemInfo) {
        int qr = traceKingItemInfo.getQr(); // 15322

        Map<String, Integer> itemNum = userInfo.getItemNum();



        // 标准串 "5=0;4=0;7=0;6=0;1=0;0=0;3=0;2=0"
        String standardQrValue = shopMessageQr.get(qr);
//        int qrEx = traceKingItemInfo.getQrEx();
//        String userInfoKey = qr + "_" + qrEx;
//        Integer traceKillItemCount = userInfo.getItemNum().get(userInfoKey);


        // 切分 key=value 项
        String[] parts = standardQrValue.split(";");

        // 替换对应 key 的 value
        for (int i = 0; i < parts.length; i++) {
            String[] kv = parts[i].split("=");

            String userInfoKey = qr + "_" + kv[0];
            Integer userHaveNum = itemNum.get(userInfoKey);
            if (userHaveNum == null) {
                continue;
            } else {
                parts[i] = kv[0] + "=" + userHaveNum;

            }
//            if (kv.length == 2 && kv[0].equals(String.valueOf(qrEx))) {
//                parts[i] = qrEx + "=" + traceKillItemCount;
//            }
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

        updatePrice();
        clickTradeKingNPC(c.getChr(), 0);


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


    // 初始化任务信息，否则下线无法查看之前的状态
    private static void sendUserQRInit(Char chr, TraceKingUserInfo userInfo) {
        String format = String.format("shop=%d;cWeight=%d;count=%d;mWeight=%d", userInfo.getShopNpc(), userInfo.getcWeight(), userInfo.getCount(), userInfo.getmWeight());
        // 满背包125    15324 shop=-1;cWeight=0;count=50;mWeight=125
        sendQRValue(chr, 15324, format);
    }

    private static void sendUserQR(Char chr, TraceKingUserInfo userInfo) {
        String format = String.format("shop=%d;cWeight=%d;count=%d;mWeight=%d", userInfo.getShopNpc(), userInfo.getcWeight(), userInfo.getCount(), userInfo.getmWeight());
        // 满背包125    15324 shop=-1;cWeight=0;count=50;mWeight=125
//        TraceKillHandler.sendQRValue(chr, 15324, format);

        chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, 15324, format, (byte) 0));
        chr.chatMessage(String.format("Sent QRValue with  QuestId %d, QrValue %s", 15324, format));
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

    // NPC点击
    public static void clickTradeKingNPC(Char chr, int npcId) {
        Integer chrId = chr.getId();
        TraceKingUserInfo userInfo = userInfoCache.get(chrId);
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
        List<TraceKingItemInfo> traceKingItemInfos = getTradeKillItems(npcId);


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
        outpacket.encodeInt(traceKingItemInfos.size());
        for (TraceKingItemInfo traceKingItemInfo : traceKingItemInfos) {
            //id
            outpacket.encodeInt(traceKingItemInfo.getId());
            // buy
            outpacket.encodeInt(traceKingItemInfo.getBuyPrices());
            // sell
            outpacket.encodeInt(traceKingItemInfo.getSellPrices());
        }
        chr.write(outpacket);
    }


    public static void getTradeKingEnd(Char chr) {
        Integer chrId = chr.getId();
        TraceKingUserInfo userInfo = userInfoMapper.selectByCharId(chrId);
        chr.getTemporaryStatManager().removeStatsBySkill(userInfo.getRide());

    }

    public static TraceKingUserInfo getTradeKingInfo(Char chr) {
        Integer chrId = chr.getId();
//        TraceKingUserInfo userInfo = userInfoMapper.selectByCharId(chrId);
//        return userInfo;
        TraceKingUserInfo traceKingUserInfo = userInfoCache.get(chrId);
        if (traceKingUserInfo != null) {
            return traceKingUserInfo;
        }
        TraceKingUserInfo userInfo = userInfoMapper.selectByCharId(chrId);
        if (userInfo != null) {
            userInfoCache.put(chrId, userInfo);
            return userInfo;
        }
        userInfo = initTradeKingInfo(chrId);
        userInfoCache.put(chrId, userInfo);
        return userInfo;
    }


    public static void getTradeKingInit(Char chr) {
        Integer chrId = chr.getId();

        // 查询数据库是否存在

        TraceKingUserInfo userInfo = userInfoMapper.selectByCharId(chrId);
        // 初始化一个
        if (userInfo == null) {
            userInfo = initTradeKingInfo(chrId);

        } else {
            // 设置一个初始货币
            userInfo.setCount(initCount);

            // 计算承重
            int mWeight = calculatorMaxWeight(userInfo);
            userInfo.setmWeight(mWeight);
            userInfo.setcWeight(0);

        }


//        sendUserQR(chr, userInfo);
        sendUserQRInit(chr, userInfo);

        // UI显示满 4和尚+ 1狐狸  DD 3B 1=4;4=1
//        TraceKingHandler.sendQRValue(chr, 15325, "1=4;4=1");
        TraceKingHandler.sendQRValue(chr, TraceKingQuestRxCode.WORKER.getVal(), userInfo.getWorker());
//        TraceKillHandler.sendQRValue(chr, 15325, "0");

        // todo 发送45次shop的message？
        shopMessageQr.forEach((key, value) -> {
            // 每个发一次就够了 客户端会拿到的
            chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE, key, value, (byte) 0));
        });
        sendExpiredTime(chr);


        // 上坐骑
        int slv = 1;
        InPacket inPacket = null;
        SkillUseInfo skillUseInfo = null;
        Job sourceJobHandler = chr.getJobHandler();

        // 判断有没有这个技能，没有需要给user提供一个
        boolean b = chr.hasSkill(userInfo.getRide());
        if (!b) {
            chr.addSkill(userInfo.getRide(), slv, slv);
        }
        sourceJobHandler.handleSkill(chr, chr.getTemporaryStatManager(), userInfo.getRide(), slv, inPacket, skillUseInfo);


        // 刚开始可以存一次
        updateUserInfo(chrId, userInfo);

    }

    private static TraceKingUserInfo initTradeKingInfo(int chrId) {
        TraceKingUserInfo userInfo = new TraceKingUserInfo();
        userInfo.setChrid(chrId);
        userInfo.setShopNpc(-1);
        userInfo.setcWeight(0);
        userInfo.setCount(initCount);
        userInfo.setmWeight(10);
        userInfo.setScount(0);
        userInfo.setWorker(initWorker);
        userInfo.setRide(initRidSkill);
        int insert = userInfoMapper.insert(userInfo);
        // 查询完整数据，包括 create_time / update_time
        userInfo = userInfoMapper.selectById(userInfo.getId());
        return userInfo;
    }

    /**
     * 同步更新数据库和缓存
     *
     * @param chrId
     * @param userInfo
     */
    private static void updateUserInfo(Integer chrId, TraceKingUserInfo userInfo) {
        // 更新数据库
        userInfoMapper.updateByCharId(userInfo);
        userInfoCache.put(chrId, userInfo);
    }

    /**
     * @param userInfo
     * @return
     */
    private static int calculatorMaxWeight(TraceKingUserInfo userInfo) {
        Integer ride = userInfo.getRide();
        int mWeight = 0;
        if (ride == 80001950) {
            mWeight += 10;
        } else if (ride == 80001951) {
            mWeight += 20;
        } else {
            mWeight += 30;
        }
        String worker = userInfo.getWorker();

        String[] split = worker.split(";");
        // 0 10
        // 1 20
        // 2 10
        // 3 15
        // 4 25
        for (String s : split) {
            String[] kv = s.split("=");
            switch (kv[0]) {
                case "0": {
                    int i = Integer.parseInt(kv[1]) * 10;
                    mWeight += i;
                    break;
                }
                case "1": {
                    int i = Integer.parseInt(kv[1]) * 20;
                    mWeight += i;
                    break;
                }
                case "2": {
                    int i = Integer.parseInt(kv[1]) * 10;
                    mWeight += i;
                    break;
                }
                case "3": {
                    int i = Integer.parseInt(kv[1]) * 15;
                    mWeight += i;
                    break;
                }
                case "4": {
                    int i = Integer.parseInt(kv[1]) * 25;
                    mWeight += i;
                    break;
                }
            }

        }

        // 坐骑 + worker
        return mWeight;

    }


    public static void saveTradeKing(Char chr, int questRxCode, String param) {
        Integer chrId = chr.getId();
        TraceKingUserInfo userInfo = getTradeKingInfo(chr);

        TraceKingQuestRxCode handleCode = null;
        for (TraceKingQuestRxCode value : TraceKingQuestRxCode.values()) {
            if (questRxCode == value.getVal()) {
                handleCode = value;
                break;
            }
        }

        switch (handleCode) {
            case GOLD: {
                // 切分 key=value 项
                String[] parts = param.split(";");

                // 替换对应 key 的 value
                for (int i = 0; i < parts.length; i++) {
                    String[] kv = parts[i].split("=");
                    if (kv.length == 2 && kv[0].equals("count")) {
                        int count = Integer.parseInt(kv[1]);
                        userInfo.setCount(count);

                    }
                    if (kv.length == 2 && kv[0].equals("scount")) {
                        int scount = Integer.parseInt(kv[1]) - initCount;
                        userInfo.setScount(scount);

                    }
                }

                break;
            }


            case RIDING: {
                // 切分 key=value 项
                String[] parts = param.split(";");

                // 替换对应 key 的 value
                for (int i = 0; i < parts.length; i++) {
                    String[] kv = parts[i].split("=");
                    if (kv.length == 2 && kv[0].equals("ride")) {
                        int ride = Integer.parseInt(kv[1]);
                        userInfo.setRide(ride);

                    }
                    if (kv.length == 2 && kv[0].equals("scount")) {
                        int scount = Integer.parseInt(kv[1]);
                        int newScount = userInfo.getScount() - scount;
                        userInfo.setScount(newScount);

                    }
                }
                break;
            }
            case WORKER:
                String[] parts = param.split(";");
                Map<String, String> workerMap = new HashMap<>();
                for (int i = 0; i < parts.length; i++) {
                    String[] kv = parts[i].split("=");
                    workerMap.put(kv[0], kv[1]);
                }
                int scount = Integer.parseInt(workerMap.get("scount"));
                // 招聘
                if (scount > 0) {
                    int newScount = userInfo.getScount() - scount;
                    userInfo.setScount(newScount);

                    int workIndex = Integer.parseInt(workerMap.get("worker"));
                    String worker = userInfo.getWorker();
                    String work = null;
                    // 新增
                    if (initWorker.equals(worker)) {
                        work = workIndex + "=" + 1;
                    } else {
                        // 更新
                        boolean update = false;
                        String[] workerParts = worker.split(";");
                        for (int i1 = 0; i1 < workerParts.length; i1++) {
                            String[] workerKV = workerParts[i1].split("=");
                            if (workerKV.length == 2 && workerKV[0].equals(String.valueOf(workIndex))) {
                                workerParts[i1] = String.valueOf(workIndex) + "=" + (Integer.parseInt(workerKV[1]) + 1);
                                update = true;
                            }
                        }
                        // 拼回字符串
                        String updated = String.join(";", workerParts);

                        // 如果新增的是之前没有的
                        if (!update) {
                            work = updated + ";" + workIndex + "=1";
                        } else {
                            // 旧的有新增
                            work = updated;
                        }

                    }
                    userInfo.setWorker(work);


                } else {
                    // 解雇
                    String worker = userInfo.getWorker();
                    int workIndex = Integer.parseInt(workerMap.get("worker"));

                    String[] workerParts = worker.split(";");
                    for (int i1 = 0; i1 < workerParts.length; i1++) {
                        String[] workerKV = workerParts[i1].split("=");
                        if (workerKV.length == 2 && workerKV[0].equals(String.valueOf(workIndex))) {
                            workerParts[i1] = String.valueOf(workIndex) + "=" + (Integer.parseInt(workerKV[1]) - 1);
                        }
                    }
                    String updated = String.join(";", workerParts);
                    userInfo.setWorker(updated);
                }

                // 发送worker更新
                TraceKingHandler.sendQRValue(chr, TraceKingQuestRxCode.WORKER.getVal(), userInfo.getWorker());
                // 计算承重
                int mWeight = calculatorMaxWeight(userInfo);
                userInfo.setmWeight(mWeight);

                // 替换对应 key 的 value
/*                for (int i = 0; i < parts.length; i++) {
                    String[] kv = parts[i].split("=");
                    if (kv.length == 2 && kv[0].equals("worker")) {
                        int workIndex = Integer.parseInt(kv[1]);
                        String worker = userInfo.getWorker();
                        String work = null;
                        if (initWorker.equals(worker)) {
                            work = workIndex + "=" + 1;
                        } else {
                            boolean update = false;
                            String[] workerParts = worker.split(";");
                            for (int i1 = 0; i1 < workerParts.length; i1++) {
                                String[] workerKV = workerParts[i1].split("=");
                                if (workerKV.length == 2 && workerKV[0].equals(String.valueOf(workIndex))) {
                                    workerParts[i1] = String.valueOf(workIndex) + "=" + (Integer.parseInt(workerKV[1]) + 1);
                                    update = true;
                                }
                            }
                            // 拼回字符串
                            String updated = String.join(";", workerParts);

                            // 如果是新的
                            if (!update) {
                                work = updated + ";" + workIndex + "=1";
                            } else {
                                // 旧的有新增
                                work = updated;
                            }

                        }
                        userInfo.setWorker(work);

                    }
                    if (kv.length == 2 && kv[0].equals("scount")) {
                        int scount = Integer.parseInt(kv[1]);
                        int newScount = userInfo.getScount() - scount;
                        userInfo.setScount(newScount);

                    }
                }*/
                break;

        }

        // 更新数据库和缓存
        updateUserInfo(chrId, userInfo);


        // 发送
        sendUserQR(chr, userInfo);
    }


    public static int getLastTradeKingMapObjInfo(Char chr) {
        TraceKingUserInfo userInfo = getTradeKingInfo(chr);
        return userInfo.getLastQuestExCode();
    }

    public static void saveLastTradeKingMapObjInfo(Char chr, int questRxCode) {
        TraceKingUserInfo userInfo = getTradeKingInfo(chr);
        userInfo.setLastQuestExCode(questRxCode);
    }
}
