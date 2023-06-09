package me.gethertv.getcase.cmd;

import me.gethertv.getcase.GetCase;
import me.gethertv.getcase.data.CaseChestData;
import me.gethertv.getcase.data.CaseData;
import me.gethertv.getcase.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CaseCmd implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("getcase.admin"))
            return false;

        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(args.length==1)
            {
                if(args[0].equalsIgnoreCase("reload"))
                {
                    GetCase.getInstance().reloadPluginCase();
                    player.sendMessage(ColorFixer.addColors("&aPlugin reloaded!"));
                    return true;
                }
                if(args[0].equalsIgnoreCase("edit"))
                {
                    player.openInventory(GetCase.getInstance().getGlobalModifyInv());
                    return true;
                }
                if(args[0].equalsIgnoreCase("removeloc"))
                {

                    removeLocChest(player);
                    return true;

                }

            }
            if(args.length==2) {
                if (args[0].equalsIgnoreCase("delete"))
                {
                    String name = args[1].toLowerCase();
                    FileConfiguration config = GetCase.getInstance().getConfig();
                    CaseData caseData = GetCase.getInstance().getCaseData().get(name);
                    if(caseData!=null) {
                        CaseChestData toRemove = null;
                        for(CaseChestData data : GetCase.getInstance().getDataChest())
                        {
                            if(data.getCaseData().equals(caseData))
                                toRemove = data;

                        }
                        if(toRemove!=null) {
                            GetCase.getInstance().getGlobalModifyInv().remove(toRemove.getCaseData().getCaseItem());
                            config.set("cases."+name, null);
                            GetCase.getInstance().saveConfig();
                            GetCase.getInstance().getDataChest().remove(toRemove);
                            toRemove.getLocation().getBlock().setType(Material.AIR);
                            toRemove.getArmorStand().remove();
                            GetCase.getInstance().getCaseData().remove(name);
                            player.sendMessage(ColorFixer.addColors("&aThe case has been successfully deleted!"));
                            return true;
                        }
                    }
                    sender.sendMessage(ColorFixer.addColors("&cThe given case does not exist!"));
                    return true;

                }
                if (args[0].equalsIgnoreCase("create"))
                {
                    String name = args[1].toLowerCase();
                    FileConfiguration config = GetCase.getInstance().getConfig();
                    config.set("cases."+name+".edit.material", "CHEST");
                    config.set("cases."+name+".edit.displayname", "&cCase "+name);
                    List<String> lore = new ArrayList<>();
                    lore.add("&fLEFT &7- Edit drop");
                    lore.add("&fRIGHT &7- Settings case");
                    config.set("cases."+name+".edit.lore", lore);
                    config.set("cases."+name+".name", "&7Case "+name);
                    config.set("cases."+name+".sound.open-case", "BLOCK_SHULKER_BOX_OPEN");
                    config.set("cases."+name+".sound.no-key", "ENTITY_VILLAGER_NO");
                    config.set("cases."+name+".key.material", "TRIPWIRE_HOOK");
                    config.set("cases."+name+".key.displayname", "&aKey "+name);
                    config.set("cases."+name+".key.lore", new ArrayList<>());
                    config.set("cases."+name+".drop", new Object());
                    GetCase.getInstance().saveConfig();
                    player.sendMessage(ColorFixer.addColors("&aThe case has been successfully created!"));
                    return true;
                }

                if(args[0].equalsIgnoreCase("preview"))
                {
                    CaseData caseData = GetCase.getInstance().getCaseData().get(args[1]);
                    if(caseData==null)
                    {
                        player.sendMessage(ColorFixer.addColors("&cThe given case does not exist!"));
                        return false;
                    }
                   caseData.openPreview(player);
                }
                if(args[0].equalsIgnoreCase("setcase"))
                {
                    CaseData caseData = GetCase.getInstance().getCaseData().get(args[1]);
                    if(caseData==null)
                    {
                        player.sendMessage(ColorFixer.addColors("&cThe given case does not exist!"));
                        return false;
                    }
                    caseData.setCase(player, caseData);
                }
            }
        }

        if(args.length==3)
        {
            if(args[0].equalsIgnoreCase("giveall"))
            {

                CaseData caseData = GetCase.getInstance().getCaseData().get(args[1]);
                if(caseData==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cThe given case does not exist!"));
                    return false;
                }
                if(!isNumber(args[2]))
                {
                    sender.sendMessage(ColorFixer.addColors("&cYou need to provide an integer!"));
                    return false;
                }
                int amount = Integer.parseInt(args[2]);
                ItemStack key = caseData.getKey().clone();
                key.setAmount(amount);

                for(Player player : Bukkit.getOnlinePlayers())
                {
                    player.getInventory().addItem(key);
                }

                sender.sendMessage(ColorFixer.addColors("&aAll keys have been successfully granted to everyone!"));
                return true;
            }
        }
        if(args.length==4)
        {
            if(args[0].equalsIgnoreCase("give"))
            {
                Player player = Bukkit.getPlayer(args[1]);
                if(player==null) {
                    sender.sendMessage(ColorFixer.addColors("&cThe specified player is not online!"));
                    return false;
                }
                String skrzynka = args[2];
                CaseData caseData = GetCase.getInstance().getCaseData().get(skrzynka);
                if(caseData==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cThe given case does not exist!"));
                    return false;
                }
                if(!isNumber(args[3]))
                {
                    sender.sendMessage(ColorFixer.addColors("&cYou need to provide an integer!"));
                    return false;
                }
                int amount = Integer.parseInt(args[3]);
                ItemStack key = caseData.getKey().clone();
                key.setAmount(amount);
                player.getInventory().addItem(key);
                sender.sendMessage(ColorFixer.addColors("&aThe key has been granted successfully!"));
                return true;

            }
        }
        return false;
    }

    private void removeLocChest(Player player)
    {
        Block targetBlock = player.getTargetBlock(null, 5);
        if(targetBlock==null || targetBlock.getType()== Material.AIR)
        {
            player.sendMessage(ColorFixer.addColors("&cYou need to look at the block!"));
            return;
        }
        CaseChestData removeCase = null;
        for(CaseChestData caseChestData : GetCase.getInstance().getDataChest())
        {
            if(caseChestData.getLocation().equals(targetBlock.getLocation()))
            {
                removeCase=caseChestData;
                caseChestData.getLocation().getBlock().setType(Material.AIR);
                caseChestData.getArmorStand().remove();
                GetCase.getInstance().getConfig().set("loc-case."+caseChestData.getId(), null);
                GetCase.getInstance().saveConfig();
                break;
            }
        }

        if(removeCase!=null) {
            GetCase.getInstance().getDataChest().remove(removeCase);
            player.sendMessage(ColorFixer.addColors("&aThe case has been deleted successfully!"));
        } else {
            player.sendMessage(ColorFixer.addColors("&cNo case was found here."));
        }



    }

    private boolean isNumber(String input)
    {
        try {
            int a = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {}
        return false;
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.hasPermission("admin"))
            return null;

        if(args.length==1)
        {
            List<String> argCmd = new ArrayList<>();
            argCmd.add("setcase");
            argCmd.add("preview");
            argCmd.add("create");
            argCmd.add("give");
            argCmd.add("giveall");
            argCmd.add("edit");
            argCmd.add("removeloc");
            argCmd.add("delete");
            argCmd.add("reload");
            return argCmd;
        }
        if(args.length==2)
        {
            List<String> argCmd = new ArrayList<>();
            if(args[0].equalsIgnoreCase("preview") || args[0].equalsIgnoreCase("setcase") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("giveall"))
            {
                for (Map.Entry<String, CaseData> stringCaseDataEntry : GetCase.getInstance().getCaseData().entrySet()) {
                    argCmd.add(stringCaseDataEntry.getKey());
                }

                return argCmd;
            }
        }
        if(args.length==3)
        {
            List<String> argCmd = new ArrayList<>();
            if(args[0].equalsIgnoreCase("give"))
            {
                for (Map.Entry<String, CaseData> stringCaseDataEntry : GetCase.getInstance().getCaseData().entrySet()) {
                    argCmd.add(stringCaseDataEntry.getKey());
                }

                return argCmd;
            }
        }
        return null;
    }
}
