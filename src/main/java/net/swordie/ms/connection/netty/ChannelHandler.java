package net.swordie.ms.connection.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.swordie.ms.Server;
import net.swordie.ms.ServerConstants;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.User;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.handlers.Handler;
import net.swordie.ms.handlers.header.InHeader;
import net.swordie.ms.util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.swordie.ms.connection.netty.NettyClient.CLIENT_KEY;


/**
 * Created by Tim on 2/28/2017.
 */
@Slf4j
public class ChannelHandler extends SimpleChannelInboundHandler<InPacket> {

//    private static final Logger log = LogManager.getLogger(ChannelHandler.class);
    private static final Map<InHeader, Method> handlers = new HashMap<>();

    public static void initHandlers(boolean mayOverride) {
        long start = System.currentTimeMillis();
        String handlersDir = ServerConstants.HANDLERS_DIR;
        Set<File> files = new HashSet<>();
        Util.findAllFilesInDirectory(files, new File(handlersDir));
        for (File file : files) {
            try {
                // grab all files in the handlers dir, strip them to their package name, and remove .java extension
                String className = file.getPath()
                        .replaceAll("[\\\\|/]", ".")
                        .split("src\\.main\\.java\\.")[1]
                        .replaceAll("\\.java", "");
                Class clazz = Class.forName(className);
                for (Method method : clazz.getMethods()) {
                    Handler handler = method.getAnnotation(Handler.class);
                    if (handler != null) {
                        InHeader header = handler.op();
                        if (header != InHeader.NO) {
                            if (handlers.containsKey(header) && !mayOverride) {
                                throw new IllegalArgumentException(String.format("Multiple handlers found for header %s! " +
                                        "Had method %s, but also found %s.", header, handlers.get(header).getName(), method.getName()));
                            }
                            handlers.put(header, method);
                        }
                        InHeader[] headers = handler.ops();
                        for (InHeader h : headers) {
                            handlers.put(h, method);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        log.info("Initialized " + handlers.size() + " handlers in " + (System.currentTimeMillis() - start) + "ms.");
    }

    public ChannelHandler(boolean autoRelease) {
        super(autoRelease);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        NettyClient o = ctx.channel().attr(CLIENT_KEY).get();
//        if(!LoginAcceptor.channelPool.containsKey(o.getIP())) {
//            System.out.println("[Dropping currently unknown client]");
//            o.close();
//        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("[ChannelHandler] | Channel inactive.");
        Client c = (Client) ctx.channel().attr(CLIENT_KEY).get();
        if (c != null) {
            User user = c.getUser();
            Char chr = c.getChr();
            if (chr != null && !chr.isChangingChannel()) {
                chr.logout();
            } else if (chr != null) { // changing channel
                chr.getClient().getChannelInstance().removeChar(chr);
                chr.setChangingChannel(false);
            } else if (user != null) {
                user.unstuck();
            }
        }
        NettyClient o = ctx.channel().attr(CLIENT_KEY).get();
        if (o != null) {
            o.close();
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        Client c = (Client) ctx.channel().attr(CLIENT_KEY).get();
        Char chr = c.getChr();
        short op = inPacket.decodeShort();
        if (op == -1) {
            op = inPacket.decodeShort();
        }
        if (Server.getInstance().isOpcodeEnc() && c.getEncryptedHeaderToNormalHeaders().size() != 0) {
            // only op that gets sent when in-game that is a login op is the private server packet
            // so -1 => client sent private server packet
            op = c.getEncryptedHeaderToNormalHeaders().getOrDefault(op, InHeader.PRIVATE_SERVER_PACKET.getValue());
        }
        InHeader inHeader = InHeader.getInHeaderByOp(op);
        inPacket.setPacketID(op);
        if (inHeader == null) {
            handleUnknown(inPacket, op, inHeader);
            return;
        }

        Method method = handlers.get(inHeader);
        try {
            if (method == null) {
                handleUnknown(inPacket, op, inHeader);
            } else {
                Class clazz = method.getParameterTypes()[0];
                try {
                    if (method.getParameterTypes().length == 3) {
                        printMethodInvokeInfo(op, inPacket, method);
                        method.invoke(this, chr, inPacket, inHeader);
                    } else if (clazz == Client.class) {
                        printMethodInvokeInfo(op, inPacket, method);

                        method.invoke(this, c, inPacket);
                    } else if (clazz == Char.class) {
                        printMethodInvokeInfo(op, inPacket, method);

                        method.invoke(this, chr, inPacket);
                    } else {
                        log.error("Unhandled first param type of handler " + method.getName() + ", type = " + clazz);
                    }
//                } catch (IllegalAccessException | InvocationTargetException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            inPacket.release();
        }
    }

    private void printMethodInvokeInfo(short op, InPacket inPacket, Method method) {

        InHeader inHeaderByOp = InHeader.getInHeaderByOp(op);
        if (!InHeader.isSpamHeader(inHeaderByOp)) {
            log.debug("[In]\t| {}, {}/0x{}\t| {} | {}", inHeaderByOp, op, Integer.toHexString(op), method.getName(), inPacket);

//            log.debug(String.format("[In]\t| %s, %d/0x%s\t| %s", inHeaderByOp, op, Integer.toHexString(op).toUpperCase(), inPacket));
        }
    }

    /**
     * 没header和没methods都进的这里，需要区分一下
     *
     * @param inPacket
     * @param opCode
     * @param inHeader
     */
    private void handleUnknown(InPacket inPacket, short opCode, InHeader inHeader) {
//        if (!InHeader.isSpamHeader(InHeader.getInHeaderByOp(opCode))) {
//            log.debug(String.format("Unhandled opcode %s/0x%s, packet %s", opCode, Integer.toHexString(opCode).toUpperCase(), inPacket));
//        }
        boolean contains = InHeader.UNKNOW_HEADER.contains(opCode);
        if (contains) {
            return;
        }

        if (inHeader == null) {
            log.debug(String.format("[Unhandled]\t  %s/0x%s, packet %s", opCode, Integer.toHexString(opCode).toUpperCase(), inPacket));
        } else {
            log.debug(String.format("[method not found!]\t %s/0x%s, packet %s", opCode, Integer.toHexString(opCode).toUpperCase(), inPacket));
        }
        InHeader.UNKNOW_HEADER.add(opCode);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.debug("Client forcibly closed the game.");
        } else {
            cause.printStackTrace();
        }
    }
}