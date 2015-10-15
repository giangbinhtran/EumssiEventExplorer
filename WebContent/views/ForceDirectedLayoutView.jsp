
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">


    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
            
            <title>EventSense - Word Cloud </title>
            <link rel="stylesheet" type="text/css" href="CSS/style.css">
            <link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600" rel="stylesheet" type="text/css" />
            <link href='http://fonts.googleapis.com/css?family=Abel|Satisfy' rel='stylesheet' type='text/css'>
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
            <script>
                $(document).ready(function() {
                	$(".story").hover(function(){
    	                $(this).css("color","#A31919");
    	                $(this).css("text-decoration","none");
    	                },
    	                function(){
    	                $(this).css("color","#0040FF");
    	                $(this).css("text-decoration","underline");
    	              });
    	        	
                //var getVar = location.search.replace('?', '').split('=');
                //$('div[id$=' + getVar[1] + ']')[0].scrollIntoView();
            });
            </script>
            <style type="text/css" >
				div#container{width:900px ; margin-left: auto; margin-right: auto;}
				div#menus {width:67%; float:left;position: relative;}
				div#content {width:32%; float:left;position: relative;}
				div#summary{width:80% ; margin-left: auto; margin-right: auto; position: relative;}	
						.rightfloat
		            {
		                color: red;
		                background-color: #BBBBBB;
		                float: right;
		                width: 200px;
		            }
		
		            .left {
		                font-size: 20pt;
		            }
		
		            .separator {
		                clear: both;
		                width: 100%;
		                border-top: 1px solid black;
		            }		
			</style>
            <style>
				a:link {text-decoration: none;}
				a:visited {text-decoration: none;}
				a:active {text-decoration: blink;}
				a:hover {text-decoration: none;}
			</style> 
			
			<style>
				.link {
				  fill: none;
				  stroke: #cccccc;
				  stroke-width: 1.1px;
				}
				
				.node circle {
				  fill: #ccc;
				  stroke: #fff;
				  stroke-width: 1.5px;
				}
				
				text {
				  font: 10px sans-serif;
				  pointer-events: none;
				}
        </style>
        <!-- The two scripts below are added for search functionality -->
        <script type='text/javascript' src="http://code.jquery.com/ui/1.11.0/jquery-ui.min.js"> </script>
		<script type='text/javascript' src="http://code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css"> </script>
		</head>
	<body>

	<div class="container">
		<!-- start #header -->
		<s:include value="/views/header.jsp"></s:include>
		<!-- end #header --> 
		
		
		
		 
		<div style="margin-left: 300px">
		 	<h3 class="mainhead">
		 	Force-Directed Layout Visualization</h3>
		<br/>
		<br/>
		</div>
		<!-- Search widget -->
		<div class="ui-widget">
   <input id="search">
    <button type="button" onclick="searchNode()">Search</button>
</div>

		<div>
		
		<script src="scripts/d3/d3.js"></script>
		<script src="scripts/d3/d3.layout.cloud.js"></script>
		<script src="scripts/d3/d3.layout.js"></script>
		<script src="scripts/d3/d3.geom.js"></script>
		
		<script src="https://raw.github.com/douglascrockford/JSON-js/master/json2.js"> </script>
		
		<!-- Force-based layout -->
		<script type="text/javascript" src="http://mbostock.github.com/d3/d3.js?2.6.0"></script>
		<script type="text/javascript" src="http://mbostock.github.com/d3/d3.layout.js?2.6.0"></script>

		<script type="text/javascript">
		var dataobject = '<s:property escape = "false"  value="tfjson"/>';
		var cooccurenceJson = '<s:property escape = "false"  value="coocc"/>';
		//var cooccurenceJson = [{"weight":679,"source":"germany","target":"germany"},{"weight":581,"source":"germany","target":"today"},{"weight":446,"source":"germany","target":"people"},{"weight":279,"source":"germany","target":"edition"},{"weight":275,"source":"germany","target":"german"},{"weight":177,"source":"germany","target":"talking"},{"weight":113,"source":"germany","target":"made"},{"weight":76,"source":"germany","target":"time"},{"weight":64,"source":"germany","target":"coming"},{"weight":43,"source":"germany","target":"show"},{"weight":39,"source":"germany","target":"berlin"},{"weight":23,"source":"germany","target":"world"},{"weight":22,"source":"germany","target":"living"},{"weight":22,"source":"germany","target":"places"},{"weight":22,"source":"germany","target":"years"},{"weight":22,"source":"germany","target":"year"},{"weight":18,"source":"germany","target":"business"},{"weight":16,"source":"germany","target":"warm"},{"weight":16,"source":"germany","target":"thousand"},{"weight":15,"source":"germany","target":"back"},{"weight":15,"source":"germany","target":"country"},{"weight":15,"source":"germany","target":"work"},{"weight":15,"source":"germany","target":"visit"},{"weight":14,"source":"germany","target":"day"},{"weight":13,"source":"germany","target":"great"},{"weight":11,"source":"germany","target":"life"},{"weight":10,"source":"germany","target":"hundred"},{"weight":10,"source":"germany","target":"guess"},{"weight":10,"source":"germany","target":"europe"},{"weight":9,"source":"germany","target":"peter"},{"weight":7,"source":"germany","target":"crisis"},{"weight":7,"source":"germany","target":"man"},{"weight":6,"source":"germany","target":"twenty"},{"weight":6,"source":"germany","target":"city"},{"weight":6,"source":"germany","target":"good"},{"weight":6,"source":"germany","target":"young"},{"weight":5,"source":"germany","target":"find"},{"weight":5,"source":"germany","target":"meet"},{"weight":5,"source":"germany","target":"make"},{"weight":4,"source":"germany","target":"european"},{"weight":4,"source":"germany","target":"home"},{"weight":4,"source":"germany","target":"joining"},{"weight":3,"source":"germany","target":"talk"},{"weight":264,"source":"today","target":"germany"},{"weight":161,"source":"today","target":"today"},{"weight":156,"source":"today","target":"people"},{"weight":79,"source":"today","target":"edition"},{"weight":46,"source":"today","target":"german"},{"weight":44,"source":"today","target":"talking"},{"weight":33,"source":"today","target":"made"},{"weight":27,"source":"today","target":"time"},{"weight":18,"source":"today","target":"coming"},{"weight":14,"source":"today","target":"show"},{"weight":14,"source":"today","target":"berlin"},{"weight":12,"source":"today","target":"world"},{"weight":11,"source":"today","target":"living"},{"weight":9,"source":"today","target":"places"},{"weight":8,"source":"today","target":"years"},{"weight":8,"source":"today","target":"year"},{"weight":6,"source":"today","target":"business"},{"weight":6,"source":"today","target":"warm"},{"weight":4,"source":"today","target":"thousand"},{"weight":3,"source":"today","target":"back"},{"weight":3,"source":"today","target":"country"},{"weight":3,"source":"today","target":"work"},{"weight":3,"source":"today","target":"visit"},{"weight":3,"source":"today","target":"day"},{"weight":264,"source":"people","target":"germany"},{"weight":31,"source":"people","target":"today"},{"weight":14,"source":"people","target":"people"},{"weight":11,"source":"people","target":"edition"},{"weight":9,"source":"people","target":"german"},{"weight":8,"source":"people","target":"talking"},{"weight":7,"source":"people","target":"made"},{"weight":6,"source":"people","target":"time"},{"weight":5,"source":"people","target":"coming"},{"weight":4,"source":"people","target":"show"},{"weight":4,"source":"people","target":"berlin"},{"weight":4,"source":"people","target":"world"},{"weight":4,"source":"people","target":"living"},{"weight":4,"source":"people","target":"places"},{"weight":3,"source":"people","target":"years"},{"weight":3,"source":"people","target":"year"},{"weight":3,"source":"people","target":"business"},{"weight":3,"source":"people","target":"warm"},{"weight":3,"source":"people","target":"thousand"},{"weight":3,"source":"people","target":"back"},{"weight":229,"source":"edition","target":"germany"},{"weight":160,"source":"edition","target":"today"},{"weight":136,"source":"edition","target":"people"},{"weight":25,"source":"edition","target":"edition"},{"weight":24,"source":"edition","target":"german"},{"weight":10,"source":"edition","target":"talking"},{"weight":7,"source":"edition","target":"made"},{"weight":6,"source":"edition","target":"time"},{"weight":5,"source":"edition","target":"coming"},{"weight":5,"source":"edition","target":"show"},{"weight":4,"source":"edition","target":"berlin"},{"weight":3,"source":"edition","target":"world"},{"weight":3,"source":"edition","target":"living"},{"weight":3,"source":"edition","target":"places"},{"weight":13,"source":"german","target":"germany"},{"weight":12,"source":"german","target":"today"},{"weight":12,"source":"german","target":"people"},{"weight":8,"source":"german","target":"edition"},{"weight":8,"source":"german","target":"german"},{"weight":7,"source":"german","target":"talking"},{"weight":6,"source":"german","target":"made"},{"weight":6,"source":"german","target":"time"},{"weight":6,"source":"german","target":"coming"},{"weight":5,"source":"german","target":"show"},{"weight":5,"source":"german","target":"berlin"},{"weight":4,"source":"german","target":"world"},{"weight":4,"source":"german","target":"living"},{"weight":4,"source":"german","target":"places"},{"weight":4,"source":"german","target":"years"},{"weight":4,"source":"german","target":"year"},{"weight":4,"source":"german","target":"business"},{"weight":3,"source":"german","target":"warm"},{"weight":3,"source":"german","target":"thousand"},{"weight":3,"source":"german","target":"back"},{"weight":3,"source":"german","target":"country"},{"weight":3,"source":"german","target":"work"},{"weight":20,"source":"talking","target":"germany"},{"weight":14,"source":"talking","target":"today"},{"weight":6,"source":"talking","target":"people"},{"weight":4,"source":"talking","target":"edition"},{"weight":3,"source":"talking","target":"german"},{"weight":3,"source":"talking","target":"talking"},{"weight":10,"source":"made","target":"germany"},{"weight":7,"source":"made","target":"today"},{"weight":6,"source":"made","target":"people"},{"weight":6,"source":"made","target":"edition"},{"weight":5,"source":"made","target":"german"},{"weight":4,"source":"made","target":"talking"},{"weight":3,"source":"made","target":"made"},{"weight":3,"source":"made","target":"time"},{"weight":3,"source":"made","target":"coming"},{"weight":3,"source":"made","target":"show"},{"weight":39,"source":"time","target":"germany"},{"weight":13,"source":"time","target":"today"},{"weight":9,"source":"time","target":"people"},{"weight":7,"source":"time","target":"edition"},{"weight":6,"source":"time","target":"german"},{"weight":6,"source":"time","target":"talking"},{"weight":4,"source":"time","target":"made"},{"weight":3,"source":"time","target":"time"},{"weight":3,"source":"time","target":"coming"},{"weight":3,"source":"time","target":"show"},{"weight":3,"source":"time","target":"berlin"},{"weight":3,"source":"time","target":"world"},{"weight":3,"source":"time","target":"living"},{"weight":3,"source":"time","target":"places"},{"weight":3,"source":"time","target":"years"},{"weight":3,"source":"time","target":"year"},{"weight":20,"source":"coming","target":"germany"},{"weight":11,"source":"coming","target":"today"},{"weight":5,"source":"coming","target":"people"},{"weight":4,"source":"coming","target":"edition"},{"weight":4,"source":"coming","target":"german"},{"weight":3,"source":"coming","target":"talking"},{"weight":3,"source":"coming","target":"made"},{"weight":3,"source":"coming","target":"time"},{"weight":3,"source":"coming","target":"coming"},{"weight":3,"source":"coming","target":"show"},{"weight":127,"source":"show","target":"germany"},{"weight":13,"source":"show","target":"today"},{"weight":13,"source":"show","target":"people"},{"weight":7,"source":"show","target":"edition"},{"weight":5,"source":"show","target":"german"},{"weight":5,"source":"show","target":"talking"},{"weight":3,"source":"show","target":"made"},{"weight":3,"source":"show","target":"time"},{"weight":10,"source":"berlin","target":"germany"},{"weight":10,"source":"berlin","target":"today"},{"weight":4,"source":"berlin","target":"people"},{"weight":4,"source":"berlin","target":"edition"},{"weight":3,"source":"berlin","target":"german"},{"weight":3,"source":"berlin","target":"talking"},{"weight":3,"source":"berlin","target":"made"},{"weight":3,"source":"berlin","target":"time"},{"weight":3,"source":"berlin","target":"coming"},{"weight":7,"source":"world","target":"germany"},{"weight":5,"source":"world","target":"today"},{"weight":263,"source":"living","target":"germany"},{"weight":4,"source":"places","target":"germany"},{"weight":22,"source":"years","target":"germany"},{"weight":14,"source":"years","target":"today"},{"weight":7,"source":"years","target":"people"},{"weight":6,"source":"years","target":"edition"},{"weight":3,"source":"years","target":"german"},{"weight":8,"source":"year","target":"germany"},{"weight":7,"source":"year","target":"today"},{"weight":4,"source":"year","target":"people"},{"weight":3,"source":"year","target":"edition"},{"weight":3,"source":"year","target":"german"},{"weight":8,"source":"business","target":"germany"},{"weight":7,"source":"business","target":"today"},{"weight":4,"source":"business","target":"people"},{"weight":3,"source":"business","target":"edition"},{"weight":3,"source":"business","target":"german"},{"weight":3,"source":"business","target":"talking"},{"weight":9,"source":"warm","target":"germany"},{"weight":3,"source":"warm","target":"today"},{"weight":48,"source":"thousand","target":"germany"},{"weight":10,"source":"thousand","target":"today"},{"weight":4,"source":"thousand","target":"people"},{"weight":3,"source":"thousand","target":"edition"},{"weight":3,"source":"thousand","target":"german"},{"weight":16,"source":"back","target":"germany"},{"weight":5,"source":"back","target":"today"},{"weight":4,"source":"back","target":"people"},{"weight":3,"source":"back","target":"edition"},{"weight":9,"source":"country","target":"germany"},{"weight":8,"source":"work","target":"germany"},{"weight":8,"source":"work","target":"today"},{"weight":4,"source":"visit","target":"germany"},{"weight":3,"source":"visit","target":"today"},{"weight":3,"source":"visit","target":"people"},{"weight":9,"source":"day","target":"germany"},{"weight":4,"source":"day","target":"today"},{"weight":3,"source":"day","target":"people"},{"weight":6,"source":"life","target":"germany"},{"weight":5,"source":"life","target":"today"},{"weight":3,"source":"life","target":"people"},{"weight":3,"source":"life","target":"edition"},{"weight":12,"source":"hundred","target":"germany"},{"weight":6,"source":"europe","target":"germany"},{"weight":5,"source":"europe","target":"today"},{"weight":134,"source":"peter","target":"germany"},{"weight":9,"source":"man","target":"germany"},{"weight":4,"source":"man","target":"today"},{"weight":4,"source":"man","target":"people"},{"weight":3,"source":"man","target":"edition"},{"weight":4,"source":"good","target":"germany"},{"weight":3,"source":"good","target":"today"},{"weight":9,"source":"young","target":"germany"},{"weight":3,"source":"home","target":"germany"},{"weight":10,"source":"joining","target":"germany"},{"weight":8,"source":"talk","target":"germany"},{"weight":7,"source":"talk","target":"today"}];

		//var dataobject = [{"text":"germany","size":3753},{"text":"today","size":1196},{"text":"people","size":706},{"text":"edition","size":690},{"text":"german","size":681},{"text":"talking","size":599},{"text":"made","size":570},{"text":"time","size":428},{"text":"coming","size":413},{"text":"show","size":376},{"text":"berlin","size":364},{"text":"world","size":330},{"text":"living","size":319},{"text":"places","size":316},{"text":"years","size":278},{"text":"year","size":260},{"text":"business","size":250},{"text":"warm","size":230},{"text":"thousand","size":213},{"text":"back","size":211},{"text":"country","size":211},{"text":"work","size":203},{"text":"visit","size":197},{"text":"day","size":196},{"text":"great","size":193},{"text":"life","size":180},{"text":"hundred","size":179},{"text":"guess","size":174},{"text":"europe","size":174},{"text":"peter","size":172},{"text":"crisis","size":168},{"text":"man","size":167},{"text":"twenty","size":166},{"text":"city","size":162},{"text":"good","size":158},{"text":"young","size":157},{"text":"find","size":157},{"text":"meet","size":157},{"text":"make","size":155},{"text":"european","size":153},{"text":"home","size":152},{"text":"joining","size":146},{"text":"talk","size":143},{"text":"family","size":142},{"text":"craven","size":135},{"text":"week","size":132},{"text":"music","size":131},{"text":"discover","size":128},{"text":"long","size":119},{"text":"international","size":117},{"text":"children","size":116}];

		var nodes = {};
		var size = 500;
		
		var scale = 1;
		var tf = JSON.parse(dataobject);
		
		var max_size = size/tf.length;
		for (var i in tf) { // get the scale 
		        scale = 8 * max_size / tf[i].size;
		        break;
		}

		//update 
		
		for (var i in tf) {
		       tf[i].size = 12 + tf[i].size * scale;
		       nodes[tf[i].text] = {name: tf[i].text, size: tf[i].size, color: "purple"};
		}
		
	
		//Set up color scale
		var color = d3.scale.category10();
		var links = JSON.parse(cooccurenceJson);
			

			// Compute the distinct nodes from the links.
			links.forEach(function(link) {
				link.source = nodes[link.source];
				link.target = nodes[link.target];
			  //link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
			  //link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
			}); 

			// SVG constants
			var width = 1060,
			    height = 800;

			// Setup the force layout
			var force = d3.layout.force()
			    .nodes(d3.values(nodes))
			    .links(links)
			    .size([width, height])
			    .linkDistance(200)
			    .charge(-300)
			    .on("tick", tick)
			    .start();

			// Append SVG to the html, with defined constants
			var svg = d3.select("body").append("svg")
			    .attr("width", width)
			    .attr("height", height);


			// Code for pinnable nodes
			var node_drag = d3.behavior.drag()
					.on("dragstart", dragstart)
					.on("drag", dragmove)
					.on("dragend", dragend);
				function dragstart(d, i) {
					force.stop() // stops the force auto positioning before you start dragging
				}
				function dragmove(d, i) {
					d.px += d3.event.dx;
					d.py += d3.event.dy;
					d.x += d3.event.dx;
					d.y += d3.event.dy;
				}
				function dragend(d, i) {
					d.fixed = true; // of course set the node to fixed so the force doesn't include the node in its auto positioning stuff
					d3.select(this).select("text").style("fill", "#FF8800"); // change color to orange
					force.resume();
				}
				function releasenode(d) {
					d.fixed = false; // of course set the node to fixed so the force doesn't include the node in its auto positioning stuff
					d3.select(this).select("text").style("fill", function (d,i) { return color(i); }); // set back to original color
					//force.resume();
				}
    
			// Create lines for the links, without location
			// Note that link attributes are defined in css
			var link = svg.selectAll(".link")
			    .data(force.links())
			  .enter().append("line")
			    .attr("class", "link");


			// Add circles to nodes and call the drag function
			var node = svg.selectAll(".node")
			    .data(force.nodes())
			  .enter().append("g")
			    .attr("class", "node")
			    .on("mouseover", mouseover)
			    .on("mouseout", mouseout)
			    .on("click", mouseclick)
			    .on('dblclick', releasenode) //added
			    .call(node_drag); //added
			    //.call(force.drag)

			// Circles are needed for dragging, find out how to get rid of them
			node.append("circle")
			   .attr("r", 6)
		       .style("fill", "#D4D94A");

				// Now we also append text			
				var TextColorScale = d3.scale.linear()
				.domain([0, d3.max(dataobject, function(d) { return d.size; })])
				.range([255,0]);

			node.append("text")
			    .attr("x", 0)
			    .attr("dy", ".35em")
			    .style("font-size", function(d) { return d.size + "px"; })
			    .style("font-family", "Impact")
			    .style("fill", function (d,i) { return color(i); })
			    .text(function(d) { return d.name; });




			

			// This function magically does the force-directed layout
			function tick() {
			  link
			      .attr("x1", function(d) { return d.source.x; })
			      .attr("y1", function(d) { return d.source.y; })
			      .attr("x2", function(d) { return d.target.x; })
			      .attr("y2", function(d) { return d.target.y; })
			      ;

			  node
			      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")" ;
			  node.each(collide(0.5)); //Added 
					  });
			}

			// Text becomes larger on mouseover (not really needed)
			function mouseover() {
			  d3.select(this).select("circle").transition()
			      .duration(750)
			      .attr("r", 16);
			  d3.select(this).select("text").style("font-size", function(d) { return (d.size + 20)  + "px"; });
			}

			function mouseout() {
			  d3.select(this).select("circle").transition()
			      .duration(750)
			      .attr("r", 8);
			 d3.select(this).select("text").style("font-size", function(d) { return (d.size)  + "px"; });
			}
			
			// Follow link when clicked on text
			function mouseclick() {
				linktext = d3.select(this).select("text").text();
				// For now, we open just the Google search results - should be replaced by new word cloud on this entity
				window.open("https://www.google.com/?gws_rd=ssl#q="+linktext);
			}
			
			// Search functionality (copied/paste from http://www.coppelia.io/2014/07/an-a-to-z-of-extra-features-for-the-d3-force-layout/
			
			var optArray = [];
			for (var i = 0; i < graph.nodes.length - 1; i++) {
				optArray.push(graph.nodes[i].name);
			}
			optArray = optArray.sort();
			$(function () {
				$("#search").autocomplete({
					source: optArray
				});
			});
			function searchNode() {
				//find the node
				var selectedVal = document.getElementById('search').value;
				var node = svg.selectAll(".node");
				if (selectedVal == "none") {
					node.style("stroke", "white").style("stroke-width", "1");
				} else {
					var selected = node.filter(function (d, i) {
						return d.name != selectedVal;
					});
					selected.style("opacity", "0");
					var link = svg.selectAll(".link")
					link.style("opacity", "0");
					d3.selectAll(".node, .link").transition()
						.duration(5000)
						.style("opacity", 1);
				}
			}

			// Collission Detection
			
			var padding = 1, // separation between circles
				radius=8;
			function collide(alpha) {
			  var quadtree = d3.geom.quadtree(graph.nodes);
			  return function(d) {
				var rb = 2*radius + padding,
					nx1 = d.x - rb,
					nx2 = d.x + rb,
					ny1 = d.y - rb,
					ny2 = d.y + rb;
				quadtree.visit(function(quad, x1, y1, x2, y2) {
				  if (quad.point && (quad.point !== d)) {
					var x = d.x - quad.point.x,
						y = d.y - quad.point.y,
						l = Math.sqrt(x * x + y * y);
					  if (l < rb) {
					  l = (l - rb) / l * alpha;
					  d.x -= x *= l;
					  d.y -= y *= l;
					  quad.point.x += x;
					  quad.point.y += y;
					}
				  }
				  return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
				});
			  };
			}

			</script>
			</div>
   </div>  
	</body>
</html>
