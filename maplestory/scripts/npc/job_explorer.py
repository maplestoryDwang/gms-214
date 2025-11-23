# TODO: {serverName} Custom Beginnings - Explorer

speaker = 2007 # maple administrator

# JobOptions: { string jobName, string jobDesc, int jobId }
options = [
    ["战士", "powerful and defensive", 100],
    ["弓箭手", "long-ranged and controlled", 300],
    ["魔法师", "intelligent and magical", 200],
    ["飞侠", "speedy and sneaky", 400],
    ["海盗", "fancy and unique", 500],
    ["Jett", "Not like the other heroes", 508],
]

optionText = "是时候选择你的职业了!\r\n作为一个 #b冒险家#k, 你可以选择下面 " + str(len(options)) + " 种职业:"

for idx, job in enumerate(options):
    optionText += "\r\n#b#L" + str(job[2]) + "#" + job[0] + "#k, " + job[1] + "#l"
choice = sm.sendNext(optionText)


for job in options:
    if (choice == job[2]):
        response = sm.sendAskYesNo("So, you wish to become a #b" + job[0] + "#k?")
        if response:
            sm.jobAdvance(job[2])
            sm.doJobEnd()
            sm.sendSayOkay("You are now a #b" + job[0] + "#k!")
        else:
            # executes the current script again
            sm.dispose()
            sm.startScript("job_explorer", "npc")