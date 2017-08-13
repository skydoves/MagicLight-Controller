package com.skydoves.magiclight_ble_control.otto;

import com.squareup.otto.Bus;

/**
 * Created by skydoves on 2017-07-01.
 */

public final class BusProvider {
    private static final Bus BUS = new Bus();

    private BusProvider() {
    }

    public static Bus getInstance() {
        return BUS;
    }
}