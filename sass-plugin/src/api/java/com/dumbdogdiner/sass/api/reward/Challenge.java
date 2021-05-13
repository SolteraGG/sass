/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.sass.api.reward;

import com.dumbdogdiner.sass.api.stats.store.statistic.Statistic;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a challenge. Challenges are goals that a player may pursue. They contain various attributes that allow
 * players to choose challenges to complete, track their progress, and anticipate rewards.
 */
public interface Challenge {
    /**
     * @return The store this challenge belongs to.
     */
    @NotNull
    ChallengeStore getChallengeStore();

    /**
     * @return The identifier for this challenge.
     */
    @NotNull
    String getIdentifier();

    /**
     * @return The function that determines the friendly name of this challenge for a given player, or null if the
     * player should not see this challenge.
     */
    @NotNull
    Function<@NotNull UUID, @Nullable String> getName();

    /**
     * @return The function that determines the reward for a given player. The function should return a nonzero value,
     * indicating the number of miles to reward. Other values indicate that the reward is not attainable.
     */
    @NotNull
    Function<@NotNull UUID, @NotNull Integer> getReward();

    /**
     * @return The function that determines the start of the range that this challenge spans for a given player.
     */
    @NotNull
    Function<@NotNull UUID, @NotNull Integer> getStart();

    /**
     * @return The function that determines the end of the range that this challenge spans for a given player.
     */
    @NotNull
    Function<@NotNull UUID, @NotNull Integer> getGoal();

    /**
     * @return The function that determines an integral value a given player has for this challenge.
     * @see Challenge#getStart()
     * @see Challenge#getGoal()
     */
    @NotNull
    Function<@NotNull UUID, @NotNull Integer> getProgress();

    /**
     * @param stat The statistic to add to the set of this challenge's associated statistics.
     * @return True if the statistic was added, false if it was already associated.
     */
    boolean addAssociatedStatistic(@NotNull Statistic stat);

    /**
     * @param stat The statistic to add to the set of this challenge's associated statistics.
     * @return True if the statistic was removed, false if it was never associated.
     */
    boolean removeAssociatedStatistic(@NotNull Statistic stat);

    /**
     * @return The set of statistics that this challenge is associated with. When any of these statistics are modified,
     * this challenge checks if it has been completed.
     */
    Set<Statistic> getAssociatedStatistics();

    /**
     * Delete this challenge. Further use of this object is invalid.
     */
    void delete();
}
