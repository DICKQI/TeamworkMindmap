package com.aqinn.mobilenetwork_teamworkmindmap.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Conf;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Aqinn
 * @date 2020/6/16 12:49 PM
 */
public class FileUtil {

    /***********
     * 单例模式 *
     ***********/
    private FileUtil() {
    }

    public static FileUtil getInstance() {
        return FileUtil.Inner.instance;
    }

    private static class Inner {
        private static final FileUtil instance = new FileUtil();
    }

    /******************************
     * 只需要调用一次的创建文件夹代码 *
     ******************************/
    public void createAppDirectory() {
        if (hansSDCard()) {
            String map_path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION;
            File mainDir = new File(map_path);
            if (!mainDir.exists()) {
                mainDir.mkdirs();
            }
            createContentDirectory();
            createTempDirectory();
        } else {
            System.out.println("createAppDirectory: 没有SD卡");
        }
    }
    private void createContentDirectory() {
        String path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
        File contentDir = new File(path);
        if (!contentDir.exists()) {
            contentDir.mkdirs();
        }
    }
    private void createTempDirectory() {
        String path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.TEMP_FILE_LOCATION;
        File tempDir = new File(path);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }


    // 下面的代码暂时没用



























    /***********************
     * 需要多次调用的文件操作 *
     ***********************/


    public TreeModel<String> readTreeObject(String filePath) throws IOException, ClassNotFoundException, InvalidClassException {
        TreeModel<String> tree;
        FileInputStream fos = new FileInputStream(filePath);
        ObjectInputStream ois = new ObjectInputStream(fos);
        tree = (TreeModel<String>) ois.readObject();
        return tree;
    }



    /***************************
     * 后期需要用到的配置文件操作 *
     *************************/
    private void writeFile(String path, String fileContent) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(fileContent.getBytes("UTF-8"));
        fos.close();
    }

//    public void writeContent(Object object) {
//        try {
//            String contentPath = Environment.getExternalStorageDirectory().getPath() +
//                    PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
//            writeTreeObject(contentPath, object);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void writeConf(Conf conf) {
        try {
            String confPath = Environment.getExternalStorageDirectory().getPath() +
                    PublicConfig.MINDMAPS_FILE_LOCATION +
                    PublicConfig.TEMP_FILE_LOCATION + PublicConfig.CONFIG_FILE;
            writeFile(confPath, conf.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private String readFile(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = fis.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        fis.close();
        baos.close();
        return baos.toString();
    }

    public boolean hansSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

}
