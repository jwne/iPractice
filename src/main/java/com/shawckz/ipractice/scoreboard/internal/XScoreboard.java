package com.shawckz.ipractice.scoreboard.internal;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class XScoreboard {

    @Getter
    @Setter
    private String title;
    @Getter private Scoreboard scoreboard;
    private Map<Integer, XLabel> scores;

    public XScoreboard(String title) {
        this.title = title;
        this.scores = new HashMap<>();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scoreboard.registerNewObjective(filterTitle(), "dummy");
        this.scoreboard.getObjective(filterTitle()).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.scoreboard.getObjective(filterTitle()).setDisplayName(title);
    }

    public XScoreboard(String title, boolean filt) {
        this.title = title;
        this.scores = new HashMap<>();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scoreboard.registerNewObjective(filterTitle(), "dummy");
        this.scoreboard.getObjective(filterTitle()).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.scoreboard.getObjective(filterTitle()).setDisplayName(title);
    }

    public void addLabel(XLabel label) {
        addScore(label.getScore(), label);
        label.setUpdated(false);
        label.update();
    }

    public void removeLabel(XLabel label) {
        resetScores(label);
        if (scores.containsKey(label.getScore())) {
            scores.remove(label.getScore());
        }
        int split = Math.round(label.getValue().length() / 2);
        final String key = label.getValue().substring(0, split);
        final String value = label.getValue().substring(split, label.getValue().length());
        scoreboard.resetScores(key);
        scoreboard.resetScores(value);
        Team team = scoreboard.getTeam(key);
        if (team != null) {
            team.unregister();
        }
    }

    public void updateLabel(XLabel label) {
        resetScores(label);
        if (label.isVisible()) {
            if (label.getValue().equals("")) {
                return;
            }
            if (label.getValue().equals("§a ")) {
                // Is a spacer label, no need to make a team and shit for it as teams
                // don't seem to like the name only being a space and color
                scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(label.getValue()).setScore(label.getScore());
            } else {
                int split = Math.round(label.getValue().length() / 2);
                final String key = label.getValue().substring(0, split);
                if (key.equals("")) {
                    return;
                }
                final String value = label.getValue().substring(split, label.getValue().length());
                scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(key).setScore(label.getScore());
                if (scoreboard.getEntries().toArray().length != 0) {
                    Team team = this.scoreboard.getTeam(key);
                    if (team == null) {
                        team = this.scoreboard.registerNewTeam(key);
                        team.addPlayer(new FakeOfflinePlayer(key));
                    }
                    team.setSuffix(value);
                }
            }
        }
        scores.put(label.getScore(), label);
        label.setUpdated(true);
    }

    public XLabel getLabel(int score) {
        return scores.get(score);
    }

    public void send(Player pl) {
        pl.setScoreboard(scoreboard);
    }

    public void update() {
        updateTitle();
        for (XLabel label : scores.values()) {
            updateLabel(label);
        }
    }

    public void updateTitle() {
        if (!this.title.equalsIgnoreCase(scoreboard.getObjective(DisplaySlot.SIDEBAR).getDisplayName())) {
            scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(title);
        }
    }

    public boolean hasScore(int score) {
        return scores.containsKey(score);
    }

    public void updateScore(XLabel label, int to, int from) {
        if (scores.containsKey(from)) {
            scores.remove(from);
        }
        scores.put(to, label);
        removeLabel(label);
        addLabel(label);
    }


    public boolean hasLabel(XLabel label) {
        for (XLabel l : scores.values()) {
            if (l.equals(label)) {
                return true;
            }
        }
        return false;
    }

    private void addScore(int score, XLabel value) {
        scores.put(score, value);
    }

    private void resetScores(XLabel label) {
        for (XRemoveLabel remove : label.getToRemove()) {
            scoreboard.resetScores(remove.getLastValue());
            scoreboard.resetScores(remove.getValue());
            if (remove.getLastValue() != null && remove.getValue().length() > remove.getLastValue().length()) {
                int split = Math.round(remove.getValue().length() / 2) - 1;
                final String key = remove.getValue().substring(0, split);
                final String value = remove.getValue().substring(split, remove.getValue().length());
                scoreboard.resetScores(key);
                scoreboard.resetScores(value);
            } else if (remove.getLastValue() != null && remove.getValue().length() < remove.getLastValue().length()) {
                int split = Math.round(remove.getValue().length() / 2) + 1;
                final String key = remove.getValue().substring(0, split);
                final String value = remove.getValue().substring(split, remove.getValue().length());
                scoreboard.resetScores(key);
                scoreboard.resetScores(value);
            } else {
                int split = Math.round(remove.getValue().length() / 2);
                final String key = remove.getValue().substring(0, split);
                final String value = remove.getValue().substring(split, remove.getValue().length());
                scoreboard.resetScores(key);
                scoreboard.resetScores(value);
            }
        }
        label.getToRemove().clear();
    }

    private String filterTitle() {
        return ChatColor.stripColor((title.length() > 16 ? title.substring(0, 15) : title));
    }

}
