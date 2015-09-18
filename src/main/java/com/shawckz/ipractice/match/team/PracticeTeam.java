package com.shawckz.ipractice.match.team;

import lombok.Data;

/**
 * Created by 360 on 9/7/2015.
 */
@Data
public class PracticeTeam {

    private final String name;
    private final Team spawn;
    private boolean eliminated = false;

}
