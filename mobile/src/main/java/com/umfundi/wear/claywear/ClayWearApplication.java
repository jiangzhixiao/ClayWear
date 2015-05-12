package com.umfundi.wear.claywear;


import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import com.umfundi.wear.claywear.dao.RoundDataFactory;

import java.util.HashSet;

//import com.umfundi.wear.claywear.dao.DataFactory;

/**
 * Created by ianmolesworth on 06/04/15.
 */
public class ClayWearApplication extends Application {

    public RoundDataFactory df;

    public int currentShooter;

    public int currentStand;
    public int currentCycle;
    public int currentTarget;



    @Override
    public void onCreate(){
        super.onCreate();
        df = RoundDataFactory.getInstance();

        // Target types
        df.SINGLES = getResources().getString(R.string.typeSingles);
        df.REPORT = getResources().getString(R.string.typeReport);
        df.RAF = getResources().getString(R.string.typeRafael);
        df.SIM = getResources().getString(R.string.typeSim);
        df.REPORT3 = getResources().getString(R.string.typeReport3);
        df.SIM3 = getResources().getString(R.string.typeSim3);
        df.RAF3 = getResources().getString(R.string.typeRafael3);
        df.OSIM = getResources().getString(R.string.type1RepSim);
        df.SIMO = getResources().getString(R.string.typeSimRep1);
        df.ORAF = getResources().getString(R.string.type1RepRaf);
        df.RAFO = getResources().getString(R.string.typeRafRep1);
        df.RSIM = getResources().getString(R.string.typeRafSim);

        df.REPPAIRS = getResources().getString(R.string.trgReportPairs);
        df.SIMPAIRS = getResources().getString(R.string.trgSimPairs);
        df.REPTRIPS = getResources().getString(R.string.trgReportTrips);
        df.SIMTRIPS = getResources().getString(R.string.trgSimTrips);

        // Disciplines and their Formats
        df.SPORTING = getResources().getString(R.string.discSporting);

        df.ESP = getResources().getString(R.string.frmEsp);
        df.FIVES = getResources().getString(R.string.frmFives);
        df.FITS = getResources().getString(R.string.frmFits);
        df.SPTR = getResources().getString(R.string.frmSptr);
        df.COPK = getResources().getString(R.string.frmCopk);
        df.SPRA = getResources().getString(R.string.frmSpra);
        df.SUPER = getResources().getString(R.string.frmSuper);

        // todo Currently unused!
        df.FED = getResources().getString(R.string.discFed);

        df.F32 = getResources().getString(R.string.frmF32);
        df.FT3 = getResources().getString(R.string.frmFT3);
        df.FT5 = getResources().getString(R.string.frmFT5);
        df.ISF = getResources().getString(R.string.frmIsf);

        df.HEL = getResources().getString(R.string.discHel);

        df.SKEET = getResources().getString(R.string.discSkeet);

        df.ESK = getResources().getString(R.string.frmEsk);
        df.ASK = getResources().getString(R.string.frmAsk);
        df.SKD = getResources().getString(R.string.frmSkd);
        df.OSK = getResources().getString(R.string.frmOsk);

        df.TRAP = getResources().getString(R.string.discTrap);

        df.DTL = getResources().getString(R.string.frmDtl);
        df.ABT = getResources().getString(R.string.frmAbt);
        df.DRS = getResources().getString(R.string.frmDrs);
        df.OTR = getResources().getString(R.string.frmOtr);
        df.DTR = getResources().getString(R.string.frmDtr);
        df.UT = getResources().getString(R.string.frmUt);
        df.ATA = getResources().getString(R.string.frmAta);
        df.ATD = getResources().getString(R.string.frmAtd);

        df.initArrays();

    }
}
