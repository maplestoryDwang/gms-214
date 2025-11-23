# sm.getTradeKingInit()
if sm.getFieldID() == 993000837 or sm.getFieldID() == 993000801 :
    if sm.sendAskYesNo("准备好开始了吗？"):
        sm.getTradeKingInit()
        sm.warpInstanceIn(993000801, False)
        sm.setInstanceTime(60*60)
elif sm.sendAskYesNo("你想结束你的旅途吗？\r\n#b"):
    sm.warp(993000800)