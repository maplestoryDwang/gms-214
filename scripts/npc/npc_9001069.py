# sm.getTradeKingInit()
if sm.getFieldID() == 993000837:
    sm.sendSay("旅途结束了, 送你到入口")
    sm.warp(993000801)
elif sm.getFieldID() == 993000801:
    sm.sendSay("旅途开始！")
    sm.getTradeKingInit()