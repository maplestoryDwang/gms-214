from net.swordie.ms.constants import GameConstants
from net.swordie.ms.constants import ItemConstants

req_shards = GameConstants.NODE_STONE_CRAFT_REQ
current_shards = chr.getNodeShards()
ARCHELLE = 1540945

sm.setSpeakerID(ARCHELLE)
canCraftNum = current_shards / req_shards

quantity = sm.sendAskNumber("你想制作核心宝石?#b\r\n\r\n制作宝石需要的核心碎片数: " + str(req_shards)
                    + "\r\n你的核心碎片数: " + str(current_shards) + "\r\n\r\n#k你想制作多少个? 当前你可以制作：" + str(canCraftNum), canCraftNum, 0, canCraftNum)
req_shards *= quantity
if quantity > 0:
    if req_shards > current_shards:
        sm.sendSayOkay("看起来你没有足够的碎片. 我需要 #b" + str(req_shards) + "#k 去制作核心宝石 "
                        "你当前的核心碎片数量： #b" + str(current_shards) + "#k.")
    elif not sm.canHold(ItemConstants.NODESTONE, quantity):
        sm.sendSayOkay("请检查你背包消耗栏是否满.")
    else:
        chr.addNodeShards(-req_shards)
        sm.sendSayOkay("你使用了#b " + str(req_shards) + "#k个核心碎片，成功制作 #b" + str(quantity) + " #k个核心宝石.")
        sm.giveItem(ItemConstants.NODESTONE, quantity)
else:
    sm.sendSayOkay("你TM是来搞笑的？")
