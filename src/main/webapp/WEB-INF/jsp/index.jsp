<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: satke
  Date: 21.07.2018
  Time: 13:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>

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
                    <li class="isLazy isFolder" title="Bookmarks">
                        ${listValue}
                    </li>
                </c:forEach>
            </ul>
        </c:if>
    </div>
    <div style="float:right; width:350px;">
        <div id="divRejectAll" class="easytree-droppable easytree-reject"
             style="float: right; width: 80px; height: 80px; border: 1px solid red; margin: 3px;">
            <p>Trash</p>
        </div>
    </div>
    <script type="text/javascript">
        var currentNode = null;
        var allNodes = {};
        var firstIteration = 0;
        var xhr = new XMLHttpRequest();
        var easyTree = $('#demo_menu').easytree({
            enableDnd: true,
            dropped: moveNode,
            dropping: deleteNode,
            openLazyNode: openLazyNode
        });

        function openLazyNode(event, nodes, node, hasChildren) {
            if (hasChildren) { // don't call ajax if lazy node already has children
                return false;
            }
            if (firstIteration === 0) {
                for (var i = 0, size = nodes.length; i < size; i++) {
                    allNodes[nodes[i].id] = nodes[i].text;
                }
            }
            if (currentNode != null) {
                iteratesNodesAndChangeIt(currentNode.children);
            }
            node.lazyUrl = '/' + allNodes[node.id];
            currentNode = node;
            firstIteration = 1;
        }

        function iteratesNodesAndChangeIt(childrenOfCurrentNode) {
            var prefix = allNodes[currentNode.id];
            if (prefix === undefined) {
                prefix = currentNode.text;
            }
            for (var i = 0, size = childrenOfCurrentNode.length; i < size; i++) {
                allNodes[childrenOfCurrentNode[i].id] =
                    prefix + '->' + childrenOfCurrentNode[i].text;
            }
        }

        function moveNode(event, nodes, isSourceNode, source, isTargetNode, target) {
            iteratesNodesAndChangeIt(currentNode.children);
            if (isSourceNode && !isTargetNode) {
                xhr.open("PUT", '/' + allNodes[source.id] + '/' + allNodes[target.id], true);
                xhr.send();
                easyTree.addNode(source, target.id);
                easyTree.removeNode(source.id);
                easyTree.rebuildTree();
            }
        }

        function deleteNode(event, nodes, isSourceNode, source, isTargetNode, target, canDrop) {
            //iteratesNodesAndChangeIt(currentNode.children);
            if (isSourceNode && !canDrop && target && (!isTargetNode && target.id == 'divRejectAll')) {
                xhr.open("DELETE", '/' + allNodes[source.id], true);
                xhr.send();
                easyTree.removeNode(source.id);
                easyTree.rebuildTree();
            }
        }

        function addNode(name) {
            var nodes;
            if (Object.keys(allNodes).length === 0){
              nodes = easyTree.getAllNodes();
            } else {
              nodes = allNodes;
            }
            var node;
            for (var i = 0, size = Object.keys(nodes).length; i < size; i++) {
                var interMass = nodes[i].text.split('->');
                var nameNode = interMass[interMass.length - 1];
                if (name === nameNode) {
                    node = easyTree.getNode(nodes[i].id);
                }
            }
            var result = prompt("Enter folder name");

            var sourceNode = {};
            sourceNode.text = result;
            sourceNode.isFolder = true;

            easyTree.addNode(sourceNode, node.id);
            easyTree.rebuildTree();
            for (var i = 0, size = nodes.length; i < size; i++) {
                if (name === nodes[i].text)
                 allNodes[nodes[i].id] = nodes[i].text;
            }
        }

        function removeNodeX() {
            var currentlySelected = $('#lstNodes :selected').val();
            var node = easytree.getNode(currentlySelected);
            if (!node) {
                return;
            }

            easytree.removeNode(node.id);
            easytree.rebuildTree();
            loadSelectBox();
        }
    </script>
    <style>
        ul li:after {
            display: block;
            height: 100px;
            line-height: 100px;
        }

        ui li img {
            vertical-align: middle;
        }
    </style>
    </body>
    </html>
</doctype>