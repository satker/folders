<%--
  Created by IntelliJ IDEA.
  User: satke
  Date: 21.07.2018
  Time: 13:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<doctype html>
    <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <script src="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
        <script src="http://www.easyjstree.com/Scripts/jquery.easytree.min.js"></script>

    </head>
    <body>
    <div id="demo_menu" style="float:left; width:250px;">
        <ul>
            <li>First Node</li>
            <li class="isLazy isFolder">Lazy Node</li>
        </ul>
    </div>
    <script>
        var counter = 0;
        function openLazyNode(event, nodes, node, hasChildren) {
            if (hasChildren) { // don't call ajax if lazy node already has children
                return false;
            }
            counter++;
            node.lazyUrl = 'exercise.html'; // must be set here or when the tree is initialised
            node.lazyUrlJson = JSON.stringify({ text: counter }); // any json object here (optional)
            //node.lazyUrlJson = "{ text: " + counter + " } "; // IE 6/7 compatible
        }

        var easyTree = $('#demo_menu').easytree({
            openLazyNode: openLazyNode
        });

    </script>
    </body>
    </html>
