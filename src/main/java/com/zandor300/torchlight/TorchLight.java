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

import com.zandor300.zsutilities.utilities.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * @author Zandor Smith
 * @since 1.0.0
 */
public class TorchLight extends JavaPlugin {

	private static HashMap<String, BlockState> playerState = new HashMap<String, BlockState>();

	private static Chat chat = new Chat("TorchLight");
	private static TorchLight plugin;

	@Override
	public void onEnable() {
		chat.sendConsoleMessage("Setting things up...");

		plugin = this;

		chat.sendConsoleMessage("Starting timers...");
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!player.getItemInHand().getType().equals(Material.TORCH)) {
						if (playerState.get(player.getName()) != null) {
							BlockState state = playerState.get(player.getName());
							state.getLocation().getBlock().setType(state.getType());
							state.getLocation().getBlock().setData(state.getData().getData());
						}
						playerState.put(player.getName(), null);
					} else {
						BlockState state = playerState.get(player.getName());
						state.getLocation().getBlock().setType(state.getType());
						state.getLocation().getBlock().setData(state.getData().getData());
						Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() - 1, player.getLocation().getZ());
						playerState.put(player.getName(), location.getBlock().getState());
						location.getBlock().setType(Material.GLOWSTONE);
					}
				}
			}
		}, 20l, 1l);
		chat.sendConsoleMessage("Timers started.");

		chat.sendConsoleMessage("Everything is setup!");
		chat.sendConsoleMessage("Enabled.");
	}

	@Override
	public void onDisable() {

	}

	public static Chat getChat() {
		return chat;
	}

	public static TorchLight getPlugin() {
		return plugin;
	}
}
