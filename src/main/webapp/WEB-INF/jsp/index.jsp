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
    <h2>Directory tree:</h2>
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
    </div>
    <div class="box" style="float:left; width:335px; margin-left:30px">
        <div class="box_content">
            Name folder: <input type="text" value="New_folder" style="width:200px" id="nodeText"/>
            <br/><br/>
            <select id="lstNodes"></select>
            <br/><br/>
            <button onclick="addNode(); return false;">Add folder</button>
            <button onclick="removeNodeX(); return false;">Remove folder</button>
            <button onclick="edit(); return false;">Edit folder name</button>
            <br/>
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
            openLazyNode: openLazyNode
        });

        $(document).ready(function () {
            loadSelectBox();
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

            if (allNodes[node.id] !== undefined) {
              node.lazyUrl = '/' + allNodes[node.id];
              loadSelectBox();
            } else {
                alert("An problem appear. Please reload a page");
            }
            currentNode = node;
            firstIteration = 1;
        }

        function iteratesNodesAndChangeIt(childrenOfCurrentNode) {
            if (childrenOfCurrentNode !== null) {
                var prefix = allNodes[currentNode.id];
                if (prefix === undefined) {
                    prefix = currentNode.text;
                }
                for (var i = 0, size = childrenOfCurrentNode.length; i < size; i++) {
                    allNodes[childrenOfCurrentNode[i].id] =
                        prefix + '->' + childrenOfCurrentNode[i].text;
                }
            }
        }

        function moveNode(event, nodes, isSourceNode, source, isTargetNode, target) {
            if (allNodes[source.id] !== undefined && allNodes[target.id] !== undefined) {
              xhr.open("PUT", '/' + allNodes[source.id] + '/' + allNodes[target.id], true);
              xhr.send();
              easyTree.addNode(source, target.id);
              var childrenWithMovingNode = easyTree.getNode(target.id).children;
              for (var i = 0, size = childrenWithMovingNode.length; i < size; i++) {
                  if (childrenWithMovingNode[i].text === source.text){
                      delete allNodes[source.id];
                      allNodes[childrenWithMovingNode[i].id] = allNodes[target.id] + '->' + source.text;
                  }
              }
              easyTree.removeNode(source.id);
              easyTree.rebuildTree();
            } else {
                alert("An problem appear. Please push another folder in tree");
            }

        }

        function addNode() {
            var sourceNode = {};
            sourceNode.text = $('#nodeText').val();
            sourceNode.isFolder = true;
            var targetId = $('#lstNodes :selected').val();
            var node = easyTree.getNode(targetId);

            xhr.open("PUT", '/' + allNodes[node.id] + "->" + sourceNode.text, true);
            xhr.send();
            easyTree.addNode(sourceNode, targetId);
            easyTree.rebuildTree();
            loadSelectBox();
        }

        function loadSelectBox() {
            var select = $('#lstNodes')[0];
            var currentlySelected = $('#lstNodes :selected').val();

            select.length = 0; // clear select box

            var root = new Option();
            root.text = 'Root';
            root.value = '';
            select.add(root);

            var allNodes = easyTree.getAllNodes();
            addOptions(allNodes, select, '-', currentlySelected);
        }

        function addOptions(nodes, select, prefix, currentlySelected) {
            var i = 0;
            for (i = 0; i < nodes.length; i++) {

                var option = new Option();
                option.text = prefix + ' ' + nodes[i].text;
                option.value = nodes[i].id;
                option.selected = currentlySelected === nodes[i].id;
                select.add(option);

                if (nodes[i].children && nodes[i].children.length > 0) {
                    addOptions(nodes[i].children, select, prefix + '-', currentlySelected);
                }
            }
        }

        function removeNodeX() {
            //iteratesNodesAndChangeIt(currentNode.children);
            var currentlySelected = $('#lstNodes :selected').val();
            var node = easyTree.getNode(currentlySelected);
            if (!node) {
                return;
            }

            xhr.open("DELETE", '/' + allNodes[node.id], true);
            xhr.send();

            delete allNodes[node.id];
            easyTree.removeNode(node.id);
            easyTree.rebuildTree();
            loadSelectBox();
        }

        function edit() {
            var nameNode = $('#nodeText').val();
            var currentlySelected = $('#lstNodes :selected').val();
            var node = easyTree.getNode(currentlySelected);
            if (!node) {
                return;
            }
            var prefix = allNodes[node.id];
            if (prefix === undefined) {
                prefix = node.text;
            }

            var resultDirectory = "";
            var strings = prefix.split("->");
            strings[strings.length - 1] = "";
            for (var i = 0; i < strings.length; i++) {
                if (resultDirectory !== ""){
                  resultDirectory = resultDirectory + "->" + strings[i];
                } else {
                    resultDirectory = strings[i];
                }
            }

            xhr.open("POST", prefix + '/' + resultDirectory + nameNode, true);
            xhr.send();
            allNodes[node.id] = resultDirectory + nameNode;
            node.text = nameNode;
            easyTree.rebuildTree();
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