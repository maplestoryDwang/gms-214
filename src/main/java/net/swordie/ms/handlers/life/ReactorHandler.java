package net.swordie.ms.handlers.life;

import lombok.extern.slf4j.Slf4j;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.handlers.Handler;
import net.swordie.ms.handlers.header.InHeader;
import net.swordie.ms.life.Life;
import net.swordie.ms.life.Reactor;
import net.swordie.ms.loaders.ReactorData;
import net.swordie.ms.loaders.containerclasses.ReactorInfo;
import net.swordie.ms.scripts.ScriptType;

import javax.script.ScriptException;

@Slf4j
public class ReactorHandler {



//    @Handler(op = InHeader.REACTOR_CLICK)
    @Handler(ops = {InHeader.REACTOR_HIT})
    public static void handleReactorHit(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        int objID = inPacket.decodeInt();
        int idk = inPacket.decodeInt();
        byte type = inPacket.decodeByte();
        Life life = chr.getField().getLifeByObjectID(objID);
        if (!(life instanceof Reactor)) {
            log.error("Could not find reactor with objID " + objID);
            return;
        }
        Reactor reactor = (Reactor) life;
        int templateID = reactor.getTemplateId();
        ReactorInfo ri = ReactorData.getReactorInfoByID(templateID);
        String action = ri.getAction();
        if (chr.getScriptManager().isActive(ScriptType.Reactor)
                && chr.getScriptManager().getParentIDByScriptType(ScriptType.Reactor) == templateID) {
            try {
                chr.getScriptManager().getInvocableByType(ScriptType.Reactor).invokeFunction("action", reactor, type);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            chr.getScriptManager().startScript(templateID, objID, action, ScriptType.Reactor);
        }
    }

    @Handler(op = InHeader.REACTOR_CLICK)
    public static void handleReactorClick(Client c, InPacket inPacket) {
        int objID = inPacket.decodeInt();
        Char chr = c.getChr();

        boolean touched = inPacket.available() == 0 || inPacket.decodeByte() > 0; //the byte is probably the state to set it to

        Life life = chr.getField().getLifeByObjectID(objID);
        if (!(life instanceof Reactor)) {
            log.error("Could not find reactor with objID " + objID);
            return;
        }
        Reactor reactor = (Reactor) life;
        if (!touched || reactor == null ) {
            System.out.println("點擊反應堆出現錯誤 - !touched: " + !touched);
            return;
        }
        int templateID = reactor.getTemplateId();
        ReactorInfo ri = ReactorData.getReactorInfoByID(templateID);
        String action = ri.getAction();
        if (chr.getScriptManager().isActive(ScriptType.Reactor)
                && chr.getScriptManager().getParentIDByScriptType(ScriptType.Reactor) == templateID) {
            try {
                int idk = inPacket.decodeInt();
                byte type = inPacket.decodeByte();
                chr.getScriptManager().getInvocableByType(ScriptType.Reactor).invokeFunction("action", reactor, type);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            chr.getScriptManager().startScript(templateID, objID, action, ScriptType.Reactor);
        }
    }



    @Handler(op = InHeader.REACTOR_RECT_IN_MOB)
    public static void handleReactorRectInMob(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        int objID = inPacket.decodeInt();

        Life life = chr.getField().getLifeByObjectID(objID);
        if (!(life instanceof Reactor)) {
            log.error("Could not find reactor with objID " + objID);
            return;
        }
        Reactor reactor = (Reactor) life;
        int templateID = reactor.getTemplateId();
        ReactorInfo ri = ReactorData.getReactorInfoByID(templateID);
        String action = ri.getAction();
        if (action.equals("")) {
            action = templateID + "action";
        }
        if (chr.getScriptManager().isActive(ScriptType.Reactor)
                && chr.getScriptManager().getParentIDByScriptType(ScriptType.Reactor) == templateID) {
            try {
                chr.getScriptManager().getInvocableByType(ScriptType.Reactor).invokeFunction("action", 0);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            chr.getScriptManager().startScript(templateID, objID, action, ScriptType.Reactor);
        }
    }
}
