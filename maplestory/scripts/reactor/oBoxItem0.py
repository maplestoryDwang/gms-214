# Orbis Box (2002000) | Orbis (200000000)

kriel = 3043
bottle = 4031198

reactor.incHitCount()
reactor.increaseState()

if reactor.getHitCount() >= 4:
	if sm.hasQuest(kriel):
	    sm.dropItem(bottle, sm.getPosition(objectID).getX(), sm.getPosition(objectID).getY())
	sm.removeReactor()
