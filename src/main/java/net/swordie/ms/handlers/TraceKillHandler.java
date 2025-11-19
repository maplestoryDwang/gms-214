package net.swordie.ms.handlers;

import lombok.extern.slf4j.Slf4j;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.quest.Quest;
import net.swordie.ms.client.character.quest.QuestManager;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.connection.packet.WvsContext;
import net.swordie.ms.enums.MessageType;
import net.swordie.ms.enums.QuestStatus;
import net.swordie.ms.handlers.header.InHeader;
import net.swordie.ms.handlers.header.OutHeader;
import net.swordie.ms.tracekill.TraceKillItemInfo;
import net.swordie.ms.tracekill.TraceKillUserInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static HashMap<Integer, List<TraceKillItemInfo>> npcItemInfo = new HashMap<>();

    private static HashMap<Integer, TraceKillUserInfo> userInfoMap = new HashMap<>();


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


        // 石铁
        int npcId = 9001072;
        List<TraceKillItemInfo> traceKillItemInfos = new ArrayList<>();
        traceKillItemInfos.add(new TraceKillItemInfo(4034804, 10,50, 15322, 0)); // 不卖写0
        traceKillItemInfos.add(new TraceKillItemInfo(4034819, 30,0, 15323, 7)); // 不卖写0
        traceKillItemInfos.add(new TraceKillItemInfo(4034814, 0,60, 15323,2)); // 不卖写0
        traceKillItemInfos.add(new TraceKillItemInfo(4034815, 0,60, 15323,3)); // 不卖写0
        traceKillItemInfos.add(new TraceKillItemInfo(4034829, 0,60, 15345,1)); // 不卖写0
        npcItemInfo.put(npcId, traceKillItemInfos);

    }



    public static List<TraceKillItemInfo> getTradeKillItems(int npcId) {
        return npcItemInfo.get(npcId);
    }


    /**
     * 购买
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
        List<TraceKillItemInfo> traceKillItemInfos = npcItemInfo.get(shopNpcId);
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
            c.getChr().write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE,
                    qr, flushQRValue, (byte) 0));


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
                itemNum.put(key, Math.max(i - count,0));
            }


            // 171  刷新对应qrvalue
            String flushQRValue = flushQRValue(userInfo, traceKillItemInfo);
            c.getChr().write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE,
                    qr, flushQRValue, (byte) 0));



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
     * @param c
     * @param inPacket
     */
    @Handler(op = InHeader.TRADE_KING_SHOP_INFO_REQ)
    public static void handleTradeKingSHopINfoReq(Client c, InPacket inPacket) {
        // 客户端发送的0内容

        sendExpiredTime(c.getChr());


        clickTradeKingNPC(c.getChr(), 0);
    }


    /**
     * 初始化
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
        chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE,
                q.getQRKey(), q.getQRValue(), (byte) 0));
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
        List<TraceKillItemInfo> traceKillItemInfos = npcItemInfo.get(npcId);


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

        chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE,
                15324,format, (byte) 0));
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
        chr.write(WvsContext.message(MessageType.QUEST_RECORD_EX_MESSAGE,
                15317, s, (byte) 0));
        return s;
    }
}
