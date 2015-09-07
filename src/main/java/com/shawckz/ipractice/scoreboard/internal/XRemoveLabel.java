package com.shawckz.ipractice.scoreboard.internal;

import lombok.Data;

@Data
public class XRemoveLabel {

    private final String value;
    private final String lastValue;
    private final int score;
    private final boolean visible;


}
