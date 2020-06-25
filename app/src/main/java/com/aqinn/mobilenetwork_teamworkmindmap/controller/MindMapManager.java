package com.aqinn.mobilenetwork_teamworkmindmap.controller;

import android.os.Environment;

import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DBUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.FileUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author Aqinn
 * @date 2020/6/24 1:06 PM
 * MindMap管理器 单例模式
 */
public class MindMapManager {

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

    public List<Mindmap> getAllMindmap() {
        return DBUtil.queryAllMindmap();
    }

    /**
     * 创建Mindmap，并通知主页列表更新
     *
     * @param name
     * @return
     */
    public Mindmap createMindmap(String name) {
        Mindmap mm = new Mindmap(name);
        mm.setPwd("");
        mm.setShareId(null);
        mm.setShareOn(0);
        mm.setMmId(System.currentTimeMillis());
        final NodeModel<String> teamwork_mindmap = new NodeModel<>(name);
        TreeModel<String> tree = new TreeModel<>(teamwork_mindmap);
        tree.addNode(teamwork_mindmap);
        mm.setTm(tree);
        long res = DBUtil.insertMindmap(mm);
        boolean flag = saveMindmap(mm);
        if (!flag)
            return null;
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
    public boolean deleteMindmao(Long mmId) {
        int res = DBUtil.deleteToDoItem(mmId);
        if (res == 0)
            return false;
        return true;
    }


    public boolean saveTree(Long id, TreeModel tm) {
        String path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
        try {
            File f = new File(path + String.valueOf(id) + ".twmm");
            if (!f.exists()){
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

    private void writeTreeObject(String filePath, Object object) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
    }

}
