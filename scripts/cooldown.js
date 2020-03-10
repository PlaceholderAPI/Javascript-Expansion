var monthSymbol = "mo";
var daySymbol = "d";
var hourSymbol = "h";
var minuteSymbol = "m";
var secondSymbol = "s";

var arg = args[0].split("_");
if (arg.length === 2) {
  var ID = arg[0];
  var cooldown = arg[1];
}

var dataLoc = "%player_name%." + ID + ".date";
var currentDate = new Date();

function Cooldown() {
  if (!Data.exists(dataLoc)) {
    return "0s";
  } else {
    var startDate = new Date(Data.get(dataLoc));
    var difference = currentDate - startDate;
    var result = Math.floor(difference / 1000);
    if (result >= cooldown) {
      return "0s";
    } else {
      startDate = new Date(startDate.getTime() + (cooldown * 1000));
      var result = startDate - currentDate;

      var months = Math.floor(result / (1000 * 60 * 60 * 24 * 31));
      var days = Math.floor(result % (1000 * 60 * 60 * 24 * 31) / (1000 * 60 * 60 * 24));
      var hours   = Math.floor(result % (1000 * 60 * 60 * 24) / (1000 * 60 * 60));
      var minutes = Math.floor(result % (1000 * 60 * 60) / (1000 * 60));
      var seconds = Math.floor(result % (1000 * 60) / (1000));

      if (months === 0 && days === 0 && hours === 0 && minutes === 0) {
        return seconds + secondSymbol;
      } else if (months === 0 && days === 0 && hours === 0) {
        return minutes + minuteSymbol + seconds + secondSymbol;
      } else if (months === 0 && days === 0) {
        return hours + hourSymbol + minutes + minuteSymbol + seconds + secondSymbol;
      } else if (months === 0) {
        return days + daySymbol + hours + hourSymbol + minutes + minuteSymbol + seconds + secondSymbol;
      } else {
        return months + monthSymbol + days + daySymbol + hours + hourSymbol + minutes + minuteSymbol + seconds + secondSymbol;
      }
    }
  }
}

function start() {
  var data = currentDate.toString();

  Data.set(dataLoc, data);
  Placeholder.saveData();
}

function run() {
  if (args.length !== 1 || arg.length !== 2) {
    return "&cInvalid syntax, Please use this syntax:\n&7%" + "javascript_cooldown_[ID]_[Cooldown/Start]" + "%";
  } else if (cooldown.toUpperCase() === "START") {
    return start();
  } else if (isNaN(cooldown)) {
    return "&cPlease set a valid cooldown.";
  } else if (!isNaN(cooldown)) {
    return Cooldown();
  } else {
    return "&cInvalid syntax, Please use this syntax:\n&7%" + "javascript_cooldown_[ID]_[Cooldown/Start]" + "%";
  }
}
run();
