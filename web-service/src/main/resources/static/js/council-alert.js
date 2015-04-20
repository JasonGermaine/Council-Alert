councilAlertApp = angular.module('councilalert', [ 'ngRoute', 'ui.bootstrap', 'uiGmapgoogle-maps', 'tableSort', 'chart.js' ]);

councilAlertApp.factory('LocalStorage', function() {
	var tokenKey = 'oauth2_token';
	var emailKey = 'user_email';
	return {		
		storeToken : function(token) {
			return localStorage.setItem(tokenKey, token);
		},
		retrieveToken : function() {
			return localStorage.getItem(tokenKey);
		},
		storeEmail : function(email) {
			return localStorage.setItem(emailKey, email);
		},
		retrieveEmail : function() {
			return localStorage.getItem(emailKey);
		},
		clear : function() {
			localStorage.removeItem(tokenKey);
			localStorage.removeItem(emailKey);
			return;
		},
		getHeader : function() {
			var config = {
					headers : { 
						"Authorization" : localStorage.getItem(tokenKey),
						"Accept" : "application/json"
					}
			};
			return config;
		}
	};
});

councilAlertApp.factory('DistanceFactory', function() {
	return {		
		getDistanceBetweenLocations : function (lat1,lon1,lat2,lon2) {
			
			// http://en.wikipedia.org/wiki/Haversine_formula
			// Haversine Formula in KM
			var R = 6371;
			var dLat = (lat2-lat1) * (Math.PI/180);
			var dLon = (lon2-lon1) * (Math.PI/180); 
			
			var a = 
			    Math.sin(dLat/2) * Math.sin(dLat/2) +
			    Math.cos(lat1 * (Math.PI/180)) * Math.cos(lat2 * (Math.PI/180)) * 
			    Math.sin(dLon/2) * Math.sin(dLon/2); 
			
			var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
			var d = R * c;
			return d;
		}
	};
});

councilAlertApp.service('dashboardService', function() {
	  var key = "";

	  var setKey = function(newKey) {
	      key = newKey;
	  }

	  var getKey = function(){
	      return key;
	  }

	  return {
	    setKey: setKey,
	    getKey: getKey
	  };

});

councilAlertApp.config( function($routeProvider, $locationProvider, $httpProvider) {
	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'navigation'
	}).when('/login', {
		templateUrl : 'login.html',
		controller : 'navigation'
	}).when('/employee/add', {
		templateUrl : '/employee/addEmployee.html',
		controller : 'addEmp'
	}).when('/employee', {
		templateUrl : '/employee/displayEmployee.html',
		controller : 'emp'
	}).when('/report', {
		templateUrl : '/report/displayReport.html',
		controller : 'report'
	}).when('/user', {
		templateUrl : '/citizen/displayCitizen.html',
		controller : 'citizen'
	}).when('/profile', {
		templateUrl : '/employee/profile.html',
		controller : 'profile'
	}).otherwise("/");
}).run(function($rootScope, $location) {
    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
      if ($rootScope.authenticated == false) {
        if ( next.templateUrl !== "login.html") {
        	$location.path("/login");
        }
      }
    });
  });


