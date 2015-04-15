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
})

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

})

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
	}).when('/citizen', {
		templateUrl : '/citizen/displayCitizen.html',
		controller : 'citizen'
	}).when('/profile', {
		templateUrl : '/employee/profile.html',
		controller : 'profile'
	}).otherwise('/');

	$locationProvider.html5Mode(true);
	
});