var player = BukkitPlayer;
var material = '%player_item_in_hand%';


function lorelines() {
  if ( material !== 'AIR' ) {
    var lore = player.getInventory().getItemInHand().getItemMeta().getLore();
    var has = player.getInventory().getItemInHand().getItemMeta().hasLore();
  }

  if ( material === 'AIR' ) {
    // return 'AIR' when you aren't holding an item (You can change it to whatever you want)
    return 'AIR';
  } else if ( has ) {
    return lore.length;
  } else {
    // return '0' when the item you're holding doesn't has lore (You can change it to whatever you want)
    return '0';
  }
}
lorelines();