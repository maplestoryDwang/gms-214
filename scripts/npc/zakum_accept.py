from net.swordie.ms.constants import BossConstants
from net.swordie.ms.enums import EventType
from time import sleep

# Mode, Required Level, Map ID, Death Count, Event Type, Cooldown

destinations = [
    ["简单", 50, 280030200, 5, EventType.EasyZakum, 8800022, 10800000],
    ["普通", 75, 280030100, 5, EventType.NormalZakum, 8800002, 21600000],
    ["困难", 120, 280030000, 5, EventType.ChaosZakum, 8800102, 43200000],
]

runsPerDay = 1

if sm.getFieldID() == 211042400:
    def is_party_eligible(reqlevel, party):
        for member in party.getMembers():
            if member.getLevel() < reqlevel:
                return False

        return True

    sm.setSpeakerID(2030008)

    dialog = "你准备好去攻略 #b扎昆#k了吗?\r\n"
    dialog += "选择你的挑战难度:\r\n"
    for i in range(len(destinations)):
        dialog += "#L%d##b %s 模式 #r(Lv. %d+)#b#l\r\n" % (i, destinations[i][0], destinations[i][1])

    dialog += "#L99#我点错了."
    response = sm.sendSay(dialog)

    if sm.getParty() is None:
        sm.sendSayOkay("你需要创建一个队伍")
        sm.dispose()

    elif not sm.isPartyLeader():
        sm.sendSayOkay("让你的队长和我说话 #bZakum#k.")
        sm.dispose()

    elif sm.partyHasCoolDown(destinations[response][4], runsPerDay):
        timeUntilReset = sm.getTimeUntilEventReset(destinations[response][4])
        sm.sendNext("You or one of your party member has already attempted facing #bZakum#k recently.\r\n\r\n You have #e#r" + timeUntilReset + "#n#k left on your cooldown.")
        sm.dispose()

#     elif not sm.hasItem(4001017):
#         sm.sendSayOkay("You do not possess a #b#v 4001017 # #z 4001017 ##k.")
#         sm.dispose()


    elif sm.checkParty() and response != 99:
#         if is_party_eligible(destinations[response][1], sm.getParty()):
#           #  sm.addCooldownTimeForParty(destinations[response][4], destinations[response][5])
#             sm.warpInstanceIn(destinations[response][2], True)
#             sm.setPartyDeathCount(destinations[response][3])
#             sm.setInstanceTime(BossConstants.ZAKUM_TIME)

        if is_party_eligible(destinations[response][1], sm.getParty()):
            sm.setDeathCount(destinations[response][3])
            sm.warpInstanceIn(destinations[response][2], True)
            sm.setInstanceTime(20*60)
            sleep(1)
#             sm.spawnMob(destinations[response][5], -54, 86, False,destinations[response][6])
#             sm.spawnMob(8800003, -54, 86, False)
            sm.spawnMob(8800002, -54, 86, False)
#             for i in range(8):
#                 sm.spawnMob(destinations[response][5] + 1 + i, -54, 86, False)
        else:
            sm.sendSayOkay("One or more party members are lacking the prerequisite entry quests, or are below level #b%d#k." % destinations[response][1])