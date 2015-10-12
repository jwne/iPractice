package com.shawckz.ipractice.scoreboard.practice;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.practice.state.*;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class PracticeScoreboard {

    private XScoreboard scoreboard;
    private IPlayer ip;

    private Set<PracticeBoardType> boards = new HashSet<>();

    public Set<PracticeBoardType> getBoards() {
        return boards;
    }

    public IPlayer getIp() {
        return ip;
    }

    public XScoreboard getScoreboard() {
        return scoreboard;
    }

    public PracticeScoreboard(IPlayer ip) {
        this.ip = ip;
        this.scoreboard = new XScoreboard(
                ChatColor.GOLD+"Practice PvP");//&6Practice PvP

        boards.add(new SpawnBoardType(scoreboard, ip));
        boards.add(new KitBuilderBoardType(scoreboard, ip));
        boards.add(new QueueBoardType(scoreboard, ip));
        boards.add(new PartyBoardType(scoreboard, ip));
        boards.add(new MatchBoardType(scoreboard, ip));
        boards.add(new StaffModeBoardType(scoreboard, ip));

        scoreboard.send(ip.getPlayer());
        scoreboard.update();
        update();
    }

    private String getTitle(String title){
        int split = Math.round(title.length() / 2);
        final String key = title.substring(0, split);
        final String value = title.substring(split, title.length());
        Score score = scoreboard.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(key);
        if (scoreboard.getScoreboard().getEntries().toArray().length != 0) {
            Team team = this.scoreboard.getScoreboard().getTeam(key);
            if (team == null) {
                team = this.scoreboard.getScoreboard().registerNewTeam(key);
                team.addPlayer(Bukkit.getOfflinePlayer(key));
            }
            team.setSuffix(value);
        }
        return score.getEntry();
    }

    public void update(){
        for (PracticeBoardType board : boards) {
            if (!board.isApplicable(ip)) {
                board.remove(scoreboard);
            }
        }
        for (PracticeBoardType board : boards) {
            if (board.isApplicable(ip)) {
                board.update(scoreboard);
            }
        }
    }

}
