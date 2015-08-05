package com.liutianjiao.smartvideosurveillance.data;

public class Node {
    public String nodeName;
    public Device deviceList[];
    public int nodeId;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Device[] getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(Device[] deviceList) {
        deviceList = deviceList;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }
}
