if sm.getFieldID() == 993000837:
    sm.sendSay("玩的开心吗欢迎你下次再来")
    sm.warp(993000801)
if sm.getFieldID() == 993000801:
    if sm.sendAskYesNo("你想结束你的旅途吗？\r\n#b"):
        sm.warp(993000800)
else:
    sm.sendSay("点击休彼得曼开始你的旅途")

