from net.swordie.ms.scripts import ScriptType
# from net.swordie.ms.constants import AchievementConstant
# from net.swordie.ms.client import Achievements

# 死亡mob
DeadIDs = [8810010, 8810011, 8810012, 8810013, 8810206, 8810014, 8810016, 8810017]

EhorntailIDs = [8810202, 8810203, 8810204, 8810205, 8810206, 8810207, 8810208, 8810209]
NhorntailIDs = [8810002, 8810003, 8810004, 8810005, 8810006, 8810007, 8810008, 8810009]
ChorntailIDs = [8810102, 8810103, 8810104, 8810105, 8810106, 8810107, 8810108, 8810109]
EhorntailMap = 240060300
NhorntailMap = 240060200
ChorntailMap = 240060201

if sm.getFieldID() == EhorntailMap:
    mobs = EhorntailIDs
    dropMob = 8810214

if sm.getFieldID() == NhorntailMap:
    mobs = NhorntailIDs
    dropMob = 8810018

if sm.getFieldID() == ChorntailMap:
    mobs = ChorntailIDs

# --- 构建 mob -> deadMob 的映射 ---
mobToDead = {mobId: DeadIDs[i] for i, mobId in enumerate(mobs)}

# --- 一次性召唤所有 Horntail 部位 ---
for mobId in mobs:
    sm.spawnMob(mobId, 95, 260, False)


count = 0
while count < len(mobs):
    mob = sm.waitForMobDeath(mobs)  # 等待任意一个死亡
    mobId = mob.getTemplateId()     # 获取死亡的 mob 的模板ID

    if mobId in mobToDead:
        deadId = mobToDead[mobId]
        sm.spawnMob(deadId, 95, 260, False)
        count += 1
    else:
        count += 1


sm.killMobs()
# sm.spawnMob(dropMob)
# sm.killMobs(True)
# if not chr.getAccount().isExistAchievement(AchievementConstant.MOB_HORNTAIL) and sm.getFieldID() == NhorntailMap:
#     Achievements.getInstance().getById(AchievementConstant.MOB_HORNTAIL).finishAchievement(chr)
# elif not chr.getAccount().isExistAchievement(AchievementConstant.MOB_CHAOS_HORNTAIL) and sm.getFieldID() == ChorntailMap:
#     Achievements.getInstance().getById(AchievementConstant.MOB_CHAOS_HORNTAIL).finishAchievement(chr)