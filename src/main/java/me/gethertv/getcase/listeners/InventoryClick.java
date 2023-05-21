package me.gethertv.getcase.listeners;

import me.gethertv.getcase.GetCase;
import me.gethertv.getcase.data.CaseData;
import me.gethertv.getcase.data.SoundModifyType;
import me.gethertv.getcase.data.UserEdit;
import me.gethertv.getcase.utils.ColorFixer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InventoryClick implements Listener {


    @EventHandler
    public void onClickInventory(InventoryClickEvent event)
    {
        if(event.getClickedInventory()==null)
            return;


        if(event.getWhoClicked()==null)
            return;

        if(event.getCurrentItem()==null)
            return;

        Player player = (Player) event.getWhoClicked();

        Inventory previewDrop = GetCase.getInstance().getPreviewDrop().get(player.getUniqueId());
        if(previewDrop!=null)
        {
            if(event.getInventory().equals(previewDrop))
                event.setCancelled(true);
        }

        if(GetCase.getInstance().getDataModify().get(player.getUniqueId())!=null)
        {
            UserEdit userEdit = GetCase.getInstance().getDataModify().get(player.getUniqueId());
            if(userEdit.getInventory().equals(event.getInventory()))
            {
                if(event.getSlot()==52)
                {
                    event.setCancelled(true);
                    return;
                }
                if(event.getSlot()==53)
                {
                    event.setCancelled(true);
                    userEdit.saveItem();
                    player.sendMessage(ColorFixer.addColors("&aSuccessfully saved!"));
                    return;
                }
                if(event.getClick()== ClickType.SHIFT_RIGHT)
                {
                    event.setCancelled(true);
                    double chance = 0;
                    Double chanceNew = userEdit.getChanceItem().get(event.getSlot());
                    if(chanceNew!=null)
                        chance = userEdit.getChanceItem().get(event.getSlot());
                    new AnvilGUI.Builder()
                            .onComplete((p, text) -> {
                                double amount = 0;
                                try {
                                    amount = Double.parseDouble(text);

                                } catch (NumberFormatException e)
                                {
                                    p.sendMessage(ColorFixer.addColors("&cNumber...!"));
                                    return (AnvilGUI.Response.openInventory(userEdit.getInventory()));
                                }
                                if(amount<=0)
                                {
                                    p.sendMessage(ColorFixer.addColors("&cNegative number!"));
                                    return (AnvilGUI.Response.openInventory(userEdit.getInventory()));
                                }
                                userEdit.getChanceItem().put(event.getSlot(), amount);

                                ItemStack itemStack = userEdit.getInventory().getItem(event.getSlot());
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                List<String> lore = new ArrayList<>();
                                if(itemMeta.getLore()!=null)
                                    lore.addAll(itemMeta.getLore());

                                if (lore != null && !lore.isEmpty()) {
                                    lore.removeIf(loreLine -> loreLine.contains("Chance:"));
                                    lore.removeIf(loreLine -> loreLine.contains("SHIFT + RIGHT CLICK"));
                                }

                                lore.add("&fChance: &a{chance}%".replace("{chance}", String.valueOf(amount)));
                                lore.add("&fSHIFT + RIGHT CLICK &7- &eSET CHANCE");
                                itemMeta.setLore(ColorFixer.addColors(lore));
                                itemStack.setItemMeta(itemMeta);
                                userEdit.refreshChance();
                                userEdit.getInventory().setItem(event.getSlot(), itemStack);

                                return (AnvilGUI.Response.openInventory(userEdit.getInventory()));
                            })
                            .text(String.valueOf(chance))
                            .itemLeft(new ItemStack(Material.PAPER))
                            .title(ColorFixer.addColors("&0Chance"))
                            .plugin(GetCase.getInstance())
                            .open(player);
                }

            }

        }
        if(event.getInventory().equals(GetCase.getInstance().getGlobalModifyInv()))
        {
            event.setCancelled(true);
            if(event.getClickedInventory().equals(GetCase.getInstance().getGlobalModifyInv()))
            {
                if(GetCase.getInstance().getItemData().get(event.getCurrentItem())!=null)
                {
                    UserEdit userEdit = new UserEdit(player, GetCase.getInstance().getItemData().get(event.getCurrentItem()));
                    GetCase.getInstance().getDataModify().put(player.getUniqueId(),
                            userEdit);
                    if(event.getClick()== ClickType.LEFT) {
                        userEdit.openInv();
                        return;
                    }
                    if(event.getClick()== ClickType.RIGHT) {
                        userEdit.openUtilsInv();
                        return;
                    }
                }
            }
        }

        {
            UserEdit userEdit = GetCase.getInstance().getDataModify().get(player.getUniqueId());
            if (userEdit != null) {
                Inventory musicInv = GetCase.getInstance().getPageMusicInv().get(userEdit.getPageMusic());
                if (event.getInventory().equals(musicInv)) {
                    event.setCancelled(true);
                    if (event.getClickedInventory().equals(musicInv)) {
                        if (event.getSlot() == 53) {
                            Inventory inventory = GetCase.getInstance().getPageMusicInv().get(userEdit.getPageMusic() + 1);
                            if (inventory == null)
                                return;

                            userEdit.nextPage();
                            player.openInventory(inventory);
                            return;
                        }
                        if (event.getSlot() == 45) {
                            Inventory inventory = GetCase.getInstance().getPageMusicInv().get(userEdit.getPageMusic() - 1);
                            if (inventory == null)
                                return;

                            userEdit.previousPage();
                            player.openInventory(inventory);
                            return;
                        }
                        ItemStack itemStack = event.getCurrentItem();
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        Sound sound = Sound.valueOf(itemMeta.getDisplayName().toUpperCase());
                        if(event.getClick()==ClickType.RIGHT)
                            userEdit.playerSound(sound);

                        if(event.getClick()==ClickType.LEFT)
                        {
                            CaseData caseData = GetCase.getInstance().getCaseData().get(userEdit.getNameCase());
                            if(caseData!=null) {
                                if(userEdit.getSoundModifyType()==SoundModifyType.NO_KEY) {
                                    caseData.setSoundNoKey(sound);
                                    GetCase.getInstance().getConfig().set("cases."+caseData.getName()+".sound.no-key", sound.name().toUpperCase());
                                }
                                if(userEdit.getSoundModifyType()==SoundModifyType.OPEN_CASE) {
                                    caseData.setSoundOpenCase(sound);
                                    GetCase.getInstance().getConfig().set("cases."+caseData.getName()+".sound.open-case", sound.name().toUpperCase());
                                }

                                GetCase.getInstance().saveConfig();

                                player.openInventory(userEdit.getUtilCase());
                            }

                        }

                        return;

                    }
                }
            }
        }

        for (Map.Entry<UUID, UserEdit> entry : GetCase.getInstance().getDataModify().entrySet()) {
            UUID uuid = entry.getKey();
            UserEdit userEdit = entry.getValue();
            if (userEdit.getUtilCase().equals(event.getInventory())) {
                event.setCancelled(true);
                if (userEdit.getUtilCase().equals(event.getClickedInventory())) {
                    if (event.getSlot() == 10) {
                        userEdit.setSoundModifyType(SoundModifyType.OPEN_CASE);
                        player.openInventory(GetCase.getInstance().getPageMusicInv().get(userEdit.getPageMusic()));
                        return;
                    }
                    if (event.getSlot() == 19) {
                        userEdit.setSoundModifyType(SoundModifyType.NO_KEY);
                        player.openInventory(GetCase.getInstance().getPageMusicInv().get(userEdit.getPageMusic()));
                        return;
                    }
                    if(event.getSlot()==14)
                    {
                        ItemStack itemStack = GetCase.getInstance().getCaseData().get(userEdit.getNameCase()).getKey();
                        new AnvilGUI.Builder()
                                .onComplete((p, text) -> {
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    itemMeta.setDisplayName(ColorFixer.addColors(text));
                                    itemStack.setItemMeta(itemMeta);
                                    userEdit.getUtilCase().setItem(15, itemStack);
                                    GetCase.getInstance().getConfig().set("cases."+userEdit.getNameCase()+".key.displayname", text);
                                    GetCase.getInstance().saveConfig();
                                    return (AnvilGUI.Response.openInventory(userEdit.getUtilCase()));
                                })
                                .text(itemStack.getItemMeta().getDisplayName())
                                .itemLeft(new ItemStack(Material.PAPER))
                                .title(ColorFixer.addColors("Displayname"))
                                .plugin(GetCase.getInstance())
                                .open(player);
                    }
                    if(event.getSlot()==16)
                    {
                        ItemStack itemStack = GetCase.getInstance().getCaseData().get(userEdit.getNameCase()).getKey();
                        new AnvilGUI.Builder()
                                .onComplete((p, text) -> {
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    List<String> lore = new ArrayList<>();
                                    if(itemMeta.getLore()!=null)
                                        lore.addAll(itemMeta.getLore());

                                    lore.add(text);
                                    itemMeta.setLore(ColorFixer.addColors(lore));
                                    itemStack.setItemMeta(itemMeta);
                                    userEdit.getUtilCase().setItem(15, itemStack);
                                    GetCase.getInstance().getConfig().set("cases."+userEdit.getNameCase()+".key.lore", lore);
                                    GetCase.getInstance().saveConfig();
                                    return (AnvilGUI.Response.openInventory(userEdit.getUtilCase()));
                                })
                                .text("Line...")
                                .itemLeft(new ItemStack(Material.PAPER))
                                .title(ColorFixer.addColors("Lore"))
                                .plugin(GetCase.getInstance())
                                .open(player);

                        return;
                    }
                    if(event.getSlot()==25)
                    {
                        ItemStack itemStack = GetCase.getInstance().getCaseData().get(userEdit.getNameCase()).getKey();
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemMeta.getLore()==null || itemMeta.getLore().size() <= 0)
                            return;

                        List<String> lore = itemMeta.getLore();
                        lore.remove(itemMeta.getLore().size() - 1);
                        itemMeta.setLore(lore);
                        itemStack.setItemMeta(itemMeta);
                        GetCase.getInstance().getConfig().set("cases."+userEdit.getNameCase()+".key.lore", lore);
                        GetCase.getInstance().saveConfig();
                        userEdit.getUtilCase().setItem(15, itemStack);
                        return;
                    }
                }
            }
        }


        for(Map.Entry<String, CaseData> data : GetCase.getInstance().getCaseData().entrySet())
        {
            if(data.getValue().getInventory().equals(event.getInventory()))
            {
                event.setCancelled(true);

                if(GetCase.getInstance().getSlotsToOpen().contains(event.getSlot()))
                {
                    InteractEvent.openCase(player, data.getValue());
                    return;
                }
                return;
            }
        }

    }

    public static boolean haskey(Player player, ItemStack itemStack)
    {

        for(ItemStack item : player.getInventory())
        {
            if(item==null)
                continue;

            if(item.isSimilar(itemStack))
                return true;
        }

        return false;
    }

    public static void removeKey(Player player, ItemStack itemStack)
    {
        int remove = 1;
        for(int i = 0; i<player.getInventory().getSize(); i++)
        {
            if(player.getInventory().getItem(i)==null)
                continue;

            if(player.getInventory().getItem(i).isSimilar(itemStack))
            {
                int calc = remove-player.getInventory().getItem(i).getAmount();
                if(calc>0)
                {
                    remove-=player.getInventory().getItem(i).getAmount();
                    player.getInventory().setItem(i, null);
                    continue;
                }
                int finalAmount = player.getInventory().getItem(i).getAmount()-remove;
                remove-= player.getInventory().getItem(i).getAmount();
                ItemStack setItem = player.getInventory().getItem(i).clone();
                setItem.setAmount(finalAmount);
                player.getInventory().setItem(i, setItem);
                return;
            }
        }
    }

}
