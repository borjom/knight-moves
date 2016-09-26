package com.finchmil.chess.models;

/**
 * Created by Vgrigoryev on 23.09.2016.
 */

public class LevelModel {

    private boolean collectBonuses;

    private int[][] boardArray;

    public int[][] getBoardArray() {
        return boardArray;
    }

    public void setBoardArray(int[][] boardArray) {
        this.boardArray = boardArray;
    }

    public boolean getCollectBonuses() {
        return collectBonuses;
    }

    public void setCollectBonuses(boolean collectBonuses) {
        this.collectBonuses = collectBonuses;
    }
}
