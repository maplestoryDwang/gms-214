# id 17672 ([Commerci Republic] Claire Tremier: Ace Attorney), field 865000002
sm.setSpeakerID(9390203) # Gilberto Daniella
sm.setParam(56)
sm.setColor(1)
sm.sendNext("Sir, I have something to tell you.")
sm.setParam(36)
sm.setInnerOverrideSpeakerTemplateID(9390206) # Vaughn Tremier
sm.sendSay("How DARE you come back here?!")
sm.setParam(32)
sm.sendSay("Why are you here?")
sm.setParam(36)
sm.sendSay("Gilberto, there's no point in listening to this nonsense! Why aren't you arresting them?")
sm.setParam(32)
res = sm.sendAskYesNo("Why are you so afraid to hear what they have to say, Vaughn? Please, say what you've come to say. ")
sm.setParam(56)
sm.sendNext("Zion, the delegate from the Heaven Empire, faked his death.")
sm.setParam(32)
sm.sendSay("Faked?")
sm.setParam(56)
sm.sendSay("It was part of a plot to isolate Commerci from the other nations of Dawnveil.")
sm.setParam(32)
sm.sendSay("A conspiracy...")
sm.setParam(56)
sm.sendSay("Yes. They wanted it to look like he was assassinated for bringing a peace treaty to you. The other nations wouldn't know how unfair the treaty had been.")
sm.setParam(36)
sm.sendSay("This is utter nonsense!")
sm.setParam(32)
sm.sendSay("Where is the envoy now?")
sm.setParam(56)
sm.sendSay("We... we captured him, but a real assassin got to him before we could get to San Commerci. It had to have been someone from the Heaven Empire...")
sm.setParam(32)
sm.sendSay("So he is lost to us for good now?")
sm.setParam(56)
sm.sendSay("Correct.")
sm.setParam(36)
sm.sendSay("This is ridiculous! This yarn wouldn't be fit for a children's book!")
sm.startQuest(parentID)