var map;
var date = new Date();
var autocomplete = [];
var polylines = [];

function initMap() {
    map = L.map('map').setView([33, 44], 2);
	
	L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
		attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
		maxZoom: 18,
		id: 'mapbox.streets',
		accessToken: 'pk.eyJ1IjoibWFuaW96YWtvIiwiYSI6ImNpbmV4ZzFhdjAwN3l3emx5bjYyZWdranIifQ.TK1h3WP9gl91Tpirc9zIZg'
	}).addTo(map);

	document.getElementById('date_label_1').value = date.getMonth() + 1;
	document.getElementById('date_label_2').value = date.getDate();
	document.getElementById('calendar_year').value = date.getFullYear();
	document.getElementById('time_hours').value = date.getHours() >= 10 ? date.getHours() : '0' + date.getHours();
	document.getElementById('time_mins').value = date.getMinutes() >= 10 ? date.getMinutes() : '0' + date.getMinutes();
}

function addMarker(location, content) {
    var marker = new L.marker(location).bindPopup("<div class=\"infoWindowForm\">" + 
					"<h3>" + content + "</h3>" +
					"<button class=\"fromButton\" onclick=\"fromButtonClick('" + content + "')\">From</button>" +
					"<button class=\"toButton\" onclick=\"toButtonClick('" + content + "')\">To</button>"
    ).addTo(map);
}

function setAutocomplete(namesArray) {
    for(var i = 0; i < namesArray.length; i++) {
        autocomplete.push(namesArray[i]);
    }
}

function setMarkers(positions) {
    for(var i=0; i < positions.length; i++) {
        var location = [parseFloat(positions[i].lat), parseFloat(positions[i].lon)];
        addMarker(location, positions[i].city);
    }
}

function setPaths(paths) {
    var div = document.getElementById('route_container');
    var form = document.getElementById('TruckForm');
    var table_body = document.getElementById('table_body');
    var new_tbody = document.createElement('tbody');
    new_tbody.setAttribute('id', 'table_body');
    var hr = document.createElement('hr');
    for(var i = 0; i < paths.length; i++) {
        var color = getRandomColor();
        for(var j = 0; j < paths[i].length; j++) {
            var Route = paths[i][j];
            new_tbody.appendChild(createTableRow([Route.src.city + " -> " + Route.dest.city, "( " + Route.Route.RouteNumber + " )"], color));
            new_tbody.appendChild(createTableRow(["Start: " + Route.Route.date, "Length: " + Route.Route.length], color));
            drawPolyline(L.latLng(Route.src.lat, Route.src.lon), L.latLng(Route.dest.lat, Route.dest.lon), color);
            new_tbody.appendChild(createSeparator('transparent', '15px'));
        }
        new_tbody.appendChild(createSeparator('black', '2px'));
    }
    table_body.parentNode.replaceChild(new_tbody, table_body);
    div.style.display = 'block';
    form.style.display = 'none';
}

function drawPolyline(source, destination, color) {
    var coords = [source, destination];
    var polyline = L.polyline(coords, {color: color}).addTo(map);
    polylines.push(polyline);
}

function clearPolylines() {
    for(var i = 0; i < polylines.length; i++) {
        map.removeLayer(polylines[i]);
    }
    polylines = [];
}

function fromButtonClick(markerTitle) {
	var forTextBox = document.getElementById('from_textbox');
	var toTextBox = document.getElementById('to_textbox');
	if(toTextBox.value != markerTitle) {
		forTextBox.value = markerTitle;
	}
}

function createTableRow(cells, color) {
    var tr = document.createElement('tr');
    var len = cells.length;
    var counter = 0;
    for(var i = 0; i < len; i++) {
        var td = tr.insertCell();
        td.appendChild(document.createTextNode(cells[i]));
        counter++;
    }
    for(var j = 0; j < 3 - counter; j++) {
        var td = tr.insertCell();
        td.setAttribute('bgcolor', 'transparent');
    }
    var colorCell = tr.insertCell();
    colorCell.setAttribute('bgcolor', color);
    colorCell.setAttribute('width', '50px');
    return tr;
}

function createSeparator(color, height) {
    var tr = document.createElement('tr');
    for(var i = 0; i < 4; i++) {
        var td = tr.insertCell();
        td.setAttribute('bgcolor', color);
        td.setAttribute('height', height);
    }
    return tr;
}

function toButtonClick(markerTitle) {
	var forTextBox = document.getElementById('from_textbox');
	var toTextBox = document.getElementById('to_textbox');
	if(forTextBox.value != markerTitle) {
		toTextBox.value = markerTitle;
	}
}

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function showForm() {
    var forTextBox = document.getElementById('from_textbox');
	var toTextBox = document.getElementById('to_textbox');
    var div = document.getElementById('route_container');
    var form = document.getElementById('TruckForm');
    
    div.style.display = 'none';
    form.style.display = 'block';
    forTextBox.value = "";
    toTextBox.value = "";
    clearPolylines();
}