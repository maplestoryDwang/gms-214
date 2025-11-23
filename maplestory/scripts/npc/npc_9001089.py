# sm.sendAskYesNo(str(sm.getFieldID()))


options = ["存储货币","升级你的小货车","雇佣搬运工","解雇搬运工","退出"]
options2 = [
               [3801023, "瘦削搬运工",100],
               [3801024, "沙漠养生师",200],
               [3801025, "沙漠舞姬",300],
               [3801026, "沙漠战士",400],
               [3801027,"坚韧的沙漠狐狸",500],
           ]

# list = "怎么样玩的开心吗"
# i = 0
# while i < len(options):
#     list += "\r\n#b#L" +unicode(i)+ "#" + unicode(options[i])
#     i += 1
#     option = sm.sendNext(list)
#     if option == 0: # I want to go somewhere (maps)
#         sm.sendAskYesNo("切换货币咯\r\n#b"):
#     elif option == 1:
#         if sm.sendAskYesNo("你想结束你的旅途吗？\r\n#b"):
#             sm.warp(993000800)



if sm.getFieldID() == 993000837:
    sm.sendSay("玩的开心吗欢迎你下次再来")
    sm.warp(993000801)
elif sm.getFieldID() == 993000801:
    list = "怎么样玩的开心吗？"
    i = 0
    while i < len(options):
        list += "\r\n#b#L" +unicode(i)+ "#" + unicode(options[i])
        i += 1
    i = 0
    nowCoin = tradeKingInfo.getCount()
    allCoin = tradeKingInfo.getScount()
    option = sm.sendNext(list)
    if option == 0:
        list = "帮你存储货币，手续费为：50个#i" + unicode(4034849) + "# : 你想兑换吗？"
        option = sm.sendNext(list)
#         nowCoin = sm.getTradeKingNowCoin()
#         allCoin = sm.getTradeKingSaveCoin()



        list = "#r" +"兑换会提前结束当前贸易请注意！#k"+ "\r\n"
        list += "当前持有货币：" + "#i" + unicode(4034849) + "# : " + str(nowCoin) + "\r\n"
        list += "当前存储货币：" + "#i" + unicode(4034849) + "# : " + str(allCoin) + "\r\n"
        if sm.sendAskYesNo(list):
            if (nowCoin == 0):
                sm.sendSay("可兑换货币不足.")
                sm.dispose()
            else:
                param = "count=0;scount=" + str(nowCoin + allCoin)
                sm.saveTradeKing(15324, param)
    #           退出当前
                sm.warpInstanceOut(993000801, 2)
                sm.getTradeKingEnd()
    elif option == 1:
        ride = tradeKingInfo.getRide()
        list = "你当前的坐骑是："
        rideId = 80001950
        payCoin = 0
        if ride == 80001950:
            list += "#i" + unicode(3801028) + "# : " + "\r\n"
            list +="需要支付：500" + "#i" + unicode(4034849) + "# : " + "升级成：" + "#i" + unicode(3801029) + "#" + "\r\n"
            rideId = 80001951
            payCoin = 500
        elif ride == 80001951:
            rideId = 80001952
            payCoin = 1500
            list += "#i" + unicode(3801029) + "# : " + "\r\n"
            list +="需要支付："+ str(payCoin) + "#i" + unicode(4034849) + "# : " + "升级成：" + "#i" + unicode(3801030) + "#" + "\r\n"
        elif ride == 80001952:
            list += "#i" + unicode(3801030) + "# : " + "\r\n"
            list += "已经无法再升级"
            sm.sendSay(list)
        if ride == 80001950 or ride == 80001951:
            list += "当前存储货币：" + "#i" + unicode(4034849) + "# : " + str(allCoin) + "\r\n"
            if sm.sendAskYesNo(list):
                if allCoin < payCoin:
                    sm.sendSay("可兑换货币不足.")
                    sm.dispose()
                else:
                    param = "scount=" + str(payCoin)+";ride=" + str(rideId)
                    sm.saveTradeKing(0, param)
                    sm.sendSay("升级成功！请重新开始")
        #           退出当前
                    sm.warpInstanceOut(993000801, 2)
                    sm.getTradeKingEnd()
    elif option == 2:
        list = "当前存储货币：" + "#i" + unicode(4034849) + "# : " + str(allCoin) + "\r\n"
        while i < len(options2):
            list += "\r\n#L" + unicode(i) + "##b" + "#i" + unicode(options2[i][0]) + "#  " + unicode(options2[i][1]) + "  雇佣费用  " + unicode(options2[i][2])
            i += 1
        select = sm.sendNext(list)
        list = "你想雇佣：" + unicode(options2[select][1]) + "花费："  + unicode(options2[select][2]) + "#i" + unicode(4034849) + "# : "
        if sm.sendAskYesNo(list):
            payCoin = options2[select][2]
            if allCoin < payCoin:
                sm.sendSay("可兑换货币不足.")
                sm.dispose()
            else:
                payCoin = options2[select][2]
                workerIndex = select
                param = "scount=" + str(payCoin)+";worker=" + str(workerIndex)
                sm.saveTradeKing(15325, param)
                sm.sendSay("升级成功！请重新开始")
    #           退出当前
                sm.warpInstanceOut(993000801, 2)
                sm.getTradeKingEnd()

    elif option == 4:
#           退出当前
        sm.warpInstanceOut(993000800, 2)
        sm.getTradeKingEnd()

else:
    sm.sendSay("点击休彼得曼开始你的旅途")
