function set(path, data) {
  Data.set(path, data);
  return "";
}
function add(path, amount) {
  if (isNaN(amount)) {
    return "";
  }
  var amt = Data.exists(path) ? Data.get(path) : 0;
  if (isNaN(amt)) {
    amt = 0;
  }
  amt = parseInt(amt) + parseInt(amount);
  Data.set(path, amt.toFixed());
  return "";
}
function subtract(path, amount) {
  if (isNaN(amount)) {
    return "";
  }
  var amt = Data.exists(path) ? parseInt(Data.get(path)) : 0;
  if (isNaN(amt)) {
    amt = 0;
  }
  amt = parseInt(amt) - parseInt(amount);
  Data.set(path, amt.toFixed());
  return "";
}
function get(path)  {
  return Data.exists(path) ? Data.get(path) : "";
}
function getInt(path) {
  if (Data.exists(path)) {
    var amt = Data.get(path);
    if (isNaN(amt)) {
      return 0;
    } else {
      return parseInt(amt).toFixed();
    }
  }
  return 0;
}
function getUsage(firstArg) {
  switch(firstArg) {
    case "get":
      return "get,<path>";
    case "getint":
      return "getint,<path>";
    case "add":
      return "add,<path>,<amount>";
    case "subtract":
      return "subtract,<path>,<amount>";
    case "set":
      return "set,<path>,<value>";
    default:
      return "first argument must be get, getint, set, add, subtract";
  }
}
function runPlaceholder() {
  if (args.length == 0) {
    return getUsage("no args");
  }
  else if (args.length == 1) {
    return getUsage(args[0]);
  }

  if (args.length == 2) {
    if (args[0].equals("get")) {
      return get(args[1]);
    }
    else if (args[0].equals("getint")) {
      return getInt(args[1]);
    }
  }
  else if (args.length == 3) {
    if (args[0].equals("set")) {
      return set(args[1], args[2]);
    }
    else if (args[0].equals("add")) {
      return add(args[1], args[2]);
    }
    else if (args[0].equals("subtract")) {
      return subtract(args[1], args[2]);
    }
  }
  return getUsage(args[0]);
}
runPlaceholder();