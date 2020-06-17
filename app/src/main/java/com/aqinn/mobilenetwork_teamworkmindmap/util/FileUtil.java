package com.aqinn.mobilenetwork_teamworkmindmap.util;

import android.os.Environment;
import android.util.Log;

import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Conf;

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

    public void createAppDirectory() {
        if (hansSDCard()) {
            String map_path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION;
            File mainDir = new File(map_path);
            if (!mainDir.exists()) {
                mainDir.mkdirs();
            }
        } else {
            System.out.println("createAppDirectory: 没有SD卡");
        }
    }

    public void createContentDirectory() {
        if (hansSDCard()) {
            String path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
            File contentDir = new File(path);
            if (!contentDir.exists()) {
                contentDir.mkdirs();
            }
        } else {
            System.out.println("createTempDirectory: 没有SD卡");
        }
    }

    public void createTempDirectory() {
        if (hansSDCard()) {
            String path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.TEMP_FILE_LOCATION;
            File tempDir = new File(path);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
        } else {
            System.out.println("createTempDirectory: 没有SD卡");
        }
    }

    public void writeContent(Object object) {
        try {
            String contentPath = Environment.getExternalStorageDirectory().getPath() +
                    PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
            writeTreeObject(contentPath, object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTreeObject(String filePath, Object object) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
    }

    public TreeModel<String> readTreeObject(String filePath) throws IOException, ClassNotFoundException, InvalidClassException {
        TreeModel<String> tree;
        FileInputStream fos = new FileInputStream(filePath);
        ObjectInputStream ois = new ObjectInputStream(fos);
        tree = (TreeModel<String>) ois.readObject();
        return tree;
    }

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

    private void writeFile(String path, String fileContext) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(fileContext.getBytes("UTF-8"));
        fos.close();
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
