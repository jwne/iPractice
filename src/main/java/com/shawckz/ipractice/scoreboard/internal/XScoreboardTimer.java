package com.shawckz.ipractice.scoreboard.internal;

import com.shawckz.ipractice.Practice;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.io.netty.util.internal.ConcurrentSet;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

public abstract class XScoreboardTimer implements XLabel {

    @Getter
    private final XScoreboard scoreboard;
    @Getter
    protected int score;
    @Getter
    protected String value;
    @Getter
    @Setter
    private boolean visible = true;
    @Getter
    @Setter
    protected boolean frozen = false;
    @Getter
    protected String lastValue = value;
    @Getter
    @Setter
    protected boolean updated = false;
    @Getter
    @Setter
    private double time;
    @Getter
    private int updateFrequency;
    @Getter
    private boolean running = false;
    @Getter
    private Set<XRemoveLabel> toRemove = new ConcurrentSet<>();
    protected BukkitTask task = null;

    public XScoreboardTimer(XScoreboard scoreboard, int score, String value, double time, int updateFrequency) {
        this.scoreboard = scoreboard;
        this.score = score;
        this.value = value;
        this.time = time;
        this.updateFrequency = updateFrequency;
        this.lastValue = value;
    }

    public final void start() {
        if (task != null) {
            task.cancel();
            running = false;
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                doRun();
            }
        }.runTaskTimer(Practice.getPlugin(), updateFrequency, updateFrequency);
        running = true;
    }

    public final void stop() {
        if (running) {
            task.cancel();
            running = false;
        }
    }

    public abstract void onUpdate();//called whenever the timer ticks

    public abstract void complete();//called when the timer is complete

    public abstract void updateTime();

    public abstract boolean isComplete();

    public final void update() {//Actually updates the scoreboard label in the xscoreboard
        scoreboard.updateLabel(this);
    }

    public final void setValue(String value) {
        if (this.value != null) {
            this.lastValue = this.value;
        } else {
            this.lastValue = value;
        }
        this.value = value;
        toRemove.add(new XRemoveLabel(this.value, this.lastValue, this.score, this.visible));
    }

    public final void setScore(int score) {
        scoreboard.updateScore(this, score, this.score);
        this.score = score;
    }

    private void doRun() {
        if (isFrozen()) {
            return;
        }

        if (isComplete()) {
            complete();
            task.cancel();
            running = false;
            return;
        }

        updateTime();
        onUpdate();//let the extending class update the value
        update();//update the value on the scoreboard
    }
}
