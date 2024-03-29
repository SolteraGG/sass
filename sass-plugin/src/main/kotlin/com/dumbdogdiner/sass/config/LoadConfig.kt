package com.dumbdogdiner.sass.config

import com.dumbdogdiner.sass.SassPlugin
import com.dumbdogdiner.sass.api.stats.Statistic
import com.dumbdogdiner.sass.impl.stats.StatisticImpl
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import java.util.UUID

private abstract class GoalSpec(val name: String, vararg val parameters: String) {
    abstract fun createListener(stat: StatisticImpl, parameters: Array<String>): GoalHandler<*>
}

// TODO add to API, could be useful
private fun Statistic.increment(playerId: UUID) {
    this[Int::class.java, playerId] = (this[Int::class.java, playerId] ?: 0) + 1
}

private abstract class GoalHandler<T : Event> : Listener {
    abstract val handlerList: HandlerList

    @EventHandler
    abstract fun onEvent(event: T)

    fun register() {
        Bukkit.getPluginManager().registerEvents(this, SassPlugin.instance)
    }

    fun unregister() {
        handlerList.unregister(this)
    }
}

private val goalSpecs = arrayOf(
    object : GoalSpec("entities killed", "mob type") {
        override fun createListener(stat: StatisticImpl, parameters: Array<String>): GoalHandler<*> {
            val mobType = EntityType.valueOf(parameters[0])

            return object : GoalHandler<EntityDeathEvent>() {
                override val handlerList = EntityDeathEvent.getHandlerList()

                override fun onEvent(event: EntityDeathEvent) {
                    if (event.entity.type != mobType) return
                    val killer = event.entity.killer ?: return
                    stat.increment(killer.uniqueId)
                }
            }
        }
    },

    object : GoalSpec("blocks mined", "block") {
        override fun createListener(stat: StatisticImpl, parameters: Array<String>): GoalHandler<*> {
            // TODO warn if block drops itself?
            val material = Material.valueOf(parameters[0])

            return object : GoalHandler<BlockBreakEvent>() {
                override val handlerList = BlockBreakEvent.getHandlerList()

                override fun onEvent(event: BlockBreakEvent) {
                    if (event.block.type != material) return
                    val activeItem = event.player.activeItem ?: return
                    if (Enchantment.SILK_TOUCH in activeItem.enchantments) return
                    stat.increment(event.player.uniqueId)
                }
            }
        }
    }
).associateBy(GoalSpec::name)

private val sampleConfig = YamlConfiguration().apply {
    load(
"""
challenges:
- name: Kill Skeletons
  stat name: kill skeletons
  goal:
    name: entities killed
    mob type: SKELETON
  tiers:
  - threshold: 10
    reward: 10
  - threshold: 30
    reward: 30
  - threshold: 50
    reward: 50
"""
    )
}
