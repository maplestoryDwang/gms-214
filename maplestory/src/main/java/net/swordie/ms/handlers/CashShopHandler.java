package net.swordie.ms.handlers;

import jnr.ffi.annotations.In;
import lombok.extern.slf4j.Slf4j;
import net.swordie.ms.Server;
import net.swordie.ms.client.Account;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.User;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.items.Equip;
import net.swordie.ms.client.character.items.Inventory;
import net.swordie.ms.client.character.items.Item;
import net.swordie.ms.client.character.quest.Quest;
import net.swordie.ms.client.character.quest.QuestManager;
import net.swordie.ms.client.trunk.Trunk;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.db.DatabaseManager;
import net.swordie.ms.connection.packet.CCashShop;
import net.swordie.ms.connection.packet.CUIHandler;
import net.swordie.ms.connection.packet.WvsContext;
import net.swordie.ms.constants.ItemConstants;
import net.swordie.ms.constants.QuestConstants;
import net.swordie.ms.enums.CashItemType;
import net.swordie.ms.enums.CashShopActionType;
import net.swordie.ms.enums.InvType;
import net.swordie.ms.enums.QuestStatus;
import net.swordie.ms.handlers.header.InHeader;
import net.swordie.ms.handlers.item.VioletCubeQuestBean;
import net.swordie.ms.handlers.item.VioletCubeQuestParser;
import net.swordie.ms.loaders.ItemData;
import net.swordie.ms.loaders.QuestData;
import net.swordie.ms.util.FileTime;
import net.swordie.ms.world.shop.cashshop.CashItemInfo;
import net.swordie.ms.world.shop.cashshop.CashShop;
import net.swordie.ms.world.shop.cashshop.CashShopItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.swordie.ms.constants.ItemConstants.getRandomRareSSB;
import static net.swordie.ms.constants.ItemConstants.getRandomSSB;
import static net.swordie.ms.enums.InvType.EQUIP;
import static net.swordie.ms.enums.InvType.EQUIPPED;

/**
 * Created on 4/23/2018.
 */
@Slf4j
public class CashShopHandler {

    @Handler(op = InHeader.CASH_SHOP_QUERY_CASH_REQUEST)
    public static void handleCashShopQueryCashRequest(Client c, InPacket inPacket) {
        c.write(CCashShop.queryCashResult(c.getChr()));
    }
    @Handler(op = InHeader.CASH_SHOP_CASH_ITEM_REQUEST)
    public static void handleCashShopCashItemRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        User user = chr.getUser();
        Account account = chr.getAccount();
        Trunk trunk = account.getTrunk();
        byte type = inPacket.decodeByte();
        CashItemType cit = CashItemType.getRequestTypeByVal(type);
        CashShop cs = Server.getInstance().getCashShop();
        if (cit == null) {
            log.error("Unhandled cash shop cash item request " + type);
//            c.write(CCashShop.error());
            return;
        }
        log.debug("Received CASH_SHOP_CASH_ITEM_REQUEST: {}, packet:{}",cit,  inPacket);
        switch (cit) {
            case Req_Buy:
                inPacket.decodeByte();
                byte paymentMethod=inPacket.decodeByte();
                log.debug("Payment method: "+paymentMethod);
//                inPacket.decodeByte();
                inPacket.decodeByte();
                inPacket.decodeByte();
                inPacket.decodeByte();
                inPacket.decodeByte();
                inPacket.decodeByte();
//                byte paymentMethod = inPacket.decodeByte();
//                int idk2 = inPacket.decodeInt();
//                int itemPos = inPacket.decodeInt();
//                int cost = inPacket.decodeInt();
                int SN=inPacket.decodeInt();
                CashShopItem csi=cs.getItemBySN(SN);
//                paymentMethod=1;
//                int paymentMethod=1;
                int cost=csi.getNewPrice();
//                CashShopItem csi = cs.getItemByPosition(itemPos - 1); // client's pos starts at 1
//                if (csi == null || csi.getNewPrice() != cost) {
//                    c.write(CCashShop.error());
//                    log.error("Requested item's cost did not match client's cost");
//                    return;
//                }
                boolean notEnoughMoney = false;
                switch (paymentMethod) {
                    case 1: // Credit
                        if (account.getNxCredit() >= cost) {
                            account.deductNXCredit(cost);
                        } else {
                            notEnoughMoney = true;
                        }
                        break;
                    case 2: // Maple points
                        if (user.getMaplePoints() >= cost) {
                            user.deductMaplePoints(cost);
                        } else {
                            notEnoughMoney = true;
                        }
                        break;
                    case 32: // Prepaid
                        if (user.getNxPrepaid() >= cost) {
                            user.deductNXPrepaid(cost);
                        } else {
                            notEnoughMoney = true;
                        }
                        break;
                }
                if (notEnoughMoney) {
                    c.write(CCashShop.error());
                    log.error("Character does not have enough to pay for this item (Paying with " + paymentMethod + ")");
                    return;
                }
                CashItemInfo cii = csi.toCashItemInfo(account);

                DatabaseManager.saveToDB(cii); // ensures the item has a unique ID
                trunk.addCashItem(cii);

                // 下面这个会直接放到背包里面
//                chr.addItemToInventory(cii.getItem());
//                account.addNXCredit(-cost);


                c.write(CCashShop.cashItemResBuyDone(cii, null, null, 0));
                c.write(CCashShop.queryCashResult(chr));
                break;
            case Req_MoveLtoS:
                long itemSn = inPacket.decodeLong();
                cii = trunk.getLockerItemBySn(itemSn);
                if (cii == null) {
                    c.write(CCashShop.fullInventoryMsg());
                    return;
                }
                Item item = cii.getItem();
                Inventory inventory;
                if (ItemConstants.isEquip(item.getItemId())) {
                    inventory = chr.getEquipInventory();
                } else {
                    inventory = chr.getCashInventory();
                }
                if (!inventory.canPickUp(item)) {
                    c.write(CCashShop.fullInventoryMsg());
                    return;
                }
                trunk.getLocker().remove(cii);
                chr.addItemToInventory(item);
                c.write(CCashShop.resMoveLtoSDone(item));
                c.write(CCashShop.queryCashResult(chr));
                break;
            case Req_MoveStoL:
                itemSn = inPacket.decodeLong();
                item = chr.getItemBySn(itemSn);
                if (item == null || trunk.isFull()) {
                    c.write(CCashShop.fullInventoryMsg());
                    return;
                }
                int quant = item.getQuantity();
                cii = CashItemInfo.fromItem(chr, item);
                c.write(CCashShop.resMoveStoLDone(cii, null));
                chr.consumeItem(item);
                item.setQuantity(quant);
                DatabaseManager.saveToDB(cii);
                trunk.addCashItem(cii);
                c.write(CCashShop.queryCashResult(chr));
                break;
            default:
//                c.write(CCashShop.error());
                log.error("Unhandled cash shop cash item request {}, {}" , cit.getVal(), cit);
                chr.dispose();
                break;
        }
    }


    @Handler(op = InHeader.CASH_SHOP_ACTION)
    public static void handleCashShopAction(Char chr, InPacket inPacket) {
        CashShop cashShop = Server.getInstance().getCashShop();
        byte type = inPacket.decodeByte();
        CashShopActionType csat = CashShopActionType.getByVal(type);
        if (csat == null) {
            log.error("Unhandled cash shop cash action request " + type);
            chr.write(CCashShop.error());
            return;
        }
        switch (csat) {
            case Req_OpenCategory:
                int categoryIdx = inPacket.decodeInt();
                chr.write(CCashShop.openCategoryResult(cashShop, categoryIdx));
                break;
            case Req_Favorite:
            case Req_Leave:
                break;

            default:
                chr.write(CCashShop.error());
                log.error("Unhandled cash shop cash action request " + csat);
                chr.dispose();
                break;
        }
    }

    @Handler(op = InHeader.SURPRISE_BOX)
    public static void handleSurprise(Client c, InPacket inPacket){
        Char chr = c.getChr();
        Account account = chr.getAccount();

        long sn = inPacket.decodeLong();
        int getRare = (int)(Math.random()*((100-1)+1))+1;
        boolean isRare;
        Trunk trunk = account.getTrunk();
        CashItemInfo box = trunk.getLockerItemBySn(sn); //Grabs by SN now so we can add handling for other boxes in the future
        Equip equip;
        if (getRare <= 5) { // Lmao get fucked
            equip = ItemData.getEquipDeepCopyFromID(getRandomRareSSB(), false);
            isRare = true;
        } else {
            equip = ItemData.getEquipDeepCopyFromID(getRandomSSB(), false);
            isRare = false;
        }
        CashItemInfo cii = CashItemInfo.fromItem(c.getChr(), equip);

        if (cii == null || trunk.isFull()) {
            c.write(CCashShop.fullInventoryMsg());
            return;
        }

        DatabaseManager.saveToDB(cii); // ensures the item has a unique ID
        trunk.addCashItem(cii);
        c.write(CCashShop.queryCashResult(chr));
        if (box != null) {
            Item item = box.getItem();
            chr.consumeItem(item, 1); // Lets try this? no need to remove the cii if we consume it here ya?
            // trunk.getLocker().remove(cii);
            if (item.getQuantity() <= 0) {
                trunk.getLocker().remove(box);
            }
        }
        c.write(CCashShop.surpriseBox(cii, box, isRare));
        c.write(CCashShop.loadLockerDone(chr.getAccount())); //Makes it not invisible
        //ToDo make this not look like shit

    }

    /**
     * 六角修复
     * @param c
     * @param inPacket
     */
    @Handler(op = InHeader.VIOLET_CUBE_REQUEST)
    public static void violetCubeRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        int questID = QuestConstants.VIOLET_CUBE_INFO;
        // 6D CF C7 11
        // 03 00 00 00
        // 6B 9C 00 00 5E 75 00 00 5E 75 00 00

        inPacket.decodeInt();
        int size = inPacket.decodeInt();

        // 拿到option
        List<Integer> selectOptions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            selectOptions.add(inPacket.decodeInt());
        }
        QuestManager questManager = chr.getQuestManager();
        Quest questById = questManager.getQuestById(questID);
        String qrValue = questById.getQRValue();
        VioletCubeQuestBean violetCubeQuestBean = VioletCubeQuestParser.parseQuestData(qrValue);
        int ePos = violetCubeQuestBean.getEpos();

        InvType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemBySlot(ePos);
        List<Integer> options = equip.getOptions();

        for (int i = 0; i < selectOptions.size(); i++) {
            options.set(i, selectOptions.get(i));
        }
        // 设置option
//        equip.setOptions(selectOptions);

        // 更新装备
        Inventory equipInv = chr.getEquipInventory();
        Equip invEquip = (Equip) equipInv.getItemBySlot(equip.getBagIndex());
        equipInv.removeItem(invEquip);
        equipInv.addItem(equip);
        equip.updateToChar(chr);




        // 取消任务
        chr.getQuestManager().removeQuest(questID);



//        //UI显示
//        c.write(CUIHandler.violetCubeResult(0, 1, 0, Collections.emptyList()));

    }



    public static void handleCashShopButtonPress(Char chr, InPacket inPacket) {
        chr.dispose();
    }
}