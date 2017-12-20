package com.qa.demo.dataStructure;
/**
 * Created by Devin Hua on 2017/8/30.
 * 系统中表示证据及相关信息的数据结构；
 */

public class Evidence {

    //表示证据的文本字符串;
    private String evidenceString;

    //表示证据的来源;
    private DataSource evidenceSource;

    //表示证据支持答案的得分;
    private double evidenceScore;

    public String getEvidenceString() {
        return evidenceString;
    }

    public void setEvidenceString(String evidenceString) {
        this.evidenceString = evidenceString;
    }

    public DataSource getEvidenceSource() {
        return evidenceSource;
    }

    public void setEvidenceSource(DataSource evidenceSource) {
        this.evidenceSource = evidenceSource;
    }

    public double getEvidenceScore() {
        return evidenceScore;
    }

    public void setEvidenceScore(double evidenceScore) {
        this.evidenceScore = evidenceScore;
    }
}
