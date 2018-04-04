package com.emilytrabert.minesweeper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Difficulty {

    EASY(9, 10), MEDIUM(16, 40), HARD(22, 99);

    private final int fieldSize;
    private final int mineCount;
}
