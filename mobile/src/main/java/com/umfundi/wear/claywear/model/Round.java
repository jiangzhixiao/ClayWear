package com.umfundi.wear.claywear.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ianmolesworth on 09/04/15.
 */

public class Round {

    private String roundID;                // This needs to be universally unique ( perhaps tied to the PC or device that created it, so a time/date and UUID )
    private String lockState;               // Locked prevents an uploaded Round from being modified by this user - fixed by round/competition owner?
    // TODO - a scheme to unlock!
    private String description;             // A descriptive string.

    // points to the active layout in the round.
    private int activeLayout;

    // A round may contain several layouts.
    private Map<Integer, Object> layouts;

    private Map<Integer, Map> roundScores;

    public Round(){
        // We need a holder for layouts
        layouts = new LinkedHashMap<Integer, Object>();
        activeLayout = 0;
        // Build a holder for the scores on this round
        roundScores = new LinkedHashMap<Integer,Map>();
        Map<Integer, Object> layoutScores = new LinkedHashMap<Integer, Object>();
        roundScores.put(1,layoutScores);
    }

    public void addLayout(int n, Layout theLayout){
        layouts.put(n, theLayout);
    }

    public int getActiveLayoutIdx() {
        return activeLayout;
    }

    public void setActiveLayoutIdx(int idx) {
        activeLayout = idx;
    }

    public Map<Integer, Object> getLayouts(){
        return layouts;
    }

    //
    // Add a scorecard for a shooter to score a layout.
    //
    public void addScoreCard(int layout, int shooter, ScoreCard card){
        Map<Integer, Object> theLayoutScores = roundScores.get(layout);

        theLayoutScores.put(shooter, card);
 //       roundScores.get(layout).put(shooter,card);
    }

    public Object getScoreCard(int layout, int shooter){
        return roundScores.get(layout).get(shooter);
    }



}
