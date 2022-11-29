# id 64026 ([MONAD: The First Omen] Ullan's Pets), field 867200400
sm.createQuestWithQRValue(parentID, "chk1=0")
sm.createQuestWithQRValue(parentID, "chk1=0;chk2=0")
sm.setSpeakerType(3)
sm.setParam(57)
sm.setColor(1)
sm.sendNext("#bUllan? ")
sm.setParam(37)
sm.setInnerOverrideSpeakerTemplateID(9400588) # Ullan
sm.sendSay("Ah, #h0#! ")
sm.setParam(57)
sm.sendSay("#bAre you looking for something? #k")
sm.setParam(37)
sm.sendSay("Yes! Pete and Elle haven't returned yet. I can't leave them behind... ")
sm.setParam(57)
sm.sendSay("#bPete? Elle? ")
sm.setParam(37)
sm.sendSay("Yes! The little Shrelephants you saved before. ")
sm.setParam(57)
sm.sendSay("#bOh right, one had the ribbon... Wait, you still haven't found them? ")
sm.setParam(37)
sm.sendSay("No... Sniff... ")
sm.setParam(57)
res = sm.sendNext("#b(There should be some time before the caravan is ready.)\r\n#L0# 'We'll look together.' #l\r\n#L1# 'I'll check on the others.'#l")
sm.setParam(37)
sm.sendNext("Really?! Ah... but if Dad finds out, I'll get in trouble for bothering you with this... ")
sm.setParam(57)
sm.sendSay("#bBy Dad, you mean Kan? Don't worry, I won't tell Chief Kan. ")
sm.setParam(37)
sm.sendSay("You made a promise! ")
res = sm.sendNext("In that case, do you think you could go to just the outskirts of the town there with me...? #b\r\n#L0# Of course.#l\r\n#L1# That may be a bit difficult.#l")
sm.sendNext("Yay! Then follow me!")
sm.startQuest(parentID)
sm.lockInGameUI(True, False)
sm.moveNpcByTemplateId(9400588, False, 500, 200)
sm.sendDelay(2000)
sm.forcedMove(False, 500)
sm.sendDelay(3000)
sm.blind(True, 255, 0, 0, 0, 500)
sm.sendDelay(500)
sm.lockInGameUI(False, True)
sm.warp(867200480)
