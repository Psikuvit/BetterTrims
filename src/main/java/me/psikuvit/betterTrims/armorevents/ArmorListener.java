package me.psikuvit.betterTrims.armorevents;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Arnah
 * @since Jul 30, 2015
 */
public class ArmorListener implements Listener{

	private final List<Material> blockedMaterials = List.of(
	Material.FURNACE,
	Material.CHEST,
	Material.TRAPPED_CHEST,
	Material.BEACON,
	Material.DISPENSER,
	Material.DROPPER,
	Material.HOPPER,
	Material.CRAFTING_TABLE,
	Material.ENCHANTING_TABLE,
	Material.ENDER_CHEST,
	Material.ANVIL,
	Material.OAK_FENCE_GATE,
	Material.SPRUCE_FENCE_GATE,
	Material.BIRCH_FENCE_GATE,
	Material.ACACIA_FENCE_GATE,
	Material.JUNGLE_FENCE_GATE,
	Material.DARK_OAK_FENCE_GATE,
	Material.IRON_DOOR,
	Material.SPRUCE_DOOR,
	Material.BIRCH_DOOR,
	Material.JUNGLE_DOOR,
	Material.ACACIA_DOOR,
	Material.DARK_OAK_DOOR,
	Material.STONE_BUTTON,
	Material.IRON_TRAPDOOR,
	Material.SPRUCE_FENCE,
	Material.BIRCH_FENCE,
	Material.JUNGLE_FENCE,
	Material.DARK_OAK_FENCE,
	Material.ACACIA_FENCE,
	Material.BREWING_STAND,
	Material.CAULDRON,
	Material.ACACIA_SIGN,
	Material.ACACIA_WALL_SIGN,
	Material.BIRCH_SIGN,
	Material.BIRCH_WALL_SIGN,
	Material.DARK_OAK_SIGN,
	Material.DARK_OAK_WALL_SIGN,
	Material.JUNGLE_SIGN,
	Material.JUNGLE_WALL_SIGN,
	Material.OAK_SIGN,
	Material.OAK_WALL_SIGN,
	Material.SPRUCE_SIGN,
	Material.SPRUCE_WALL_SIGN,
	Material.LEVER,
	Material.BLACK_SHULKER_BOX,
	Material.BLUE_SHULKER_BOX,
	Material.BROWN_SHULKER_BOX,
	Material.CYAN_SHULKER_BOX,
	Material.GRAY_SHULKER_BOX,
	Material.GREEN_SHULKER_BOX,
	Material.LIGHT_BLUE_SHULKER_BOX,
	Material.LIME_SHULKER_BOX,
	Material.MAGENTA_SHULKER_BOX,
	Material.ORANGE_SHULKER_BOX,
	Material.PINK_SHULKER_BOX,
	Material.PURPLE_SHULKER_BOX,
	Material.RED_SHULKER_BOX,
	Material.LIGHT_GRAY_SHULKER_BOX,
	Material.WHITE_SHULKER_BOX,
	Material.YELLOW_SHULKER_BOX,
	Material.DAYLIGHT_DETECTOR,
	Material.BARREL,
	Material.BLAST_FURNACE,
	Material.SMOKER,
	Material.CARTOGRAPHY_TABLE,
	Material.COMPOSTER,
	Material.GRINDSTONE,
	Material.LECTERN,
	Material.LOOM,
	Material.STONECUTTER,
	Material.BELL
    );

	//Event Priority is highest because other plugins might cancel the events before we check.
	@EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
	public final void inventoryClick(final InventoryClickEvent e){
		boolean shift = false, numberkey = false;
        if(e.getAction() == InventoryAction.NOTHING) return;// Why does this get called if nothing happens??
		if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)){
			shift = true;
		}
		if(e.getClick().equals(ClickType.NUMBER_KEY)){
			numberkey = true;
		}
		if(e.getSlotType() != SlotType.ARMOR && e.getSlotType() != SlotType.QUICKBAR && e.getSlotType() != SlotType.CONTAINER) return;
		if(e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
		if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER)) return;
		if(!(e.getWhoClicked() instanceof Player)) return;
		ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
		if(!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot()){
			// Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots slot.
			return;
		}
		if(shift){
			newArmorType = ArmorType.matchType(e.getCurrentItem());
			if(newArmorType != null){
				boolean equipping = e.getRawSlot() != newArmorType.getSlot();
                if(newArmorType.equals(ArmorType.HELMET) && (equipping == isAirOrNull(e.getWhoClicked().getInventory().getHelmet())) || newArmorType.equals(ArmorType.CHESTPLATE) && (equipping == isAirOrNull(e.getWhoClicked().getInventory().getChestplate())) || newArmorType.equals(ArmorType.LEGGINGS) && (equipping == isAirOrNull(e.getWhoClicked().getInventory().getLeggings())) || newArmorType.equals(ArmorType.BOOTS) && (equipping == isAirOrNull(e.getWhoClicked().getInventory().getBoots()))){
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
					}
				}
			}
		}else{
			ItemStack newArmorPiece = e.getCursor();
			ItemStack oldArmorPiece = e.getCurrentItem();
			if(numberkey){
				if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)){// Prevents shit in the 2by2 crafting
					// e.getClickedInventory() == The players inventory
					// e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
					// e.getRawSlot() == The slot the item is going to.
					// e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
					ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
					if(!isAirOrNull(hotbarItem)){// Equipping
						newArmorType = ArmorType.matchType(hotbarItem);
						newArmorPiece = hotbarItem;
						oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
					}else{// Unequipping
						newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
					}
				}
			}else{
				if(isAirOrNull(e.getCursor()) && !isAirOrNull(e.getCurrentItem())){// unequip with no new item going into the slot.
					newArmorType = ArmorType.matchType(e.getCurrentItem());
				}
				// e.getCurrentItem() == Unequip
				// e.getCursor() == Equip
				// newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
			}
			if(newArmorType != null && e.getRawSlot() == newArmorType.getSlot()){
				ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.PICK_DROP;
				if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
				if(armorEquipEvent.isCancelled()){
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority =  EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent e){
		if(e.useItemInHand().equals(Result.DENY))return;
		//
		if(e.getAction() == Action.PHYSICAL) return;
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player player = e.getPlayer();
			if(!e.useInteractedBlock().equals(Result.DENY)){
				if(e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()){// Having both of these checks is useless, might as well do it though.
					// Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
					Material mat = e.getClickedBlock().getType();
					if (blockedMaterials.contains(mat)) return;
				}
			}
			ArmorType newArmorType = ArmorType.matchType(e.getItem());
			if(newArmorType != null){
				if(newArmorType.equals(ArmorType.HELMET) && isAirOrNull(e.getPlayer().getInventory().getHelmet()) || newArmorType.equals(ArmorType.CHESTPLATE) && isAirOrNull(e.getPlayer().getInventory().getChestplate()) || newArmorType.equals(ArmorType.LEGGINGS) && isAirOrNull(e.getPlayer().getInventory().getLeggings()) || newArmorType.equals(ArmorType.BOOTS) && isAirOrNull(e.getPlayer().getInventory().getBoots())){
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
						player.updateInventory();
					}
				}
			}
		}
	}
	
	@EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
	public void inventoryDrag(InventoryDragEvent event){
		// getType() seems to always be even.
		// Old Cursor gives the item you are equipping
		// Raw slot is the ArmorType slot
		// Can't replace armor using this method making getCursor() useless.
		ArmorType type = ArmorType.matchType(event.getOldCursor());
		if(event.getRawSlots().isEmpty()) return;// Idk if this will ever happen
		if(type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)){
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(), ArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor());
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if(armorEquipEvent.isCancelled()){
				event.setResult(Result.DENY);
				event.setCancelled(true);
			}
		}
		// Debug shit
		/*System.out.println("Slots: " + event.getInventorySlots().toString());
		System.out.println("Raw Slots: " + event.getRawSlots().toString());
		if(event.getCursor() != null){
			System.out.println("Cursor: " + event.getCursor().getType().name());
		}
		if(event.getOldCursor() != null){
			System.out.println("OldCursor: " + event.getOldCursor().getType().name());
		}
		System.out.println("Type: " + event.getType().name());*/
	}

	@EventHandler
	public void itemBreakEvent(PlayerItemBreakEvent e){
		ArmorType type = ArmorType.matchType(e.getBrokenItem());
		if(type != null){
			Player p = e.getPlayer();
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if(armorEquipEvent.isCancelled()){
				ItemStack i = e.getBrokenItem().clone();
				i.setAmount(1);
				i.setDurability((short) (i.getDurability() - 1));
				if(type.equals(ArmorType.HELMET)){
					p.getInventory().setHelmet(i);
				}else if(type.equals(ArmorType.CHESTPLATE)){
					p.getInventory().setChestplate(i);
				}else if(type.equals(ArmorType.LEGGINGS)){
					p.getInventory().setLeggings(i);
				}else if(type.equals(ArmorType.BOOTS)){
					p.getInventory().setBoots(i);
				}
			}
		}
	}

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(e.getKeepInventory()) return;
		for(ItemStack i : p.getInventory().getArmorContents()){
			if(!isAirOrNull(i)){
				Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
				// No way to cancel a death event.
			}
		}
	}

	/**
	 * A utility method to support versions that use null or air ItemStacks.
	 */
	public static boolean isAirOrNull(ItemStack item){
		return item == null || item.getType().equals(Material.AIR);
	}
}
