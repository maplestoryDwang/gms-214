if sm.getFieldID() == 993073000:
    sm.sendSayOkay("No")
else:
    sm.setReturnField(chr.getFieldID())
    options = ["Warp me!"]

    options2 = ["Home", "Town Maps", "Leveling Maps", "Boss Entrances", "Jump Quests"]

    maps = [
        800000000,
        [
            300000000, 680000000, 230000000, 910001000, 260000000, 541000000, 540000000, 211060010,
            105300000, 310000000, 211000000, 101072000, 101000000, 101050000, 130000000, 820000000, 223000000,
            410000000,
            141000000, 120040000, 209000000, 310070000, 401000000, 100000000, 271010000, 251000000, 744000000,
            551000000,
            103000000, 224000000, 241000000, 240000000, 104000000, 220000000, 150000000, 261000000, 701220000,
            807000000,
            701210000, 250000000, 800000000, 600000000, 120000000, 200000000, 800040000, 400000000, 102000000,
            914040000,
            865000000, 801000000, 105000000, 866190000, 270000000, 273000000, 701100000, 320000000
        ],  # Town Maps

        [
            [100040300, 10],
            [310030310, 20],
            [120041100, 30],
            [400010400, 40],
            [102030000, 45],
            [102040301, 55],
            [105010000, 60],
            [200010200, 70],
            [211040200, 75],
            [260020600, 85],
            [261020600, 90],
            [240010000, 100],
            [240010600, 100],
            [220020600, 110],
            [224000100, 120],
            [220060201, 115, 26],
            [224000142, 140],
            [211042200, 130, 55],
            [240040320, 135, 65],
            [240040512, 135, 70],
            [103041119, 150, 80],
            [103041129, 150, 80],
            [801040004, 169],
            [402000125, 170],
            [221030610, 170],
            [221030660, 170, 140],
            [273010000, 180],
            [273040100, 180],
            [410000123, 185],
            [310070140, 190],
            [450001000, 200],
            [450002000, 210],
            [450003000, 220],
            [450005000, 230],
            [450006130, 235],
            [450007040, 240],
        ],  # Leveling Maps

        [
            [105100100, "Balrog"], [211042300, "Zakum"], [240050400, "Horntail"],
            [211070000, "Von Leon"], [272020110, "Arkarium"],
            [270050000, "Pink Bean"], [271040000, "Cygnus"],
            [105200000, "Root Abyss"],
            [401000001, "Easy Magnus"], [401060000, "Normal/Hard Magnus"],
            [211041700, "Ranmaru"],
            [992000000, "Dorothy"],
            [350060300, "Lotus"],  # 350060000 ← イベントのスウと戦える
            [105300303, "Damien"],
            [450004000, "Lucid", 230],
            [450007240, "Will", 240],
            [450012500, "BlackMage", 250]
        ],  # Boss Map

        [
            280020000, 910130000, 220000006, 100000202, 921110000, 992017000, 910360000
        ],  # Jump Quests

    ]

    i = 0
    list = "Where would you like to go? "
    while i < len(options2):
        list += "\r\n#b#L" + str(i) + "#" + str(options2[i])
        i += 1
    i = 0
    ans1 = sm.sendNext(list)
    list = "These are your options: "
    isAns2 = True
    if ans1 == 0:
        sm.warp(maps[ans1], 1)
        isAns2 = False
    elif ans1 == 2:  # leveling maps
        while i < len(maps[ans1]):
            list += "\r\n#L" + str(i) + "##b#m" + str(maps[ans1][i][0]) + "# : " + str(maps[ans1][i][1]) + " Lv"
            if len(maps[ans1][i]) == 3:
                list += ", Star " + str(maps[ans1][i][2])
            i += 1
    elif ans1 == 3:  # boss maps
        while i < len(maps[ans1]):
            list += "\r\n#L" + str(i) + "##b" + str(maps[ans1][i][1])
            if len(maps[ans1][i]) == 3:
                list += " : " + str(maps[ans1][i][2]) + " Lv"
            i += 1
    else:  # town/monster maps
        while i < len(maps[ans1]):
            list += "\r\n#L" + str(i) + "##b#m" + str(maps[ans1][i]) + "#"
            i += 1

    if isAns2:
        ans2 = sm.sendNext(list)
        if ans1 == 2:
            if maps[ans1][ans2][1] >= 200:
                if maps[ans1][ans2][1] <= sm.getChr().getLevel():
                    sm.warp(maps[ans1][ans2][0], 1)
                else:
                    sm.sendSayOkay("Level Cap: " + str(maps[ans1][ans2][1]))
            else:
                sm.warp(maps[ans1][ans2][0], 1)
        elif ans1 == 3:  # boss maps
            if len(maps[ans1][ans2]) == 3:
                if maps[ans1][ans2][2] <= sm.getChr().getLevel():
                    sm.warp(maps[ans1][ans2][0], 1)
                else:
                    sm.sendSayOkay("Level Cap: " + str(maps[ans1][ans2][2]))
            else:
                sm.warp(maps[ans1][ans2][0], 1)
        else:
            sm.warp(maps[ans1][ans2], 0)
