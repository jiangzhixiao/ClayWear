package com.umfundi.wear.claywear.model;

import java.util.List;
import com.umfundi.wear.claywear.model.StandScore;

/**
 * Created by ianmolesworth on 22/04/15.
 */
public class ScoreCard {

    private String ownerId;
    private String layoutId;
    private List<StandScore> scorePattern;

    public ScoreCard(){ }

    public ScoreCard(List<StandScore> theList){
        scorePattern = theList;
    }

    public List<StandScore> getScorePattern(){
        return scorePattern;
    }

    public void setOwnerId(String id){
        ownerId = id;
    }

    public String getOwnerId(){
        return ownerId;
    }

    public void setLayoutId(String id){
        layoutId = id;
    }

    public String getlayoutId(){
        return layoutId;
    }

}
