var url = new java.net.URL(args[0]);
var conn = url.openConnection();
var reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
var response = "";
while ((line = reader.readLine()) != null)
    response += line;
var json = JSON.parse(response);
var path = args[1].split(".");
for (var i in path)
    json = json[path[i]];
json;
