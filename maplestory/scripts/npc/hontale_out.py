# Crystal of Roots(2083002) | Cave of Life, Entrance to Horntail's Cave
if sm.getFieldID() == 240050400:
    sm.warp(240040700, 0)
elif sm.sendAskYesNo("你想要出去吗"):
    sm.warpInstanceOut(240050400, 2)
