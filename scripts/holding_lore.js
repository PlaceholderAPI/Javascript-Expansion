var player = BukkitPlayer;
var material = '%player_item_in_hand%';

var line = ' ';


function itemlore() {
  if ( material !== 'AIR' ) {
    var has = player.getInventory().getItemInHand().getItemMeta().hasLore();
    var linelore = player.getInventory().getItemInHand().getItemMeta().getLore();
  }

  if ( material !== 'AIR' && has === true ) {
    var lore = player.getInventory().getItemInHand().getItemMeta().getLore().toString();
  }

  if ( material !== 'AIR' && has === true && lore.indexOf(', ,') !== -1 ) {
    lore = lore.replace(/, ,/g, ',  ,');
  }

  if ( args.length == 1 ) {
    line = args[0];
  }

  if ( material === 'AIR' ) {
    // return 'AIR' when you aren't holding an item (You can change it to whatever you want)
    return 'AIR';
  } else if ( has && line === ' ' ) {
    return lore.replace(/^\[/, "").replace(/.$/,"").replace(/, /g, '\n');
  } else if ( has && line !== ' ' ) {
    if (linelore.length >= line) {
      line = parseInt(line) - 1;
      return linelore[line];
    }
    // return ' ' (Nothing/blank line) when the item you're holding doesn't has the requested line (You can change it to whatever you want)
    return ' ';
  } else {
    // return ' ' (Nothing/blank line) when the item you're holding doesn't has lore (You can change it to whatever you want)
    return ' ';
  }

}
itemlore();