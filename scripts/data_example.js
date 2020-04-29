function set(path, data) {
    Data.set(path, data);
    return "";
}

function add(path, amount) {
    if (isNaN(amount)) {
        return "";
    }

    var current = Data.exists(path) ? Data.get(path) : 0;

    if (isNaN(current)) {
        current = 0;
    }

    current = parseInt(current) + parseInt(amount);
    Data.set(path, current.toFixed());
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
    return Data.exists(path)  ? Data.get(path) : "";
}

function getInt(path) {
    if (Data.get(path) != null) {
        var value = Data.get(path);
        return isNaN(value) ? 0 : parseInt(value).toFixed();
    }

    return 0;
}

function getUsage(action) {
    switch(action) {
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
            return "First argument must be get, getint, set, add, subtract";
    }
}

function runPlaceholder() {
    if (args.length === 0) {
        return getUsage("No arguments");
    }

    var action = args[0].toLowerCase();

    if (args.length === 1) {
        return getUsage(action);
    }

    var path = args[1];

    if (args.length === 2) {
        if (action.equals("get")) {
            return get(path);
        }

        if (action.equals("getint")) {
            return getInt(path);
        }
    }

    var value = args[2];

    if (args.length === 3) {
        if (action.equals("set")) {
            return set(path, value);
        }

        if (action.equals("add")) {
            return add(path, value);
        }

        if (action.equals("subtract")) {
            return subtract(path, value);
        }
    }

    return getUsage(action);
}

runPlaceholder();