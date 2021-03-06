/**
 * Copyright 2015 Zandor Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zandor300.torchlight;

import com.zandor300.torchlight.commands.TorchLightCommand;
import com.zandor300.zsutilities.ZSUtilities;
import com.zandor300.zsutilities.commandsystem.CommandManager;
import com.zandor300.zsutilities.utilities.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zandor Smith
 * @since 1.0.0
 */
public class TorchLight extends JavaPlugin {

	private static HashMap<String, BlockState> playerState = new HashMap<String, BlockState>();

	private static Chat chat = new Chat("TorchLight", ChatColor.GOLD);
	private static TorchLight plugin;
	private static BukkitTask task;

	public static Chat getChat() {
		return chat;
	}

	public static TorchLight getPlugin() {
		return plugin;
	}

	@Override
	public void onEnable() {
		chat.sendConsoleMessage("Setting things up...");

		plugin = this;

		ZSUtilities.addDependency(this);

		chat.sendConsoleMessage("Starting metrics...");
		try {
			new Metrics(this).start();
			chat.sendConsoleMessage("Submitted stats to MCStats.org.");
		} catch (IOException e) {
			chat.sendConsoleMessage("Couldn't submit stats to MCStats.org...");
		}

		chat.sendConsoleMessage("Starting timers...");
		task = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (!player.getItemInHand().getType().equals(Material.TORCH)) {
						if (playerState.get(player.getName()) != null) {
							BlockState state = playerState.get(player.getName());
							if (state.getLocation().getBlock() != null) {
								state.getLocation().getBlock().setType(state.getType());
								state.getLocation().getBlock().setData(state.getData().getData());
							}
						}
						playerState.put(player.getName(), null);
					} else {
						BlockState state = playerState.get(player.getName());
						if (state != null && state.getLocation().getBlock() != null) {
							state.getLocation().getBlock().setType(state.getType());
							state.getLocation().getBlock().setData(state.getData().getData());
						}
						Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() - 1, player.getLocation().getZ());
						boolean theVoid = false;
						while (location.getBlock().getType().equals(Material.AIR) ||
								location.getBlock().getType().equals(Material.WATER) ||
								location.getBlock().getType().equals(Material.STATIONARY_WATER) ||
								location.getBlock().getType().equals(Material.LAVA) ||
								location.getBlock().getType().equals(Material.STATIONARY_LAVA)) {
							location.add(0, -1, 0);
							if(location.getY() <= 1) {
								theVoid = true;
								break;
							}
						}
						if(!theVoid) {
							playerState.put(player.getName(), location.getBlock().getState());
							location.getBlock().setType(Material.GLOWSTONE);
						}
					}
				}
			}
		}, 20l, 1l);
		chat.sendConsoleMessage("Timers started.");

		CommandManager cm = new CommandManager();
		cm.registerCommand(new TorchLightCommand(), this);

		chat.sendConsoleMessage("Everything is setup!");
		chat.sendConsoleMessage("Enabled.");
	}

	@Override
	public void onDisable() {
		chat.sendConsoleMessage("Resetting remaining glowstone blocks...");
		task.cancel();
		for (Map.Entry<String, BlockState> entry : playerState.entrySet()) {
			entry.getValue().getLocation().getBlock().setType(entry.getValue().getType());
			entry.getValue().getLocation().getBlock().setData(entry.getValue().getData().getData());
		}
		chat.sendConsoleMessage("All remaining glowstone blocks have been reset.");
	}
}
