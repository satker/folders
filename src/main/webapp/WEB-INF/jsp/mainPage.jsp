<%--
  Created by IntelliJ IDEA.
  User: satke
  Date: 05.08.2018
  Time: 18:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Main page</title>
</head>
<body>

Directory for search: <input id="directory">
<button id="search">Start</button>

<script>
    var xhr = new XMLHttpRequest();

    function say_hi() {
        var directory = document.getElementById('directory').value;

        var strings = directory.split("\\");
        var parseDirectory  = "";
        for (var i = 0; i < strings.length; i++) {
            if (parseDirectory !== ""){
                parseDirectory = parseDirectory + "->" + strings[i];
            } else {
                parseDirectory = strings[i];
            }
        }

        var strings1 = parseDirectory.split(":->");
        var parseDirectory1  = "";
        for (var i1 = 0; i1 < strings1.length; i1++) {
            if (parseDirectory1 !== ""){
                parseDirectory1 = parseDirectory1 + "-->" + strings1[i1];
            } else {
                parseDirectory1 = strings1[i1];
            }
        }

        xhr.open("POST", '/enterDirectory/' + parseDirectory1, true);
        xhr.send();
    }

    document.getElementById('search').addEventListener('click', say_hi);
</script>

</body>
</html>
