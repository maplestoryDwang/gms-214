# id 867201340 (Abrup Basin : Unbent Timber Lane), field 867201340
sm.setMapTaggedObjectVisible("ribbon01", False, 0, 0)
sm.lockInGameUI(True, False)
sm.spawnNpc(9400580, 1987, 346)
sm.showNpcSpecialActionByTemplateId(9400580, "summon", 0)
sm.spawnNpc(9400595, 2076, 446)
sm.showNpcSpecialActionByTemplateId(9400595, "summon", 0)
sm.sendDelay(500)
sm.showNpcSpecialActionByTemplateId(9400580, "ribbon", -1)
sm.sendDelay(3000)
sm.resetNpcSpecialActionByTemplateId(9400580)
sm.setMapTaggedObjectVisible("ribbon01", True, 0, 0)
sm.sendDelay(1000)
sm.lockInGameUI(False, True)