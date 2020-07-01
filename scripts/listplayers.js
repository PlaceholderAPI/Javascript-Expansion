var args0;
var args1;
var args2;
var listOnline = BukkitServer.getOnlinePlayers();
var listOnlineAmount = listOnline.size();

function onlineListPerm() {
	//parse PAPI placeholders in arg1 or arg2
	if (args[1].indexOf("[") == 0 && args[1].indexOf("]") == args[1].length-1) {
		args[1] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[1].replace(/\[|]/g, "%"));
		if (!isNaN(args[1])) {
			args[1] = Math.round(args[1])
		}
	}
	if (args[2].indexOf("[") == 0 && args[2].indexOf("]") == args[2].length-1) {
		args[2] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[2].replace(/\[|]/g, "%"));
		if (!isNaN(args[2])) {
			args[2] = Math.round(args[2])
		}
	}

	var players = [];

	for (var i = 0; i < listOnlineAmount; i++) {
	    var player = listOnline[i];

	    if (player.hasPermission(args[1])) {
	        players.push(player);
	    }
	}
	//list
	if (args[2] == "list" && players != "") {return players.join(", ").replace(/\[|\]|}|CraftPlayer{name=/gi, "")}
	//amount
	else if (args[2] == "amount") {return players.length}
	//player
	else if (players.length != 0 && args[2] < players.length) {return players[args[2]].getName()}
	//0 players
	else {return "Offline"}
}
function onlineListWorld() {
	//parse PAPI placeholders in arg1 or arg2
	if (args[1].indexOf("[") == 0 && args[1].indexOf("]") == args[1].length-1) {
		args[1] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[1].replace(/\[|]/g, "%"));
		if (!isNaN(args[1])) {
			args[1] = Math.round(args[1])
		}
	}
	if (args[2].indexOf("[") == 0 && args[2].indexOf("]") == args[2].length-1) {
		args[2] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[2].replace(/\[|]/g, "%"));
		if (!isNaN(args[2])) {
			args[2] = Math.round(args[2])
		}
	}

	var players = [];

	for (var i = 0; i < listOnlineAmount; i++) {
	    var player = listOnline[i];

	    if (player.getWorld().getName() == args[1]) {
	        players.push(player);
	    }
	}
	//list
	if (args[2] == "list" && players != "") {return players.join(", ").replace(/\[|\]|}|CraftPlayer{name=/gi, "")}
	//amount
	else if (args[2] == "amount") {return players.length}
	//player
	else if (players.length != 0 && args[2] < players.length) {return players[args[2]].getName()}
	//0 players
	else {return "Offline"}
}

function onlineList() {
	if (args.length >= 1) {
		var args0 = args[0];
	}
	if (args.length >= 2) {
		var args1 = args[1];
	}
	if (args.length >= 3) {
		var args2 = args[2];
	}

//parse PAPI placeholders in arg1 or arg2
	if (args0 != undefined && args0.indexOf("[") == 0 && args0.indexOf("]") == args0.length-1) {
		args0 = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args0.replace(/\[|]/g, "%"));
	}
	if (args1 != undefined && args1.indexOf("[") == 0 && args1.indexOf("]") == args1.length-1) {
		args1 = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args1.replace(/\[|]/g, "%"));
		if (args1 != NaN) {
			Math.round(args1)
		}
	}
	if (args2 != undefined && args2.indexOf("[") == 0 && args2.indexOf("]") == args2.length-1) {
		args2 = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args2.replace(/\[|]/g, "%"));
		if (args2 != NaN) {
			Math.round(args2)
		}
	}

//help
	if (args0 == undefined) {
		return " &8>> &3&lValid List Types and Syntaxes:\n     &f- &9all&7:     &7%"+"javascript_listplayers_all,&9#&7%\n      &f- &9perm&7:  &7%"+"javascript_listplayers_perm,&9<permission>&7,&9#&7%\n      &f- &9world&7: &7%"+"javascript_listplayers_world,&9<world>&7,&9#&7%\n&7# can be &9list&7, &9amount&7, or &9a number&7.";
	}

	//help all
	else if (args0 == "all" && args1 == undefined) {
		return "&4&lError&c: &cInvalid Syntax!\n&7Valid Syntax:\n &f- &b%" + "javascript_listplayers_all,&9#&b%\n&7# can be &9list&7, &9amount&7, or &9a number&7.";
	}

	//help perm
	else if (args0 == "perm" && (args1 == undefined || args2 == undefined)) {
		return "&4&lError&c: &cInvalid Syntax!\n&7Valid Syntax:\n &f- &b%" + "javascript_listplayers_perm,&9<permission>&b,&9#&b%\n&7# can be &9list&7, &9amount&7, or &9a number&7.";
	}

	//help world
	else if (args0 == "world" && (args1 == undefined || args2 == undefined)) {
		return "&4&lError&c: &cInvalid Syntax!\n&7Valid Syntax:\n &f- &b%" + "javascript_listplayers_world,&9<world>&b,&9#&b%\n&7# can be &9list&7, &9amount&7, or &9a number&7.";
	}

//check for errors
	//check for type
	else if (args0 != "all" && args0 != "perm" && args0 != "world" && args0 != "{1}") {
		return "&4&lError&c: &cInvalid list type\n&7Valid list types are:\n &f- &ball\n &f- &bperm\n &f- &bworld"
	}

	//check for world
	else if (args0 == "world" && BukkitServer.getWorld(args1) == null) {
		return "&4&lError&c: &cThat world doesn't exist"
	}

	//check for args2 lower than 0
	else if (args2 < 0 || args0 == "all" && args1 < 0) {
		return "&4&lError&c: &cYou can't use negative numbers ! What's even the point ? x)"
	}

//list of all players
	else if (args0 == "all" && args1 == "list") {
		return listOnline.toString().replace(/\[|\]|}|CraftPlayer{name=/gi, "");
	}

	//get player from list
	else if (args0 == "all" && args1 < listOnlineAmount) { 
		return listOnline.get(args1).getName();
	}

	//get amount of players online
	else if (args0 == "all" && args1 == "amount") { 
		if (listOnline == "") {
			return 0
		}
		else {
			return listOnlineAmount
		}
	}

//list of all players with perm     the list          a player         the amount
	else if (args0 == "perm" && (args2 == "list" || !isNaN(args2) || args2 == "amount")) {
			return onlineListPerm();
	}

//list of all players in world      the list          a player         the amount
	else if (args0 == "world" && (args2 == "list" || !isNaN(args2) || args2 == "amount")) {
		return onlineListWorld();
	}

	else {
		return "Offline"
	}
}
onlineList();