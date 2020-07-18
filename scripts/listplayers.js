var listOnline = BukkitServer.getOnlinePlayers();
var listOffline = BukkitServer.getOfflinePlayers();
var listType = listOffline;

function normalListPlayers() {
	for (var i = 0; i < args.length; i++) {

		if (args[i].indexOf("[") == 0 && args[i].indexOf("]") == args[i].length-1) {
			args[i] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[i].replace(/\[|]/g, "%"));
			if (!isNaN(args[i])) {
				args[i] = parseInt(args[i],10).toPrecision(1);
			}
		}
	}

	if (args[0] == "online") {listType = listOnline}

	var players = [];

	for (var i = 0; i < listType.length; i++) {
	    var player = listType[i];

	    if (args[2] == "no" && BukkitPlayer.getName() == player.getName()) {}
		else {
	    	if (args[0] == "offline" && listOnline.indexOf(player) >= 0) {}
	    	else {players.push(player.getName())}
		}
	}
	players.sort();
	//list
	if (args[3] == "list" && players != "") {return players.join(", ")}
	//amount
	else if (args[3] == "amount") {return players.length}
	//player
	else if (players.length != 0 && args[3] < players.length) {return players[args[3]]}
	//0 players
	else {return "Offline"}
}
function permListPlayers() {
	for (var i = 0; i < args.length; i++) {

		if (args[i].indexOf("[") == 0 && args[i].indexOf("]") == args[i].length-1) {
			args[i] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[i].replace(/\[|]/g, "%"));
			if (!isNaN(args[i])) {
				args[i] = parseInt(args[i],10).toPrecision(1);
			}
		}
	}

	var args4 = args[4].split('+');
	var players = [];

	for (var i = 0; i < listOnline.length; i++) {
	    var player = listOnline[i];

	    for (var i2 = 0; i2 < args4.length; i2++) {
		    if (players.indexOf(player.getName()) == -1 && player.hasPermission(args4[i2])) {
			    if (args[2] == "no" && BukkitPlayer.getName() == player.getName()) {}
 				else {players.push(player.getName())}
		    }
		}
	}
	players.sort();
	//list
	if (args[3] == "list" && players != "") {return players.join(", ")}
	//amount
	else if (args[3] == "amount") {return players.length}
	//player
	else if (players.length != 0 && args[3] < players.length) {return players[args[3]]}
	//0 players
	else {return "Offline"}
}
function worldListPlayers() {
	for (var i = 0; i < args.length; i++) {

		if (args[i].indexOf("[") == 0 && args[i].indexOf("]") == args[i].length-1) {
			args[i] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[i].replace(/\[|]/g, "%"));
			if (!isNaN(args[i])) {
				args[i] = parseInt(args[i],10).toPrecision(1);
			}
		}
	}

	var args4 = args[4].split('+') + ',';
	var players = [];

	for (var i = 0; i < listOnline.length; i++) {
	    var player = listOnline[i];

	    if (args4.indexOf(player.getWorld().getName()+",") >= 0) {
			if (args[2] == "no" && BukkitPlayer.getName() == player.getName()) {}
 			else {players.push(player.getName())}
	    }
	}
	players.sort();
	//list
	if (args[3] == "list" && players != "") {return players.join(", ")}
	//amount
	else if (args[3] == "amount") {return players.length}
	//player
	else if (players.length != 0 && args[3] < players.length) {return players[args[3]]}
	//0 players
	else {return "Offline"}
}
function nearbyListPlayers() {
	for (var i = 0; i < args.length; i++) {

		if (args[i].indexOf("[") == 0 && args[i].indexOf("]") == args[i].length-1) {
			args[i] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[i].replace(/\[|]/g, "%"));
			if (!isNaN(args[i])) {
				args[i] = parseInt(args[i],10).toPrecision(1);
			}
		}
	}

	var players = [];

	for (var i = 0; i < listOnline.length; i++) {
	    var player = listOnline[i];
	    var zone = args[4]*args[4];

	    	if (BukkitPlayer.getWorld() == player.getWorld() && BukkitPlayer.getLocation().distanceSquared(player.getLocation()) < zone) {
				if (args[2] == "no" && BukkitPlayer.getName() == player.getName()) {}
	 			else {players.push(player.getName())}
	    }
	}
	players.sort();
	//list
	if (args[3] == "list" && players != "") {return players.join(", ")}
	//amount
	else if (args[3] == "amount") {return players.length}
	//player
	else if (players.length != 0 && args[3] < players.length) {return players[args[3]]}
	//0 players
	else {return "Offline"}
}

function mainListPlayers() {
	for (var i = 0; i < args.length; i++) {

		if (args[i].indexOf("[") == 0 && args[i].indexOf("]") == args[i].length-1) {
			args[i] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, args[i].replace(/\[|]/g, "%"));
			if (!isNaN(args[i])) {
				args[i] = parseInt(args[i],10).toPrecision(1);
			}
		}
	}
		//type
	var args0;
	if (args.length >= 1) {args0 = args[0]}
		//subtype
	var args1;
	if (args.length >= 2) {args1 = args[1]}
		//yes/no
	var args2;
	if (args.length >= 3) {args2 = args[2]}
		//output
	var args3;
	if (args.length >= 4) {args3 = args[3]}
		//subtype value
	var args4;
	if (args.length >= 5) {args4 = args[4]}


		//check for types
	if (args0 != "online" && args0 != "offline" && args0 != "all") {return "&3&lValid List Types: &9online&f, &9offline&f, &9all&f."}
		//check for subtypes
	else if (args1 != "normal" && args1 != "perm" && args1 != "world" && args1 != "nearby") {return "&3&lValid List SubTypes: &9normal&f, &9perm&f, &9world&f, &9nearby&f."}
		//help syntax
	else if (args.length == 2) {return "&3&lValid Syntax: &9%"+"javascript_listplayers_&b"+args0+"&9,&b"+args1+"&9,&b<yes/no>&9,&b<output>&9,&b<subtype value>&9%"}
		//check for yes/no
	else if (args2 != "yes" && args2 != "no") {return "&4&lError&c: You have to use either &9yes &cor &9no &cin the third argument."}
		//check for output
	else if (args3 != "list" && args3 != "amount" && isNaN(args3)) {return "&3&lValid Output Types: &9list&f, &9amount&f, &9a number&f."}
		//check for output lower than 0
	else if (args3 == "-0" || args3 < 0) {return "&4&lError&c: &cYou can't use negative numbers ! What's even the point ? x)"}
		//check for args4
	else if ((args1 == "world" || args1 == "perm" || args1 == "nearby") && args4 == null) {return "&4&lError&c: &cYou have to specify a &9permission&c/&9world&c/&9radius&c."}
		//check for offline/all + perm/world/nearby
	else if ((args0 == "offline" || args0 == "all") && (args1 == "perm" || args1 == "world" || args1 == "nearby")) {return "Unsupported =/"}


	if (args1 == "normal") {return normalListPlayers()}
	else if (args1 == "perm") {return permListPlayers()}
	else if (args1 == "world") {return worldListPlayers()}
	else if (args1 == "nearby") {return nearbyListPlayers()}
	else {return "Offline"}
}

mainListPlayers()
