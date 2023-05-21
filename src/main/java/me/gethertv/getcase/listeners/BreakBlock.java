package me.gethertv.getcase.listeners;

import me.gethertv.getcase.GetCase;
import me.gethertv.getcase.data.CaseChestData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlock implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockBreakEvent event)
    {
        Player player = event.getPlayer();

        if(event.isCancelled())
            return;


        for(CaseChestData chestData : GetCase.getInstance().getDataChest())
        {
            Location location = event.getBlock().getLocation();
            if(location.getBlockX()==chestData.getLocation().getBlockX() &&
                    location.getBlockY()==chestData.getLocation().getBlockY() &&
                    location.getBlockZ()==chestData.getLocation().getBlockZ())
            {
                event.setCancelled(true);
                return;
            }

        }
    }
}
