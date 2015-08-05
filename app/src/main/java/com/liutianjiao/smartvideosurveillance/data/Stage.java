package com.liutianjiao.smartvideosurveillance.data;

public class Stage {
    public int stageId;
    public String stageName;
    public Node[] nodeList;

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Node[] getNodeList() {
        return nodeList;
    }

    public void setNodeList(Node[] nodeList) {
        this.nodeList = nodeList;
    }
}
