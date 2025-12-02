from net.swordie.ms.enums import WeatherEffNoticeType
from time import sleep


if chr.getInstance() is not None:
    sm.removeReactor()
#     sm.invokeAfterDelay(1500, "spawnMob", 9400942, -200, 440, False)
#     sm.invokeAfterDelay(1500, "spawnMob", 8930100, -200, 440, False, 1L)
    sleep(2)
    sm.spawnMob(8930100, -200, 440, 1)

    sm.showWeatherNotice("You ignore my warnings?! I will show you no mercy!", WeatherEffNoticeType.BossVellum, 10000)
    sm.dispose()
