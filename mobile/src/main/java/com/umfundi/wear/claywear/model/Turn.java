package com.umfundi.wear.claywear.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Turn implements Serializable {
    private static final long serialVersionUID = 2791048111397767820L;

    public List<Integer> turnShots;
    public List<String> targetNames;
    public int firstPattern;

    public Turn()
    {
        turnShots = new ArrayList<Integer>();
        targetNames = new ArrayList<String>();
    }
}
