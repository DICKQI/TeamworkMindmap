package com.aqinn.mobilenetwork_teamworkmindmap.config;

import com.aqinn.mobilenetwork_teamworkmindmap.R;

/**
 * @author Aqinn
 * @date 2020/6/15 1:31 PM
 */
public class PublicConfig {

    public static final String MINDMAPS_FILE_LOCATION = "/TWMindMaps/";
    public static final String TEMP_FILE_LOCATION = "twmm_temp/";
    public static final String CONTENT_LOCATION = "twmm_content/";
    public static final String CONFIG_FILE = "conf.txt";

    public static final String url_delete_closeShare(Long shareId) {
        return "http://49.234.71.210/map/close/" + shareId + "/";
    }

    /**
     * 删除在线导图
     * @param shareId
     * @return
     */
    public static final String url_delete_deleteMindmap(Long shareId) {
        return "http://49.234.71.210/map/" + shareId + "/";
    }

    /**
     * 加入导图协作
     *
     * @param shareId
     * @return
     */
    public static final String url_post_joinTeamWorkMindmap(final Long shareId) {
        return "http://49.234.71.210/map/join/" + shareId + "/";
    }

    /**
     * 再次开启共享同步导图
     *
     * @param shareId
     * @return
     */
    public static final String url_put_shareOnAgain(final Long shareId) {
        return "http://49.234.71.210/map/" + shareId + "/";
    }

    /**
     * 获取导图列表
     *
     * @return
     */
    public static final String url_get_getMindmapList() {
        return "http://49.234.71.210/map/list/";
    }

    /**
     * 删除指定节点及其子节点
     *
     * @param shareId
     * @param nodeId
     * @return
     */
    public static final String url_delete_deleteNodeAndSubNode(final Long shareId, final Long nodeId) {
        return "http://49.234.71.210/map/" + shareId + "/mod/" + nodeId + "/";
    }


    /**
     * 修改节点信息
     *
     * @param shareId
     * @param nodeId
     * @return
     */
    public static final String url_put_editNode(final Long shareId, final Long nodeId) {
        return "http://49.234.71.210/map/" + shareId + "/mod/" + nodeId + "/";
    }

    /**
     * 新增节点
     *
     * @param shareId
     * @param parentNodeId
     * @return
     */
    public static final String url_post_addNode(final Long shareId, final Long parentNodeId) {
        return "http://49.234.71.210/map/" + shareId + "/add/" + parentNodeId + "/";
    }

    /**
     * 获取在线导图详情
     *
     * @param shareId
     * @return
     */
    public static final String url_get_getMindmapDetail(final Long shareId) {
        return "http://49.234.71.210/map/" + shareId + "/";
    }

    /**
     * 创建在线导图/本地首次开启共享
     *
     * @return
     */
    public static final String url_post_firstShareOn() {
        return "http://49.234.71.210/map/new/";
    }

    /**
     * 登出账户
     * @return
     */
    public static final String url_delete_logout() {
        return "http://49.234.71.210/user/";
    }

    /**
     * 登录账户
     * @return
     */
    public static final String url_post_login() {
        return "http://49.234.71.210/user/";
    }

    /**
     * 验证登录
     * @return
     */
    public static final String url_get_verifyLogin() {
        return "http://49.234.71.210/user/";
    }

    /**
     * 注册账户
     * @return
     */
    public static final String url_post_register() {
        return "http://49.234.71.210/user/register/";
    }

    /**
     * 获取用户信息
     * @return
     */
    public static final String url_get_dashboard() {
        return "http://49.234.71.210/user/dashboard/";
    }

    /**
     * 更改密码
     * @return
     */
    public static final String url_post_dashboard() {
        return "http://49.234.71.210/user/dashboard/";
    }

    /**
     * 更改个人信息
     * @return
     */
    public static final String url_put_dashboard() {
        return "http://49.234.71.210/user/dashboard/";
    }

    /**
     * 获取头像
     * @return
     */
    public static final String url_get_head() {
        return "http://49.234.71.210/user/head/";
    }

    /**
     * 更新头像
     * @return
     */
    public static final String url_put_head() {
        return "http://49.234.71.210/user/head/";
    }

}