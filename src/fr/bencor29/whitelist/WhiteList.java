package fr.bencor29.whitelist;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class WhiteList extends JavaPlugin implements Listener {

	private boolean allow = false;

	private List<String> whitelist = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		try {
			this.allow = getConfig().getBoolean("allowed", false);
		} catch (Exception e) {
			this.allow = false;
		}
		getConfig().set("allowed", false);
		
		saveConfig();
		
		this.whitelist = (List<String>) getConfig().getList("whitelist");
		try {
			if (this.whitelist.size() == 0) {
				this.whitelist.add("Bencor29");
			}
		} catch(Exception e) {
			List<String> temp = new ArrayList<>();
			temp.add("Bencor29");
			this.whitelist = temp;
		}
		getConfig().set("whitelist", this.whitelist);
		
		saveConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("whitelist.admin")) {
			sender.sendMessage("§cVous n'avez pas la permission !");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage("     §e§l---==[§c§lWhiteList§e§l]==---");
			sender.sendMessage(new String[] {
					"§aCreateur: §eBencor29",
					"§aVersion: §e1.1.0",
					"§aCommande: §e/whitelist",
					"§aSite web: §ehttps://benjamin.cornou.dev",
			});
			return true;
		}
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				printWhitelist(sender);
			} else if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("     §e§l---==[§c§lWhiteList§e§l]==---\n");
				sender.sendMessage("§6Commandes du plugin:\n"
						+ "§7§o/whitelist §ahelp §6: Affiche l'aide sur le plugin.\n"
						+ "§7§o/whitelist §alist §6: Affiche la list des joueurs whitelistés.\n"
						+ "§7§o/whitelist §aadd <joueur> §6: Ajoute un joueur à la whitelist.\n"
						+ "§7§o/whitelist §aremove <joueur> §6: Retire un joueur de la whitelist.\n"
						+ "§7§o/whitelist §aturn <on/off> §6: Active ou désactive la whitelist.");
			} else {
				sender.sendMessage("§cUsage: /whitelist help");
			}
			return true;
		}
		
		if (args.length != 2) {
			sender.sendMessage("§cUsage: /whitelist help");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("add"))
			addPlayer(sender, args[1]);
		else if (args[0].equalsIgnoreCase("remove"))
			removePlayer(sender, args[1]);
		else if (args[0].equalsIgnoreCase("turn"))
			turnWhitelist(sender, args[1]);
		else
			sender.sendMessage("§cUsage: /whitelist help");

		saveConfig();
		return true;
	}
	
	public void turnWhitelist(CommandSender sender, String args) {
		if (args.equalsIgnoreCase("on")) {
			getConfig().set("allowed", "true");
			this.allow = true;
			sender.sendMessage("§aLa whitelist est désormais activée !");
			saveConfig();
		} else if (args.equalsIgnoreCase("off")) {
			getConfig().set("allowed", "false");
			this.allow = false;
			sender.sendMessage("§aLa whitelist est désormais désactivée !");
			saveConfig();
		} else
			sender.sendMessage("§cUsage: /whitelist turn <on/off>");
	}

	public void removePlayer(CommandSender sender, String playerName) {
		int i = 0;
		boolean removed = false;
		for(String user : this.whitelist) {
			if(user.equalsIgnoreCase(playerName)) {
				this.whitelist.remove(i);
				removed = true;
				i--;
			}
			i++;
		}
		if (removed)
			sender.sendMessage("§aLe joueur §e" + playerName + "§a a été retiré de la whitelist !");
		else
			sender.sendMessage("§cLe joueur §e" + playerName + "§c n'a pas été trouvé !");
	}

	public void addPlayer(CommandSender sender, String args) {
		boolean exist = false;
		for (String user : this.whitelist) {
			if (user.equalsIgnoreCase(args)) {
				exist = true;
				break;
			}
		}
		if (exist)
			sender.sendMessage("§cLe joueur §e" + args + "§c est déjà dans la list !");
		else {
			this.whitelist.add(args);
			sender.sendMessage("§aLe joueur §e" + args + "§a a été ajouté à la whitelist !");
		}
	}

	public void printWhitelist(CommandSender sender) {
		StringBuilder message = new StringBuilder("\n§6Liste des joueurs whitelistés :\n");
		for(String user : this.whitelist) {
			message.append("§7 - ").append(user).append("\n");
		}
		sender.sendMessage(message.toString());
	}
	
	@EventHandler
	public void event(PlayerLoginEvent event)
	{
		if(!this.allow) return;

		String player = event.getPlayer().getName();
		for(String user : this.whitelist)
			if(user.equalsIgnoreCase(player))
				return;

		event.disallow(Result.KICK_WHITELIST, "§cTu n'es pas dans la whitelist !");
	}
}
