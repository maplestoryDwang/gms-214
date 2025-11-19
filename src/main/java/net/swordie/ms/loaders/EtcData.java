package net.swordie.ms.loaders;

import lombok.extern.slf4j.Slf4j;
import net.swordie.ms.ServerConstants;
import net.swordie.ms.loaders.containerclasses.AndroidInfo;
import net.swordie.ms.tracekill.TraceKillQuestRxCode;
import net.swordie.ms.tracekill.TraceKillRiding;
import net.swordie.ms.tracekill.TraceKillWorker;
import net.swordie.ms.util.Loader;
import net.swordie.ms.util.Saver;
import net.swordie.ms.client.character.items.BossSoul;
import net.swordie.ms.enums.SoulType;
import net.swordie.ms.util.Util;
import net.swordie.ms.util.XMLApi;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sjonnie
 * Created on 1/4/2019.
 */
@Slf4j
public class EtcData {

    private static final Map<Integer, AndroidInfo> androidInfo = new HashMap<>();
    private static final Map<Integer, Integer> familiarSkills = new HashMap<>();
    private static Map<Integer, BossSoul> soulCollection = new HashMap<>();
    private static final Set<String> forbiddenNames = new HashSet<>();
    private static final Set<String> curseWordWhiteList = new HashSet<>();

    private static Map<Integer, List> traceKillMap = new HashMap<>();


    private static void loadFamiliarSkillsFromWz() {
        File file = new File(String.format("%s/Etc.wz/FamiliarInfo.img.xml", ServerConstants.WZ_DIR));
        Node root = XMLApi.getRoot(file);
        Node mainNode = XMLApi.getAllChildren(root).get(0);
        List<Node> nodes = XMLApi.getAllChildren(mainNode);
        for (Node node : nodes) {
            int id = Integer.parseInt(XMLApi.getNamedAttribute(node, "name"));
            Node skillIDNode = XMLApi.getFirstChildByNameBF(node, "passive");
            if (skillIDNode != null) {
                int skillID = Integer.parseInt(XMLApi.getNamedAttribute(skillIDNode, "value"));
                familiarSkills.put(id, skillID);
            }
        }
    }


    @Saver(varName = "familiarSkills")
    private static void saveFamiliarSkills(File file) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            dos.writeInt(familiarSkills.size());
            for (Map.Entry<Integer, Integer> entry : familiarSkills.entrySet()) {
                dos.writeInt(entry.getKey()); // familiar ID
                dos.writeInt(entry.getValue()); // skill ID
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Loader(varName = "etc\\familiarSkills")
    public static void loadFamiliarSkills(File file, boolean exists) {
        if (!exists) {
            //  loadFamiliarSkillsFromWz();
            //  saveFamiliarSkills(file);
        } else {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
                int gradeSkillSize = dis.readInt();
                for (int j = 0; j < gradeSkillSize; j++) {
                    int familiarID = dis.readInt();
                    int skillID = dis.readInt();
                    familiarSkills.put(familiarID, skillID);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getSkillByFamiliarID(int familiarID) {
        return familiarSkills.get(familiarID);
    }

    public static int getSkillBySoulItem(int soulItemId) {
        BossSoul bs = getBossSoulByItem(soulItemId);
        return bs != null ? bs.getSkillId() : 0;
    }


    public static boolean isForbidden(String name) {
        name = name.toLowerCase();
        // it may regulate a lot of false-positive forbidden names :thinking:
        for (String forbiddenName : forbiddenNames) {
            Pattern p = Pattern.compile(forbiddenName);
            Matcher m = p.matcher(name);
            if (m.find()) {
                LABEL: for (int i = 0, n = m.groupCount(); i <= n; i++) {
                    String matched = m.group(i);
                    for (String whiteListed : curseWordWhiteList) {
                        if (whiteListed.contains(matched) && name.contains(whiteListed)) {
                            continue LABEL;
                        }
                    }
                    return true;
                }
            }
        }
        /*for (String blackListed : curseWordBlackList) {
            Pattern p = Pattern.compile(blackListed.replace("*", ""));
            Matcher m = p.matcher(name);
            if (m.find()) {
                LABEL: for (int i = 0, n = m.groupCount(); i <= n; i++){
                    String matched = m.group(i);
                    for (String whiteListed : curseWordWhiteList) {
                        if (whiteListed.contains(matched) && name.contains(whiteListed)) {
                            continue LABEL;
                        }
                    }
                    return true;
                }
            }
        } */ // We arent babies now
        return false;
    }

    public static BossSoul getBossSoulByItem(int soulItemId) {
        if (soulCollection.isEmpty()) {
            loadSoulCollectionFromFile(ServerConstants.DAT_DIR);
        }
        return soulCollection.getOrDefault(soulItemId, null);
    }


    public static void loadAndroidsFromWz() {
        String wzDir = ServerConstants.WZ_DIR + "/Etc.wz/Android";
        File dir = new File(wzDir);
        for (File file : dir.listFiles()) {
            AndroidInfo ai = new AndroidInfo(Integer.parseInt(file.getName().replace(".img.xml", "")));
            Node node = XMLApi.getAllChildren(XMLApi.getRoot(file)).get(0);
            List<Node> nodes = XMLApi.getAllChildren(node);
            for (Node mainNode : nodes) {
                String mainName = XMLApi.getNamedAttribute(mainNode, "name");
                switch (mainName) {
                    case "costume":
                        for (Node n : XMLApi.getAllChildren(mainNode)) {
                            String nName = XMLApi.getNamedAttribute(n, "name");
                            switch (nName) {
                                case "face":
                                    for (Node inner : XMLApi.getAllChildren(n)) {
                                        ai.addFace(Integer.parseInt(XMLApi.getNamedAttribute(inner, "value")) % 10000);
                                    }
                                    break;
                                case "hair":
                                    for (Node inner : XMLApi.getAllChildren(n)) {
                                        ai.addHair(Integer.parseInt(XMLApi.getNamedAttribute(inner, "value")) % 10000);
                                    }
                                    break;
                                case "skin":
                                    for (Node inner : XMLApi.getAllChildren(n)) {
                                        ai.addSkin(Integer.parseInt(XMLApi.getNamedAttribute(inner, "value")) % 1000);
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
            androidInfo.put(ai.getId(), ai);
        }
    }


    /**
     *  加载跑商数据
     */
    public static void loadNiTradeKillCollectionFromWz() {
//        String wzDir = "E:\\javaguide\\214\\wz\\xml\\wz\\Etc.wz/NihalTrade.img.xml";
        String wzDir = ServerConstants.WZ_DIR + "/Etc.wz/NihalTrade.img.xml";
        File dir = new File(wzDir);
        Node node = XMLApi.getFirstChildByNameBF(XMLApi.getRoot(dir), "NihalTrade.img");
        Node worker = XMLApi.getFirstChildByNameBF(node, "worker");
        Node workList = XMLApi.getFirstChildByNameBF(worker, String.valueOf(TraceKillQuestRxCode.WORKER.getVal()));
        List<Node> workNodes = XMLApi.getAllChildren(workList);
        ArrayList<TraceKillWorker> workers = new ArrayList<>();
        for (Node workNode : workNodes) {
            int index = Integer.valueOf(XMLApi.getNamedAttribute(workNode, "name"));
            NodeList children = workNode.getChildNodes();
            Node firstChild = null;
            for (int i = 0; i < children.getLength(); i++) {
                Node c = children.item(i);
                if (c.getNodeType() == Node.ELEMENT_NODE) {
                    firstChild = c;
                    break;
                }
            }
            int npcID = Integer.valueOf(XMLApi.getNamedAttribute(firstChild, "name"));

            Node speedNode = XMLApi.getFirstChildByNameBF(firstChild, "speedAdd");
            int speedAdd = Integer.valueOf(XMLApi.getNamedAttribute(speedNode, "value"));

            Node jumpNode = XMLApi.getFirstChildByNameBF(firstChild, "jumpAdd");
            int jumpAdd = Integer.valueOf(XMLApi.getNamedAttribute(jumpNode, "value"));

            Node weightNode = XMLApi.getFirstChildByNameBF(firstChild, "weightAdd");
            int weightAdd = Integer.valueOf(XMLApi.getNamedAttribute(weightNode, "value"));

            TraceKillWorker traceKillWorker = new TraceKillWorker(index, npcID, speedAdd, jumpAdd, weightAdd);
            workers.add(traceKillWorker);
        }
        traceKillMap.put(TraceKillQuestRxCode.WORKER.getVal(), workers);


        Node riding = XMLApi.getFirstChildByNameBF(node, "riding");
        ArrayList<TraceKillRiding> ridingList = new ArrayList<>();

        Node riding0 = XMLApi.getFirstChildByNameBF(riding, "0");
        int riding0Value = Integer.valueOf(XMLApi.getNamedAttribute(riding0, "value"));
        ridingList.add(new TraceKillRiding(0, riding0Value));

        Node riding1 = XMLApi.getFirstChildByNameBF(riding, "1");
        int riding1Value = Integer.valueOf(XMLApi.getNamedAttribute(riding1, "value"));
        ridingList.add(new TraceKillRiding(1, riding1Value));


        Node riding2 = XMLApi.getFirstChildByNameBF(riding, "2");
        int riding2Value = Integer.valueOf(XMLApi.getNamedAttribute(riding2, "value"));
        ridingList.add(new TraceKillRiding(2, riding2Value));
        traceKillMap.put(TraceKillQuestRxCode.RIDING.getVal(), ridingList);


        Node item = XMLApi.getFirstChildByNameBF(node, "item");


        Node gold = XMLApi.getFirstChildByNameBF(node, "gold");




//        List<Node> nodes = XMLApi.getAllChildren(node);
//        for (Node mainNode : nodes) {
//            int skillId = 0;
//            int skillIdH = 0;
//            Node soulSkill = XMLApi.getFirstChildByNameBF(mainNode, "soulSkill");
//            Node soulSkillH = XMLApi.getFirstChildByNameBF(mainNode, "soulSkillH");
//            if (soulSkill != null) {
//                skillId = Integer.parseInt(XMLApi.getNamedAttribute(soulSkill, "value"));
//            }
//            if (soulSkillH != null) {
//                skillIdH = Integer.parseInt(XMLApi.getNamedAttribute(soulSkillH, "value"));
//            }
//
//            Node soulList = XMLApi.getFirstChildByNameBF(mainNode, "soulList");
//            List<Node> soulNodes = XMLApi.getAllChildren(soulList);
//            for (int i = 0; i < soulNodes.size(); i++) {
//                Node soulNode = soulNodes.get(i);
//                int soul1 = -1;
//                int soul2 = -1;
//                Node soul1Node = XMLApi.getFirstChildByNameBF(soulNode, "0");
//                Node soul2Node = XMLApi.getFirstChildByNameBF(soulNode, "1");
//                if (soul1Node != null) {
//                    soul1 = Integer.valueOf(XMLApi.getNamedAttribute(soul1Node, "value"));
//                }
//                if (soul2Node != null) {
//                    soul2 = Integer.valueOf(XMLApi.getNamedAttribute(soul2Node, "value"));
//                }
//                int finalSkillId = -1;
//                if (i <= SoulType.Radiant.getVal()) { //normal soul
//                    finalSkillId = skillId;
//                } else { //hard soul
//                    finalSkillId = skillIdH;
//                }
//                BossSoul bs = new BossSoul(finalSkillId, SoulType.getSoulTypeByVal(i));
//                if (finalSkillId != -1) {
//                    if (soul1 != -1) {
//                        soulCollection.put(soul1, bs);
//                    }
//                    if (soul2 != -1) {
//                        soulCollection.put(soul2, bs);
//                    }
//                }
//            }
//        }
    }



    public static void loadSoulCollectionFromWz() {
        String wzDir = ServerConstants.WZ_DIR + "/Etc.wz/SoulCollection.img.xml";
        File dir = new File(wzDir);
        Node node = XMLApi.getFirstChildByNameBF(XMLApi.getRoot(dir), "SoulCollection.img");
        List<Node> nodes = XMLApi.getAllChildren(node);
        for (Node mainNode : nodes) {
            int skillId = 0;
            int skillIdH = 0;
            Node soulSkill = XMLApi.getFirstChildByNameBF(mainNode, "soulSkill");
            Node soulSkillH = XMLApi.getFirstChildByNameBF(mainNode, "soulSkillH");
            if (soulSkill != null) {
                skillId = Integer.parseInt(XMLApi.getNamedAttribute(soulSkill, "value"));
            }
            if (soulSkillH != null) {
                skillIdH = Integer.parseInt(XMLApi.getNamedAttribute(soulSkillH, "value"));
            }

            Node soulList = XMLApi.getFirstChildByNameBF(mainNode, "soulList");
            List<Node> soulNodes = XMLApi.getAllChildren(soulList);
            for (int i = 0; i < soulNodes.size(); i++) {
                Node soulNode = soulNodes.get(i);
                int soul1 = -1;
                int soul2 = -1;
                Node soul1Node = XMLApi.getFirstChildByNameBF(soulNode, "0");
                Node soul2Node = XMLApi.getFirstChildByNameBF(soulNode, "1");
                if (soul1Node != null) {
                    soul1 = Integer.valueOf(XMLApi.getNamedAttribute(soul1Node, "value"));
                }
                if (soul2Node != null) {
                    soul2 = Integer.valueOf(XMLApi.getNamedAttribute(soul2Node, "value"));
                }
                int finalSkillId = -1;
                if (i <= SoulType.Radiant.getVal()) { //normal soul
                    finalSkillId = skillId;
                } else { //hard soul
                    finalSkillId = skillIdH;
                }
                BossSoul bs = new BossSoul(finalSkillId, SoulType.getSoulTypeByVal(i));
                if (finalSkillId != -1) {
                    if (soul1 != -1) {
                        soulCollection.put(soul1, bs);
                    }
                    if (soul2 != -1) {
                        soulCollection.put(soul2, bs);
                    }
                }
            }
        }
    }

    public static void saveSoulCollection(String dir) {
        Util.makeDirIfAbsent(dir);
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(new File(dir + "/" + "soulCollection" + ".dat")))) {
            dataOutputStream.writeInt(soulCollection.size());
            for (Map.Entry<Integer, BossSoul> soulEntry : soulCollection.entrySet()) {
                dataOutputStream.writeInt(soulEntry.getKey());
                dataOutputStream.writeInt(soulEntry.getValue().getSkillId());
                dataOutputStream.writeByte(soulEntry.getValue().getSoulType().getVal());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSoulCollectionFromFile(String dir) {
        Util.makeDirIfAbsent(dir);
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File(dir + "/" + "soulCollection" + ".dat")))) {
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; i++) {
                int itemId = dataInputStream.readInt();
                int skillId = dataInputStream.readInt();
                byte soulType = dataInputStream.readByte();
                soulCollection.put(itemId, new BossSoul(skillId, soulType));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAndroidInfo(String dir) {
        Util.makeDirIfAbsent(dir);
        for (AndroidInfo ai : androidInfo.values()) {
            File file = new File(String.format("%s/%d.dat", dir, ai.getId()));
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
                dos.writeInt(ai.getId());
                dos.writeInt(ai.getHairs().size());
                for (int hair : ai.getHairs()) {
                    dos.writeInt(hair);
                }
                dos.writeInt(ai.getFaces().size());
                for (int face : ai.getFaces()) {
                    dos.writeInt(face);
                }
                dos.writeInt(ai.getSkins().size());
                for (int skin : ai.getSkins()) {
                    dos.writeInt(skin);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static AndroidInfo getAndroidInfoById(int androidId) {
        if (androidInfo.containsKey(androidId)) {
            return androidInfo.get(androidId);
        }
        return loadAndroidInfoFromFile(String.format("%s/etc/android/%d.dat", ServerConstants.DAT_DIR, androidId));
    }

    private static AndroidInfo loadAndroidInfoFromFile(String file) {
        AndroidInfo ai = null;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            ai = new AndroidInfo(dis.readInt());
            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                ai.addHair(dis.readInt());
            }
            size = dis.readInt();
            for (int i = 0; i < size; i++) {
                ai.addFace(dis.readInt());
            }
            size = dis.readInt();
            for (int i = 0; i < size; i++) {
                ai.addSkin(dis.readInt());
            }
            androidInfo.put(ai.getId(), ai);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ai;
    }


    public static void generateDatFiles() {
        log.info("Started generating etc data.");
        Util.makeDirIfAbsent(ServerConstants.DAT_DIR + "/etc");
        long start = System.currentTimeMillis();
        loadAndroidsFromWz();
        loadSoulCollectionFromWz();
        saveSoulCollection(ServerConstants.DAT_DIR);
        saveAndroidInfo(ServerConstants.DAT_DIR + "/etc/android");
        log.info(String.format("Completed generating etc data in %dms.", System.currentTimeMillis() - start));
    }

    public static void clear() {
        androidInfo.clear();
    }

    public static void main(String[] args) {
//        generateDatFiles();
        loadNiTradeKillCollectionFromWz();
    }
}
