package com.emilytrabert.minesweeper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
public class Space {
    private final int row, col;
    private int nearby;
    @Setter
    private boolean mine, show, flagged, lastMine;

    Space(boolean m, int n, boolean s, boolean f, int r, int c, boolean l) {
        mine = m;
        nearby = n;
        show = s;
        flagged = f;
        row = r;
        col = c;
        lastMine = l;
    }

    public void incrementNearby() {
        nearby += 1;
    }
}
