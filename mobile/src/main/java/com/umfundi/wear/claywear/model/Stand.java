package com.umfundi.wear.claywear.model;

import java.io.Serializable;

public class Stand implements Serializable {

    // Stand allows us to track how many targets are shot on each stand
        public int standTargets;

        public Stand()
        {
            standTargets = 0;
        }
}
