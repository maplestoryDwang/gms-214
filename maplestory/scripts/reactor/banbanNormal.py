from time import sleep

if chr.getInstance() is not None and reactor.getHitCount() == 0:
    # global hitCount
    # hitCount += 1
    # sm.chat(str(hitCount))
    # if hitCount >= 1:
    reactor.incHitCount()
#     sm.spawnMob(9303154, -135, 455, False) # 没有血条是系统判定的？
    sm.spawnMob(8910100, -135, 455, False)
#     sm.spawnMob(8900100, -135, 455, False)

    sm.removeReactor()
    sleep(2)
    sm.removeReactor()
