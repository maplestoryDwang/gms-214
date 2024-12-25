if sm.getFieldID() == 993073000:
    sm.sendSayOkay("No")
else:
    sm.setReturnField(chr.getFieldID())
    options = ["你需要我的帮助!"]

    options2 = ["城镇地图","自由市场","Boss入口","跳跳地图"]

    maps = [
    [
        300000000, 680000000, 230000000, 910001000, 260000000, 541000000, 540000000, 211060010,
        105300000, 310000000, 211000000, 101072000, 101000000, 101050000, 130000000, 820000000, 223000000, 410000000,
        141000000, 120040000, 209000000, 310070000, 401000000, 100000000, 271010000, 251000000, 744000000, 551000000,
        103000000, 224000000, 241000000, 240000000, 104000000, 220000000, 150000000, 261000000, 701220000, 807000000,
        701210000, 250000000, 800000000, 600000000, 120000000, 200000000, 800040000, 400000000, 102000000, 914040000,
        865000000, 801000000, 105000000, 866190000, 270000000, 273000000, 701100000, 320000000
    ], # Town Maps

    [
        910000000
    ], # Free Market

    [
        [120040000, "Black Bean"], [211042300,"扎昆"], [105100100, "蝙蝠魔"],
        [105200000, "鲁塔比斯"], [211070000, "狮子王"], [272020110, "阿卡"], [401000001, "简单麦格纳斯"],
        [401060000, "普通/困难麦格纳斯"], [270050000, "品克缤"], [271040000, "女皇"],
        [211041700, "Ranmaru"], [105300303, "戴米安"], [992000000, "Dorothy"], [450007240, "威尔"]
    ],

    [
               280020000, 910130000, 220000006, 100000202, 921110000, 992017000, 910360000
    ], #Jump Quests

    ]

    list = "我可以带你去 #b城镇, #dBoss#k or #r跳跳地图#k !"
    i = 0
    while i < len(options):
        list += "\r\n#b#L" +unicode(i)+ "#" + unicode(options[i])
        i += 1
    i = 0
    option = sm.sendNext(list)


    if option == 0: # I want to go somewhere (maps)
        list = "你想去哪里? "
        while i < len(options2):
            list += "\r\n#b#L" +unicode(i)+ "#" + unicode(options2[i])
            i += 1
        i = 0
        ans1 = sm.sendNext(list)


        list = "这些是你的选项: "
        if ans1 == 2: # boss maps
            while i < len(maps[ans1]):
                list += "\r\n#L" + unicode(i) + "##b" + unicode(maps[ans1][i][1])
                i += 1
        else: # town/monster maps
            while i < len(maps[ans1]):
                list += "\r\n#L" + unicode(i) + "##b#m" + unicode(maps[ans1][i]) + "#"
                i += 1
        ans2 = sm.sendNext(list)
        if ans1 == 2: # boss maps
            sm.warp(maps[ans1][ans2][0], 1)
        else:
            sm.warp(maps[ans1][ans2], 0)

    else:
        sm.sendSayOkay("This option currently is uncoded.")