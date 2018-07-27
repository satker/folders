<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
        <c:if test="${not empty list}">

            <ul>
                <c:forEach var="listValue" items="${list}">
                    <li class="isLazy isFolder">${listValue}</li>
                </c:forEach>
            </ul>

        </c:if>
    </div>
    <script>
        var currentNode = null;
        //var allNodes = null;

        function openLazyNode(event, nodes, node, hasChildren) {
            // if (allNodes != null) {
            //     nodes = allNodes;
            // }
            if (hasChildren) { // don't call ajax if lazy node already has children
                return false;
            }
            if (currentNode != null) {
                var childrenOfCurrentNode = currentNode.children;
                iteratesNodesAndChangeIt(nodes, childrenOfCurrentNode, node);
            }
            node.lazyUrl = '/' + node.text; // must be set here or when the tree is initialised
            currentNode = node;
            //allNodes = nodes;
        }

        function iteratesNodesAndChangeIt(nodes, childrenOfCurrentNode, node) {
            for (var i = 0, size = childrenOfCurrentNode.length; i < size; i++) {
                childrenOfCurrentNode[i].text = currentNode.text + '-' + childrenOfCurrentNode[i].text;
                if (node.id === childrenOfCurrentNode[i].id) {
                    node.text = childrenOfCurrentNode[i].text;
                }
            }

        }

        var easyTree = $('#demo_menu').easytree({
            openLazyNode: openLazyNode
        });
    </script>
    </body>
    </html>
</doctype>