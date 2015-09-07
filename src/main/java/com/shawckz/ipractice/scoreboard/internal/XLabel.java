package com.shawckz.ipractice.scoreboard.internal;

import java.util.Set;

public interface XLabel {

    int getScore();

    void setScore(int score);

    String getValue();

    void setValue(String value);

    boolean isVisible();

    void setVisible(boolean visible);

    String getLastValue();

    boolean isUpdated();

    void setUpdated(boolean updated);

    void update();

    Set<XRemoveLabel> getToRemove();

}
