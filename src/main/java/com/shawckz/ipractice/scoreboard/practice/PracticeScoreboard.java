package com.shawckz.ipractice.scoreboard.practice;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.label.BasicLabel;
import com.shawckz.ipractice.scoreboard.practice.state.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class PracticeScoreboard {

    private XScoreboard scoreboard;
    private IPlayer ip;

    private Set<PracticeBoardType> boards = new HashSet<>();

    public PracticeScoreboard(IPlayer ip) {
        this.ip = ip;
        this.scoreboard = new XScoreboard(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+"Practice");

        boards.add(new SpawnBoardType(scoreboard, ip));
        boards.add(new KitBuilderBoardType(scoreboard, ip));
        boards.add(new QueueBoardType(scoreboard, ip));
        boards.add(new PartyBoardType(scoreboard, ip));

        scoreboard.send(ip.getPlayer());
        scoreboard.update();
        update();
    }

    public void update(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(Practice.getPlugin(), new Runnable() {
            @Override
            public void run() {
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
        });
    }

}
