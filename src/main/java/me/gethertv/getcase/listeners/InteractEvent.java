package me.gethertv.getcase.listeners;

import me.gethertv.getcase.GetCase;
import me.gethertv.getcase.data.CaseChestData;
import me.gethertv.getcase.data.CaseData;
import me.gethertv.getcase.data.DropData;
import me.gethertv.getcase.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;

public class InteractEvent implements Listener {

    @EventHandler
    public void onClickPlayer(PlayerInteractEvent event)
    {

        Player player = event.getPlayer();
        if(event.getAction()==Action.RIGHT_CLICK_BLOCK) {
            for (CaseChestData chestData : GetCase.getInstance().getDataChest()) {
                Location location = event.getClickedBlock().getLocation();
                if (player.getInventory().getItemInMainHand().isSimilar(chestData.getCaseData().getKey()) ||
                        player.getInventory().getItemInOffHand().isSimilar(chestData.getCaseData().getKey())) {
                    event.setCancelled(true);
                    if (location.getBlockX() == chestData.getLocation().getBlockX() &&
                            location.getBlockY() == chestData.getLocation().getBlockY() &&
                            location.getBlockZ() == chestData.getLocation().getBlockZ())
                    {
                        openCase(player, chestData.getCaseData(), event.getClickedBlock());
                    }
                    return;
                }

            }
        }
        if(event.getAction()==Action.RIGHT_CLICK_BLOCK || event.getAction()==Action.LEFT_CLICK_BLOCK) {
            for (CaseChestData chestData : GetCase.getInstance().getDataChest()) {
                Location location = event.getClickedBlock().getLocation();
                if (location.getBlockX() == chestData.getLocation().getBlockX() &&
                        location.getBlockY() == chestData.getLocation().getBlockY() &&
                        location.getBlockZ() == chestData.getLocation().getBlockZ()) {
                    event.setCancelled(true);

                    if(EquipmentSlot.OFF_HAND==event.getHand())
                        return;

                    player.playSound(location, chestData.getCaseData().getSoundOpenCase(), 1F, 1F);
                    chestData.getCaseData().openPreview(player);
                    return;
                }

            }
        }

    }

    private void openCase(Player player, CaseData data, Block block) {
        for(CaseChestData chestData : GetCase.getInstance().getDataChest())
        {
            Location location = block.getLocation();
            if(location.getBlockX()==chestData.getLocation().getBlockX() &&
                    location.getBlockY()==chestData.getLocation().getBlockY() &&
                    location.getBlockZ()==chestData.getLocation().getBlockZ())
            {
                openCase(player, data);
            }
        }
    }

    public static void openCase(Player player, CaseData data)
    {
        if(InventoryClick.haskey(player, data.getKey()))
        {
            InventoryClick.removeKey(player, data.getKey());
            data.giveDrop(player);
            return;
        }
        player.closeInventory();
        player.playSound(player.getLocation(), data.getSoundNoKey(), 1F, 1F);
        player.sendMessage(ColorFixer.addColors(GetCase.getInstance().getConfig().getString("lang.no-key")));
        return;
    }
}
