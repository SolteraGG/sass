/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.sass.api;

import com.dumbdogdiner.sass.api.reward.Challenge;
import com.dumbdogdiner.sass.api.reward.Tier;
import com.dumbdogdiner.sass.api.stats.Statistic;
import com.dumbdogdiner.sass.api.stats.Store;
import com.google.gson.JsonElement;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SassService {
    /**
     * @return The instance of the SassService, or null if none is registered.
     */
    @Nullable
    static SassService get() {
        var servicesManager = Bukkit.getServicesManager();
        var provider = servicesManager.getRegistration(SassService.class);
        if (provider != null) {
            return provider.getProvider();
        } else {
            return null;
        }
    }

    /**
     * @param plugin The plugin to get statistics for.
     * @return The global statistics store attached to this plugin instance.
     */
    @NotNull
    Store getGlobalStore(@NotNull JavaPlugin plugin);

    /**
     * @param plugin The plugin to get statistics for.
     * @return The per-server statistics store attached to this plugin instance, or null if BungeeCord is not detected.
     */
    @Nullable
    Store getServerStore(@NotNull JavaPlugin plugin);

    /**
     * @param name {@link Challenge#getName()}
     * @param icon {@link Challenge#getIcon()}
     * @param tiers {@link Challenge#getTiers()}
     * @param statistic {@link Challenge#getStatistic()}
     * @param progress {@link Challenge#getProgress()}
     * @return The new challenge.
     */
    @NotNull
    Challenge createChallenge(
        @NotNull String name,
        @NotNull ItemStack icon,
        @NotNull Tier[] tiers,
        @NotNull Statistic statistic,
        @NotNull Function<@Nullable JsonElement, @NotNull Integer> progress
    );
}
