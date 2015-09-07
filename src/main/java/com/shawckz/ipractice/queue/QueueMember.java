package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.player.IPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created by 360 on 09/05/2015.
 */

/**
 * The QueueMember class, just a simple class to combine a CPlayer with a KDRange
 * in order to make the queue system a lot easier.
 */
@RequiredArgsConstructor
public class QueueMember {

    @Getter
    @NonNull
    IPlayer player;
    @Getter
    @NonNull
    KDRange range;

}
