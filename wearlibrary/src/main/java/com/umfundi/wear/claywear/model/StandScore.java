package com.umfundi.wear.claywear.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Map;

public class StandScore {

    private int total;
    private int width;
    private String stand;
    private int scoreformat;

    private int T1 /*,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,T37,T38,T39,T40,T41,T42,T43,T44,T45,T46,T47,T48,T49,T50*/ = 0;

    // perhaps we can make an array of them ????
    // Cant use booleans for targets as we need to cater for ..
    // Not used           = null
    // Shot but not hit   = 0
    // shot and hit       = 1 or 2
    // shot and hit second barrel = 2 or 1!

    // We don't send this array as Jackson seems to be incapable of ignoring the nulls!
    @JsonIgnore
    private Integer[] pips;
    // these equivalent place holders are needed to fool the grid stuff

    private Integer S1,S2,S3,S4,S5,S6,S7,S8,S9,S10,S11,S12,S13,S14,S15,S16,S17,S18,S19,S20,S21,S22,S23,S24,S25,S26,S27,S28,S29,S30,S31,S32,S33,S34,S35,S36,S37,S38,S39,S40,S41,S42,S43,S44,S45,S46,S47,S48,S49,S50;

    public StandScore(){
        calculateTotal();
    }

    /*
    // Default target setting in the boolean
    public StandScore(int b, int targets,int f) {
        width = targets;
        scoreformat = f;
        for (int i = 0; i<targets; i++) pips[i] = b;
        calculateTotal();
    }
*/

    public StandScore(Integer[] p){
        width = p.length;
        pips = p;
        scoreformat = 0;
        calculateTotal();
    }

    // Default target setting in b
    // build a standscore from the stand itself
    public StandScore(int b, Map<Integer, Map> stand, int f) {

        pips = new Integer[50];
        scoreformat = f;

        int os = 0;
        int s = 0;
        int w = 0;
        // iterate through all of the cycles on this stand
        for ( int c = 1; c<=stand.size();c++){
            // cet each cycle in turn
            Map<Integer, ArrayList> aCycle = stand.get(c);
            ArrayList<Integer> cinfo = aCycle.get(0);
            ArrayList<Boolean> ctargets = aCycle.get(1);
            for ( s=0; s<cinfo.get(1)*cinfo.get(2);s++){
                // Map all targets to the cycle targets
                if (ctargets.get(s))
                    pips[os+s] = b;
                else
                    pips[os+s] = 0;
            }
            os=s;
            if (s>w)
                w=s;
        }

        width = w;

        arrayToVals();

        // Surely the total must be equal to the targets input????
        calculateTotal();

    }

    private void arrayToVals(){
        S1  = pips[0];
        if (width > 1)
            S2  = pips[1];
        if (width > 2)
            S3  = pips[2];
        if (width > 3)
            S4  = pips[3];
        if (width > 4)
            S5  = pips[4];
        if (width > 5)
            S6  = pips[5];
        if (width > 6)
            S7  = pips[6];
        if (width > 7)
            S8  = pips[7];
        if (width > 8)
            S9  = pips[8];
        if (width > 9)
            S10 = pips[9];
        if (width > 10)
            S11 = pips[10];
        if (width > 11)
            S12 = pips[11];
        if (width > 12)
            S13 = pips[12];
        if (width > 13)
            S14 = pips[13];
        if (width > 14)
            S15 = pips[14];
        if (width > 15)
            S16 = pips[15];
        if (width > 16)
            S17 = pips[16];
        if (width > 17)
            S18 = pips[17];
        if (width > 18)
            S19 = pips[18];
        if (width > 19)
            S20 = pips[19];
        if (width > 20)
            S21 = pips[20];
        if (width > 21)
            S22 = pips[21];
        if (width > 22)
            S23 = pips[22];
        if (width > 23)
            S24 = pips[23];
        if (width > 24)
            S25 = pips[24];
        if (width > 25)
            S26 = pips[25];
        if (width > 26)
            S27 = pips[26];
        if (width > 27)
            S28 = pips[27];
        if (width > 28)
            S29 = pips[28];
        if (width > 29)
            S30 = pips[29];
        if (width > 30)
            S31 = pips[30];
        if (width > 31)
            S32 = pips[31];
        if (width > 32)
            S33 = pips[32];
        if (width > 33)
            S34 = pips[33];
        if (width > 34)
            S35 = pips[34];
        if (width > 35)
            S36 = pips[35];
        if (width > 36)
            S37 = pips[36];
        if (width > 37)
            S38 = pips[37];
        if (width > 38)
            S39 = pips[38];
        if (width > 39)
            S40 = pips[39];
        if (width > 40)
            S41 = pips[40];
        if (width > 41)
            S42 = pips[41];
        if (width > 42)
            S43 = pips[42];
        if (width > 43)
            S44 = pips[43];
        if (width > 44)
            S45 = pips[44];
        if (width > 45)
            S46 = pips[45];
        if (width > 46)
            S47 = pips[46];
        if (width > 47)
            S48 = pips[47];
        if (width > 48)
            S49 = pips[48];
        if (width > 49)
            S50 = pips[49];
    }

    public void valsToArray(){
        if (pips == null)
            pips = new Integer[50];
        pips[0]=S1;
        if (width > 1)
            pips[1] = S2 ;
        if (width > 2)
            pips[2] = S3 ;
        if (width > 3)
            pips[3] = S4 ;
        if (width > 4)
            pips[4] = S5 ;
        if (width > 5)
            pips[5] = S6 ;
        if (width > 6)
            pips[6] = S7 ;
        if (width > 7)
            pips[7] = S8 ;
        if (width > 8)
            pips[8] = S9 ;
        if (width > 9)
            pips[9] = S10 ;
        if (width > 10)
            pips[10] = S11 ;
        if (width > 11)
            pips[11] = S12 ;
        if (width > 12)
            pips[12] = S13 ;
        if (width > 13)
            pips[13] = S14 ;
        if (width > 14)
            pips[14] = S15 ;
        if (width > 15)
            pips[15] = S16 ;
        if (width > 16)
            pips[16] = S17 ;
        if (width > 17)
            pips[17] = S18 ;
        if (width > 18)
            pips[18] = S19 ;
        if (width > 19)
            pips[19] = S20 ;
        if (width > 20)
            pips[20] = S21 ;
        if (width > 21)
            pips[21] = S22 ;
        if (width > 22)
            pips[22] = S23 ;
        if (width > 23)
            pips[23] = S24 ;
        if (width > 24)
            pips[24] = S25 ;
        if (width > 25)
            pips[25] = S26 ;
        if (width > 26)
            pips[26] = S27 ;
        if (width > 27)
            pips[27] = S28 ;
        if (width > 28)
            pips[28] = S29 ;
        if (width > 29)
            pips[29] = S30 ;
        if (width > 30)
            pips[30] = S31 ;
        if (width > 31)
            pips[31] = S32 ;
        if (width > 32)
            pips[32] = S33 ;
        if (width > 33)
            pips[33] = S34 ;
        if (width > 34)
            pips[34] = S35 ;
        if (width > 35)
            pips[35] = S36 ;
        if (width > 36)
            pips[36] = S37 ;
        if (width > 37)
            pips[37] = S38 ;
        if (width > 38)
            pips[38] = S39 ;
        if (width > 39)
            pips[39] = S40 ;
        if (width > 40)
            pips[40] = S41;
        if (width > 41)
            pips[41] = S42 ;
        if (width > 42)
            pips[42] = S43 ;
        if (width > 43)
            pips[43] = S44 ;
        if (width > 44)
            pips[44] = S45 ;
        if (width > 45)
            pips[45] = S46 ;
        if (width > 46)
            pips[46] = S47 ;
        if (width > 47)
            pips[47] = S48 ;
        if (width > 48)
            pips[48] = S49 ;
        if (width > 49)
            pips[49] = S50 ;    }

    public int calculateTotal() {

        // Build a boolean array and populate it with the individual booleans
        // then add up all the hits.
        // todo this may have to change if we go to an 'unused,not shot, miss, hit system ( UNMH )

        total = 0;
        for (int i = 0; i < width; i++){
            total+=pips[i];
        }
        T1 = total;
        return total;
    }

    // used to work with arrays rather than individual names
    public int getOffsetFromName(String s )
    {
        int v = Integer.parseInt(s.substring(1))-1;
        return v;
    }

    // used to work with arrays rather than individual names
    public String getNameFromOffset(int o )
    {
        String s = "S"+(o+1);
        return s;
    }

    public int getTotal() {
        return total;
    }
    public void setTotal(int tot) {
        this.total = tot;
    }

    public int getT1() { return T1 ; }
    public void setT1(int i) { T1 =i;  }

    public String getStand() { return stand ; }
    public void setStand(String s) { stand = s;  }

    public int getWidth(){ return width; }
    public void setWidth(int w){width = w; }

    public void setScoreFormat(int f) {
        scoreformat = f;
    }
    public int getScoreFormat(){ return scoreformat;}

    public void setPips(Integer[] p){ pips = p; width = p.length;}

    public void setPip(int i, int v){
        pips[i] = v;
    }

    public Integer getPip(int i){
        return pips[i];
    }

    public Integer[] getPips(){
        return pips;
    }

    public Integer getS1 () { return pips[0];  }
    public Integer getS2 () { return pips[1];  }
    public Integer getS3 () { return pips[2];  }
    public Integer getS4 () { return pips[3];  }
    public Integer getS5 () { return pips[4];  }
    public Integer getS6 () { return pips[5];  }
    public Integer getS7 () { return pips[6];  }
    public Integer getS8 () { return pips[7];  }
    public Integer getS9 () { return pips[8];  }
    public Integer getS10() { return pips[9];  }
    public Integer getS11() { return pips[10]; }
    public Integer getS12() { return pips[11]; }
    public Integer getS13() { return pips[12]; }
    public Integer getS14() { return pips[13]; }
    public Integer getS15() { return pips[14]; }
    public Integer getS16() { return pips[15]; }
    public Integer getS17() { return pips[16]; }
    public Integer getS18() { return pips[17]; }
    public Integer getS19() { return pips[18]; }
    public Integer getS20() { return pips[19]; }
    public Integer getS21() { return pips[20]; }
    public Integer getS22() { return pips[21]; }
    public Integer getS23() { return pips[22]; }
    public Integer getS24() { return pips[23]; }
    public Integer getS25() { return pips[24]; }
    public Integer getS26() { return pips[25]; }
    public Integer getS27() { return pips[26]; }
    public Integer getS28() { return pips[27]; }
    public Integer getS29() { return pips[28]; }
    public Integer getS30() { return pips[29]; }
    public Integer getS31() { return pips[30]; }
    public Integer getS32() { return pips[31]; }
    public Integer getS33() { return pips[32]; }
    public Integer getS34() { return pips[33]; }
    public Integer getS35() { return pips[34]; }
    public Integer getS36() { return pips[35]; }
    public Integer getS37() { return pips[36]; }
    public Integer getS38() { return pips[37]; }
    public Integer getS39() { return pips[38]; }
    public Integer getS40() { return pips[39]; }
    public Integer getS41() { return pips[40]; }
    public Integer getS42() { return pips[41]; }
    public Integer getS43() { return pips[42]; }
    public Integer getS44() { return pips[43]; }
    public Integer getS45() { return pips[44]; }
    public Integer getS46() { return pips[45]; }
    public Integer getS47() { return pips[46]; }
    public Integer getS48() { return pips[47]; }
    public Integer getS49() { return pips[48]; }
    public Integer getS50() { return pips[49]; }

    public void setS1  (int b) { S1  = b; valsToArray();}
    public void setS2  (int b) { S2  = b; valsToArray();}
    public void setS3  (int b) { S3  = b; valsToArray();}
    public void setS4  (int b) { S4  = b; valsToArray();}
    public void setS5  (int b) { S5  = b; valsToArray();}
    public void setS6  (int b) { S6  = b; valsToArray();}
    public void setS7  (int b) { S7  = b; valsToArray();}
    public void setS8  (int b) { S8  = b; valsToArray();}
    public void setS9  (int b) { S9  = b; valsToArray();}
    public void setS10 (int b) { S10 = b; valsToArray();}
    public void setS11 (int b) { S11 = b; valsToArray();}
    public void setS12 (int b) { S12 = b; valsToArray();}
    public void setS13 (int b) { S13 = b; valsToArray();}
    public void setS14 (int b) { S14 = b; valsToArray();}
    public void setS15 (int b) { S15 = b; valsToArray();}
    public void setS16 (int b) { S16 = b; valsToArray();}
    public void setS17 (int b) { S17 = b; valsToArray();}
    public void setS18 (int b) { S18 = b; valsToArray();}
    public void setS19 (int b) { S19 = b; valsToArray();}
    public void setS20 (int b) { S20 = b; valsToArray();}
    public void setS21 (int b) { S21 = b; valsToArray();}
    public void setS22 (int b) { S22 = b; valsToArray();}
    public void setS23 (int b) { S23 = b; valsToArray();}
    public void setS24 (int b) { S24 = b; valsToArray();}
    public void setS25 (int b) { S25 = b; valsToArray();}
    public void setS26 (int b) { S26 = b; valsToArray();}
    public void setS27 (int b) { S27 = b; valsToArray();}
    public void setS28 (int b) { S28 = b; valsToArray();}
    public void setS29 (int b) { S29 = b; valsToArray();}
    public void setS30 (int b) { S30 = b; valsToArray();}
    public void setS31 (int b) { S31 = b; valsToArray();}
    public void setS32 (int b) { S32 = b; valsToArray();}
    public void setS33 (int b) { S33 = b; valsToArray();}
    public void setS34 (int b) { S34 = b; valsToArray();}
    public void setS35 (int b) { S35 = b; valsToArray();}
    public void setS36 (int b) { S36 = b; valsToArray();}
    public void setS37 (int b) { S37 = b; valsToArray();}
    public void setS38 (int b) { S38 = b; valsToArray();}
    public void setS39 (int b) { S39 = b; valsToArray();}
    public void setS40 (int b) { S40 = b; valsToArray();}
    public void setS41 (int b) { S41 = b; valsToArray();}
    public void setS42 (int b) { S42 = b; valsToArray();}
    public void setS43 (int b) { S43 = b; valsToArray();}
    public void setS44 (int b) { S44 = b; valsToArray();}
    public void setS45 (int b) { S45 = b; valsToArray();}
    public void setS46 (int b) { S46 = b; valsToArray();}
    public void setS47 (int b) { S47 = b; valsToArray();}
    public void setS48 (int b) { S48 = b; valsToArray();}
    public void setS49 (int b) { S49 = b; valsToArray();}
    public void setS50 (int b) { S50 = b; valsToArray();}
}
