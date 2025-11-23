package net.swordie.ms.connection;

import io.netty.buffer.*;
import lombok.extern.slf4j.Slf4j;
import net.swordie.ms.ServerConstants;
import net.swordie.ms.handlers.header.InHeader;
import net.swordie.ms.util.CustomConfigsLoad;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.Rect;
import net.swordie.ms.util.Util;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 2/18/2017.
 */
@Slf4j
public class InPacket extends Packet {
    private final ByteBuf byteBuf;
    private boolean loopback;
    private short packetID;

    // 分析具体包头开关
    private boolean debugInpacket  = Boolean.parseBoolean(CustomConfigsLoad.getConfig("app.debug.inpacket"));

    List<InHeader> debugHeaderList = Arrays.asList(
//        InHeader.USER_SHOOT_ATTACK
    );

            /**
             * todo 后面加别的条件
             * @return
             */
    public boolean debugPacket() {
        InHeader inHeaderByOp = InHeader.getInHeaderByOp(packetID);
        boolean debugHeader = debugHeaderList.contains(inHeaderByOp);
        return debugInpacket && debugHeader;
    }

    /**
     * 获取调用的方法
     * @return
     */
    public String getCallerMethodName(String inpacketMethodName, Object result) {
        // 获取当前线程的调用栈
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement stackTraceElement = stackTrace[3];
            String methodName = stackTraceElement.getMethodName();
            String className = stackTraceElement.getClassName();
            int lineNumber = stackTraceElement.getLineNumber();
            log.debug("{} {} line:{} cal {} \t result:[{}], leave:{}", className, methodName, lineNumber, inpacketMethodName, result, this);
            return stackTraceElement.getMethodName();
        }
        return "Unknown";
    }





    /**
     * Creates a new InPacket with a given buffer.
     * @param byteBuf The buffer this InPacket has to be initialized with.
     */
    public InPacket(ByteBuf byteBuf) {
        super(byteBuf.array());
        this.byteBuf = byteBuf.copy();
        if (debugPacket()) {
            log.debug("[debug.inpacket]\t InPacket{}", this.toString());
        }
    }

    /**
     * Creates a new InPacket with no data.
     */
    public InPacket(){
        this(Unpooled.buffer());
    }

    /**
     * Creates a new InPacket with given data.
     * @param data The data this InPacket has to be initialized with.
     */
    public InPacket(byte[] data) {
        this(Unpooled.copiedBuffer(data));
    }

    @Override
    public int getLength() {
        return byteBuf.readableBytes();
    }

    @Override
    public byte[] getData() {
        return byteBuf.array();
    }

    @Override
    public InPacket clone() {
        return new InPacket(byteBuf);
    }

    /**
     * Reads a single byte of the ByteBuf.
     * @return The byte that has been read.
     */
    public byte decodeByte() {
        byte b = byteBuf.readByte();
        if (debugPacket()) {
            getCallerMethodName("decodeByte", b);
        }
        return b;
    }

    public short decodeUByte() {
        short i = byteBuf.readUnsignedByte();
        if (debugPacket()) {
            getCallerMethodName("decodeByte", i);
        }
        return i;
    }

    /**
     * Reads an <code>amount</code> of bytes from the ByteBuf.
     * @param amount The amount of bytes to read.
     * @return The bytes that have been read.
     */
    public byte[] decodeArr(int amount) {
        byte[] arr = new byte[amount];
        for(int i = 0; i < amount; i++) {
            arr[i] = byteBuf.readByte();
        }
        if (debugPacket()) {
            getCallerMethodName("decodeArr", arr);
        }

        return arr;
    }

    /**
     * Reads an integer from the ByteBuf.
     * @return The integer that has been read.
     */
    public int decodeInt() {
        int readIntResult = byteBuf.readIntLE();
        if (debugPacket()) {
            getCallerMethodName("decodeInt", readIntResult);
        }
        return readIntResult;

    }

    /**
     * Reads a short from the ByteBuf.
     * @return The short that has been read.
     */
    public short decodeShort() {
        short readShortResult = byteBuf.readShortLE();
        if (debugPacket()) {
            getCallerMethodName("decodeShort", readShortResult);
        }
        return readShortResult;
    }


    /**
     * Reads a char array of a given length of this ByteBuf. 中文编码
     * @param amount The length of the char array
     * @return The char array as a String
     */
    public String decodeString(int amount) {
        byte[] bytes = decodeArrInner(amount);
        String strResult = new String(bytes, ServerConstants.ENCODING);
        if (debugPacket()) {
            getCallerMethodName("decodeString(int amount)", strResult);
        }
        return strResult;
    }
//    /**
//     * Reads a char array of a given length of this ByteBuf.
//     * @param amount The length of the char array
//     * @return The char array as a String
//     */
//    public String decodeString(int amount) {
//        byte[] bytes = decodeArr(amount);
//        char[] chars = new char[amount];
//        for(int i = 0; i < amount; i++) {
//            chars[i] = (char) bytes[i];
//        }
//        return String.valueOf(chars);
//    }

    /**
     * Reads a String, by first reading a short, then reading a char array of that length.
     * @return The char array as a String
     */
    public String decodeString() {
        int amount = decodeShortInner();
        String strResult = decodeString(amount);
        if (debugPacket()) {
            getCallerMethodName("decodeString()", strResult);
        }
        return strResult;
    }

    @Override
    public String toString() {
        return Util.readableByteArray(Arrays.copyOfRange(getData(), getData().length - getUnreadAmount(), getData().length)); // Substring after copy of range xd
    }


    /**
     * Reads and returns a long from this net.swordie.ms.connection.packet.
     * @return The long that has been read.
     */
    public long decodeLong() {
        long longResult = byteBuf.readLongLE();
        if (debugPacket()) {
            getCallerMethodName("decodeLong()", longResult);
        }
        return longResult;
    }

    /**
     * Reads a position (short x, short y) and returns this.
     * @return The position that has been read.
     */
    public Position decodePosition() {
        Position position = new Position(decodeShortInner(), decodeShortInner());
        if (debugPacket()) {
            getCallerMethodName("decodePosition()", position);
        }
        return position;
    }

    /**
     * Reads a position (int x, int y) and returns this.
     * @return The position that has been read.
     */
    public Position decodePositionInt() {
        Position position = new Position(decodeIntInner(), decodeIntInner());
        if (debugPacket()) {
            getCallerMethodName("decodePositionInt()", position);
            log.debug("[debug.inpacket]\t result:[{}], leave:{}", position, this);
        }
        return position;
    }

    /**
     * Reads a rectangle (short l, short t, short r, short b) and returns this.
     * @return The rectangle that has been read.
     */
    public Rect decodeShortRect() {
        Rect rect = new Rect(decodePositionInner(), decodePositionInner());
        if (debugPacket()) {
            getCallerMethodName("decodeShortRect()", rect);
        }
        return rect;
    }

    /**
     * Reads a rectangle (int l, int t, int r, int b) and returns this.
     * @return The rectangle that has been read.
     */
    public Rect decodeIntRect() {
        Rect rect = new Rect(decodePositionIntInner(), decodePositionIntInner());
        if (debugPacket()) {
            getCallerMethodName("decodeIntRect()", rect);
        }
        return rect;
    }


    /**
     * ===========================内部调用===========================
     * @param amount
     * @return
     */
    private byte[] decodeArrInner(int amount) {
        byte[] arr = new byte[amount];
        for(int i = 0; i < amount; i++) {
            arr[i] = byteBuf.readByte();
        }
        return arr;
    }

    /**
     * Reads a short from the ByteBuf.
     * @return The short that has been read.
     */
    private short decodeShortInner() {
        return byteBuf.readShortLE();
    }


    /**
     * Reads an integer from the ByteBuf.
     * @return The integer that has been read.
     */
    private int decodeIntInner() {
        return byteBuf.readIntLE();

    }


    /**
     */
    public long available() {
        return byteBuf.array().length - byteBuf.readerIndex();
    }

    /**
     * Reads a position (short x, short y) and returns this.
     * @return The position that has been read.
     */
    private Position decodePositionInner() {
        return new Position(decodeShortInner(), decodeShortInner());
    }

    /**
     * Reads a position (int x, int y) and returns this.
     * @return The position that has been read.
     */
    private Position decodePositionIntInner() {
        return new Position(decodeIntInner(), decodeIntInner());
    }


    /**
     * Returns the amount of bytes that are unread.
     * @return The amount of bytes that are unread.
     */
    public int getUnreadAmount() {
        return byteBuf.readableBytes();
    }

    public void release() {
        byteBuf.release();
    }

    public boolean isLoopback() {
        return loopback;
    }

    public void setLoopback(boolean loopback) {
        this.loopback = loopback;
    }

    public short getPacketID() {
        return packetID;
    }

    public void setPacketID(short packetID) {
        this.packetID = packetID;
    }
}
