package com.hust.ewsystem.computmgt.entity;

import java.io.File;
import java.util.Arrays;

/**
 * @BelongsProject: FileSmart
 * @BelongsPackage: com.yssm.filesmart.model
 * @Author: xdy
 * @CreateTime: 2024-06-27  14:40
 * @Description:
 * @Version: 1.0
 */
public class WkFileForm {


    /**
     * 服务地址接口url
     */
    private String url;

    /**
     * 企业编号
     */
    private String companyId;

    /**
     * 用户
     */
    private String userId;

    /**
     * 源文件路径(绝对路径)
     */
    private String inputFilePath;

    /**
     * 目标文件路径(绝对路径)
     */
    private String outputFilePath;

    /**
     * 系统接入账号
     */
    private String username;

    /**
     * 系统接入密码
     */
    private String password;

    private File file;

    private byte[] saveFile;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public byte[] getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(byte[] saveFile) {
        this.saveFile = saveFile;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WkFileForm() {
    }

    @Override
    public String toString() {
        return "WkFileForm{" +
                "url='" + url + '\'' +
                ", companyId='" + companyId + '\'' +
                ", userId='" + userId + '\'' +
                ", inputFilePath='" + inputFilePath + '\'' +
                ", outputFilePath='" + outputFilePath + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", file=" + file +
                ", saveFile=" + Arrays.toString(saveFile) +
                '}';
    }
}
