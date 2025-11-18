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

    // 每个商店卖的东西
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
        traceKillItemInfos.add(new TraceKillItemInfo(4034819, 1,10, 15323)); // 不卖写0
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


        int shopNpcId = userInfo.getShopNpc();

        // 找到shop卖的东西
        List<TraceKillItemInfo> traceKillItemInfos = npcItemInfo.get(shopNpcId);
        // 找到商品
        TraceKillItemInfo traceKillItemInfo = traceKillItemInfos.get(itemIndex);
        int itemId = traceKillItemInfo.getId();

        // 找到对应的qr
        int qr = traceKillItemInfo.getQr();
        String qrValue = shopMessageQr.get(qr);


        // 处理自己的逻辑
        if (buy) {
            // 增加重量
            userInfo.setcWeight(userInfo.getcWeight() + count);

            // 扣掉金币
            int buyPrices = traceKillItemInfo.getBuyPrices() * count;
            userInfo.setCount(userInfo.getCount() - buyPrices);


        } else {
            // 减少重量
            userInfo.setcWeight(userInfo.getcWeight() - count);

            // 增加金币
            int buyPrices = traceKillItemInfo.getSellPrices() * count;
            userInfo.setCount(userInfo.getCount() + buyPrices);

        }


        // 171  刷新自身信息
        sendUserQR(c.getChr(), userInfo);


        // 171  刷新对应qrvalue
        TraceKillHandler.sendQRValue(c.getChr(), qr, qrValue);


        //00 00 00 00    // 固定？
        //01             // 买入 卖出
        //F4 90 3D 00   // 购买物品 0348004
        //0B 00 00 00   //购买价格
        OutPacket outpacket = new OutPacket(OutHeader.TRADE_KING_SHOP_RES);
        outpacket.encodeInt(0);
        outpacket.encodeByte(buySellTag);
        outpacket.encodeInt(itemId);
        outpacket.encodeInt(traceKillItemInfo.getBuyPrices());

    }

    /**
     * 到时间刷新了：获取信息
     * @param c
     * @param inPacket
     */
    @Handler(op = InHeader.TRADE_KING_SHOP_INFO_REQ)
    public static void handleTradeKingSHopINfoReq(Client c, InPacket inPacket) {
        // 客户端发送的0内容

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
        userInfo.setCount(50);
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
//        sendUserQR(chr, userInfo);



        // 找到shop卖的东西
        List<TraceKillItemInfo> traceKillItemInfos = npcItemInfo.get(npcId);


        OutPacket outpacket = new OutPacket(OutHeader.TRADE_KING_SHOP_ITEM);
        outpacket.encodeInt(npcId);
        // 应该是发自己的状态
        outpacket.encodeInt(15324);  // quest_id
        String format = String.format("shop=%d;cWeight=%d;count=%d;mWeight=%d", userInfo.getShopNpc(), userInfo.getcWeight(), userInfo.getCount(), userInfo.getmWeight());
        outpacket.encodeString(format);


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

        // UI显示满 4和尚+ 1狐狸  DD 3B 1=4;4=1
        TraceKillHandler.sendQRValue(chr, 15325, "1=4;4=1");

        // todo 发送45次shop的message？







    }

    private static void sendUserQR(Char chr, TraceKillUserInfo userInfo) {
        String format = String.format("shop=%d;cWeight=%d;count=%d;mWeight=%d", userInfo.getShopNpc(), userInfo.getcWeight(), userInfo.getCount(), userInfo.getmWeight());
        // 满背包125    15324 shop=-1;cWeight=0;count=50;mWeight=125
        TraceKillHandler.sendQRValue(chr, 15324, format);
    }
}
