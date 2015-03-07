<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style type="text/css">
html, body, #map-canvas {
	height: 100%;
	margin: 0;
	padding: 0;
}
</style>
<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBp9F2ud5Gq2VH7YBiLX3LOo4jSIOrYaRc">
	
</script>
<script type="text/javascript">
	function initialize() {
		var mapOptions = {
			center : {
				lat : 53.3356007,
				lng : -6.2356033
			},
			zoom : 11
		};
		var map = new google.maps.Map(document.getElementById('map-canvas'),
				mapOptions);
		
		var lats = new Array();
		var longs = new Array();
		var titles = new Array();
		var markers = new Array();
		
		<c:forEach items="${reports}" var="report">
			lats.push('<c:out value="${report.getLatitude()}" />');
			longs.push('<c:out value="${report.getLongitude()}"/>');
			titles.push('<c:out value="${report.getName()}" />');
		
		</c:forEach>
		
		for(var i = 0; i < titles.length; i++) {
			markers.push(new google.maps.Marker({
			      position: new google.maps.LatLng(parseFloat(lats[i]), parseFloat(longs[i])),
			      map: map,
			      title: titles[i]
			  }));
		}
	}
	google.maps.event.addDomListener(window, 'load', initialize);
</script>
</head>
<body>
	<div id="map-canvas"></div>
</body>
</html>