say = "我将会给你一些" + "#i" + str(4310194) + "#  "
if sm.sendAskYesNo(say):
    sm.giveItem(4310194, 100)
    sm.dispose()