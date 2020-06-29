package com.aqinn.mobilenetwork_teamworkmindmap.controller;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.http.MyHttpPost;
import com.aqinn.mobilenetwork_teamworkmindmap.http.RespMsg;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DBUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.FileUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.HttpUtils;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Aqinn
 * @date 2020/6/24 1:06 PM
 * MindMap管理器 单例模式
 */
public class MindMapManager {

    // 文件操作 单例模式
    private FileUtil fileUtil = FileUtil.getInstance();
    // json转tw的中间变量
    private JSONArray tmsja = new JSONArray();
    private static final String TAG = "MindMapManager";

    /***********
     * 单例模式 *
     ***********/
    private MindMapManager() {
    }

    public static MindMapManager getInstance() {
        return Inner.instance;
    }

    private static class Inner {
        private static final MindMapManager instance = new MindMapManager();
    }

    /**
     * 随机生成唯一的nodeId
     * @param mmId TODO 这个参数暂时是多余的，因为本来是想着在这个方法里去请求后台，
     *              但是介于更新UI等问题，现选择把该方法当做关协作情况下的专用生成nodeId方法
     * @return
     */
    public Long getNewNodeId(Long mmId) {
        //判断如果mmId是关协作的话，直接返回当前系统时间
//        Mindmap mm = DBUtil.queryMindmapByMmId(mmId);
//        if (mm.getShareOn() == 0) {
//            return System.currentTimeMillis();
//        } else {
//            // 请求后台，返回实际的节点ID
//            return System.currentTimeMillis();
//        }
        return System.currentTimeMillis();
    }

    /**
     * 获取用户的思维导图列表
     * TODO 现阶段的逻辑较为简单，有待改进。
     * 目前是先在`tb_Mindmap`获取所有用户在本地数据库的思维导图，
     * 再从`tb_user_own_mindmap`获取该用户所拥有的所有Mindmap的mmId，再逐个取出
     * @param userId
     * @return
     */
    public List<Mindmap> getUserAllMindmap(Long userId) {
        List<Mindmap> allMindmap = getAllMindmap();
        List<Long> longList = DBUtil.getUserOwnMindmap(userId);
        List<Mindmap> userMindmap = new ArrayList<>();
        for (int i = 0; i < allMindmap.size(); i++) {
            for (int j = 0; j < longList.size(); j++) {
                if (longList.get(j).equals(allMindmap.get(i).getMmId())) {
                    userMindmap.add(allMindmap.get(i));
                    break;
                }
            }
        }
        return userMindmap;
    }

    /**
     * 获取本地数据库中所有的Mindmap
     * @return
     */
    public List<Mindmap> getAllMindmap() {
        return DBUtil.queryAllMindmap();
    }

    /**
     * 根据mmId从本地数据库中获取所有思维导图的列表
     * @param mmId
     * @return
     */
    public Mindmap getMindmapByMmId(Long mmId) {
        return DBUtil.queryMindmapByMmId(mmId);
    }

    /**
     * JSON格式的树转成TreeModel
     * 办法太low了，后期有时间得改
     *
     * @param json JSON样例
     *             {
     *             "node": [
     *             {
     *             "content": "A",
     *             "pid": 0,
     *             "nid": 1
     *             },
     *             {
     *             "content": "B",
     *             "pid": 1,
     *             "nid": 2
     *             },
     *             {
     *             "content": "C",
     *             "pid": 1,
     *             "nid": 3
     *             },
     *             {
     *             "content": "D",
     *             "pid": 5,
     *             "nid": 4
     *             },
     *             {
     *             "content": "E",
     *             "pid": 2,
     *             "nid": 5
     *             }
     *             ],
     *             "shareId": "1"
     *             }
     * @return
     */
    public TreeModel<String> json2tm(String json) {
        JSONObject jo = JSONObject.parseObject(json);
        Long shareId = jo.getLong("shareId");
        JSONArray ja = jo.getJSONArray("node");
        List<NodeModel<String>> nmsl = new ArrayList<>();
        for (int i = 0; i < ja.size(); i++) {
            JSONObject tempJo = (JSONObject) ja.get(i);
            NodeModel<String> nm = new NodeModel<>(tempJo.getString("content"));
            nm.setMnId(tempJo.getLong("id"));
            nm.setpId(tempJo.getLong("parent_node"));
            nmsl.add(nm);
        }
        NodeModel nmroot = new NodeModel("根节点出错");
        for (int i = 0; i < nmsl.size(); i++) {
            if (nmsl.get(i).pId == 0L)
                nmroot = nmsl.get(i);
            for (int j = 0; j < nmsl.size(); j++) {
                if (nmsl.get(i) == nmsl.get(j))
                    continue;
                if (nmsl.get(i).getMnId() == nmsl.get(j).getpId()) {
                    nmsl.get(i).getChildNodes().add(nmsl.get(j));
                    nmsl.get(j).setParentNode(nmsl.get(i));
                }
            }
        }
        if ("根节点出错".equals(String.valueOf(nmroot.getValue())))
            return null;
        TreeModel<String> tm = new TreeModel<>(nmroot);
        tm.setShareId(shareId);
        return tm;
    }

    /**
     * TreeModel转成后台请求所需格式的JSON
     * @param tms
     * @return
     */
    public String tm2json(TreeModel<String> tms) {
        return nm2json(tms.getRootNode());
    }

    /**
     * NodeModel转JSON
     * @param nm
     * @return
     */
    public String nm2json(NodeModel<String> nm) {
        tmsja.clear();
        nm2jo(nm);
        return tmsja.toJSONString();
    }

    /**
     * NodeModel转JSONObject
     * @param nms
     */
    private void nm2jo(NodeModel<String> nms) {
        JSONObject jo = new JSONObject();
        jo.put("nodeId", nms.getnId());
        jo.put("parent_node", nms.getpId());
        jo.put("content", nms.getValue());
        tmsja.add(jo);
        for (NodeModel<String> cnm : nms.getChildNodes()) {
            nm2jo(cnm);
        }
    }

    /**
     * 创建Mindmap，并通知主页列表更新
     *
     * @param userId
     * @param name
     * @return
     */
    public Mindmap createMindmap(Long userId, Long ownerId, String name) {
        Mindmap mm = new Mindmap(name);
        mm.setPwd("");
        mm.setShareId(-1L);
        mm.setShareOn(0);
        mm.setMmId(System.currentTimeMillis());
        mm.setOwnerId(ownerId);
        final NodeModel<String> teamwork_mindmap = new NodeModel<>(name);
        teamwork_mindmap.setnId(System.currentTimeMillis());
        teamwork_mindmap.setpId(0L);
        TreeModel<String> tree = new TreeModel<>(teamwork_mindmap);
        tree.addNode(teamwork_mindmap);
        mm.setTm(tree);
        long res1 = DBUtil.insertMindmap(mm);
        Log.d(TAG, "mmm创建思维导图时存入tb_mindmap的操作结果 => " + res1);
        long res2 = DBUtil.insertUserOwnMindmap(userId, mm.getMmId());
        boolean flag = saveMindmap(mm);
        if (!flag || res1 == -1L || res2 == -1)
            return null;
        Log.d(TAG, "思维导图创建成功");
        return mm;
    }

    /**
     * 保存Mindmap
     * 1.保存导图信息到数据库
     * 2.保存导图文件至本地
     *
     * @param mm
     * @return
     */
    public boolean saveMindmap(Mindmap mm) {
        if (!saveTree(mm.getMmId(), mm.getTm()))
            return false;
        return true;
    }

    /**
     * 根据mmId删除Mindmap
     *
     * @param mmId
     * @return
     */
    public boolean deleteMindmap(Long mmId) {
        int res = DBUtil.deleteMindmap(mmId);
        if (res == 0)
            return false;
        String path = Environment.getExternalStorageDirectory().getPath()
                + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
        if (!fileUtil.deleteFile(path + String.valueOf(mmId) + ".twmm"))
            return false;
        return true;
    }

    /**
     * 保存TreeModel到文件系统
     * @param id
     * @param tm
     * @return
     */
    public boolean saveTree(Long id, TreeModel tm) {
        String path = Environment.getExternalStorageDirectory().getPath()
                + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
        try {
            File f = new File(path + String.valueOf(id) + ".twmm");
            if (!f.exists()) {
                System.out.println(f.getPath());
                f.createNewFile();
            }
            writeTreeObject(path + String.valueOf(id) + ".twmm", tm);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存Object（TreeModel）
     * 私有方法
     * @param filePath
     * @param object
     * @throws IOException
     */
    private void writeTreeObject(String filePath, Object object) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
    }

    /**
     * 思维导图第一次开协作
     * @param mmId
     * @param shareId
     * @return
     */
    public int mindmapFirstShareOn(Long mmId, Long shareId) {
        Mindmap mm = getMindmapByMmId(mmId);
        mm.setShareOn(1);
        mm.setShareId(shareId);
        return DBUtil.updateMindmap(mm, 0, 1, 1, 1);
    }

    /**
     * 思维导图非第一次开协作
     * @param mmId
     * @return
     */
    public int mindmapShareOn(Long mmId) {
        Mindmap mm = getMindmapByMmId(mmId);
        mm.setShareOn(1);
        return DBUtil.updateMindmap(mm, 0, 0, 1, 1);
    }

    /**
     * 思维导图关协作
     * @param mmId
     * @return
     */
    public int mindmapShareOff(Long mmId) {
        Mindmap mm = getMindmapByMmId(mmId);
        mm.setShareOn(0);
        return DBUtil.updateMindmap(mm, 0, 0, 1, 1);
    }


    /********************
     * 目前不怎么用得到的方法 *
     *******************/

    /**
     * TODO 在程序后台定时运行的方式，定时从网上获取最新的数据
     * @param shareId
     * @return
     */
    public TreeModel<String> updateTreeModelByShareId(Long shareId) {
        String res = "";
        String json = HttpUtils.post("科科的URL");
        // 中间不知道需要经过什么处理
        return json2tm(res);
    }

    /*
     * 1.上传本地版本并获取shareId
     * 2.根据shareId获取云端版本
     */
    public Long uploadLocalVersionTreeModel() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                MyHttpPost post = new MyHttpPost("科科的URL");
                Map<String, String> params = new HashMap<>();
                params.put("treeModel", "???");
                RespMsg msg = post.req(params);
                final String respCodeMsg = msg.getRespCodeMsg();
                final String respBody = msg.getRespBody();
            }
        });
        t.start();
        return 0L;
    }

    /**
     * 添加导图的拥有者信息
     * @param userId
     * @param mmId
     * @param ownerId
     * @return
     */
    public Long insertUserOwnMindmap(Long userId, Long mmId, Long ownerId) {
        return DBUtil.insertUserOwnMindmap(userId, mmId);
    }

    /**
     * 修改导图的拥有者信息
     * @param mmId
     * @return
     */
    public static int removeUserOwnMindmap(Long mmId) {
        return DBUtil.removeUserOwnMindmap(mmId);
    }

}
