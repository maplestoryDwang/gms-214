from net.swordie.ms.constants import BossConstants
from net.swordie.ms.enums import EventType

EASY_MAGNUS_QUEST = 31851
def is_party_eligible(reqlevel, party):
    for member in party.getMembers():
        if member.getLevel() < reqlevel:
            return False

    return True

sm.setSpeakerID(3001000) # Edea
destinations = ["简单", 50, 401060300, 5, EventType.EMagnus, 8800022, 10800000]


response = sm.sendAskYesNo("Would you like to fight easy magnus?")
if response:
    if not sm.hasQuestCompleted(EASY_MAGNUS_QUEST):
        sm.sendSayOkay("Please talk to Piston to know more about the Magnus simulator.")
    elif sm.getParty() is None:
        sm.sendSayOkay("Please create a party before going in.")
    elif not sm.isPartyLeader():
        sm.sendSayOkay("Please have your party leader enter if you wish to face Magnus.")
#     elif sm.checkParty():
#         sm.setDeathCount(BossConstants.MAGNUS_DEATHCOUNT)
#         sm.warpInstanceIn(401060300, True)
    elif is_party_eligible(destinations[1], sm.getParty()):
        sm.setDeathCount(destinations[3])
        sm.warpInstanceIn(destinations[2], True)
        sm.setInstanceTime(20*60)