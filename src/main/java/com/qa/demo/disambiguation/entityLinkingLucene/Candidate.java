package com.qa.demo.disambiguation.entityLinkingLucene;

public class Candidate{


    private String mentionname;
    private String name;
    private double score;


    private double popular;
    private double similarityScore;

    public String getMentionname() {
        return mentionname;
    }

    public void setMentionname(String mentionname) {
        this.mentionname = mentionname;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public double getScore()
    {
        return score;
    }
    public void setScore(double score)
    {
        this.score = score;
    }
    public double getPopular() {
        return popular;
    }

    public void setPopular(double popular) {
        this.popular = popular;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }
}
