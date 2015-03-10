angular.module('councilalert', [ 'ngRoute' ])
.factory('LocalStorage', function() {
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
		}
	};
}).config( function($routeProvider, $locationProvider, $httpProvider) {
	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'navigation'
	}).when('/login', {
		templateUrl : 'login.html',
		controller : 'navigation'
	}).when('/employee/add', {
		templateUrl : 'addEmployee.html',
		controller : 'addEmp'
	}).when('/employee', {
		templateUrl : 'displayEmployee.html',
		controller : 'emp'
	}).when('/employee/unassigned', {
		templateUrl : 'displayUnassignedEmployee.html',
		controller : 'empUnassigned'
	}).when('/report', {
		templateUrl : 'displayReport.html',
		controller : 'report'
	}).when('/citizen', {
		templateUrl : 'displayCitizen.html',
		controller : 'citizen'
	}).otherwise('/');

	$locationProvider.html5Mode(true);
	
}).controller('navigation', function($rootScope, $scope, $http, $location, $route, LocalStorage) {

	$scope.credentials = {};

	
	$scope.init = function() {
		var email = LocalStorage.retrieveEmail();
		var token = LocalStorage.retrieveToken(); 
		if(email != null && email != '' && token != null && token != '') {
			var config = {
					headers : { 
						"Authorization" : token,
					}
			}
			$http.get("api/admin/ping", config)
			.success(function(response) {
				$rootScope.authenticated = true;
			}).error(function() {
				$rootScope.authenticated = false;
				LocalStorage.clear();
			});
			
		} else {
			$rootScope.authenticated = false;
			LocalStorage.clear();
		}
	}
	
	$scope.login = function() {		
		var authdata = btoa("angular-client:council-alert-angular-secret");
		
		var request = {
            "username" : $scope.credentials.username,
            "password" : $scope.credentials.password,
            "grant_type" : "password",
            "scope" : "read write trust",
            "client_id" : "angular-client",
            "client_secret" : "council-alert-angular-secret"
		};
		
		var config = {
			headers : { 
				"Authorization" : 'Basic ' + authdata,
				"Content-type" : 'application/x-www-form-urlencoded',
				"Accept" : "application/json"
			}
		};
		
		$http.post('oauth/token', $.param(request), config).
		success(function(response) {
			$scope.oauth_resp = response;
			if ($scope.oauth_resp.access_token) {
				LocalStorage.storeToken('Bearer ' + $scope.oauth_resp.access_token);
				LocalStorage.storeEmail($scope.credentials.username);
				
				$rootScope.authenticated = true;
				$location.path("/");
			}
		}).error(function(data) {
			$rootScope.authenticated = false;
			$rootScope.error = true;
			LocalStorage.clear();
		})
	};
	
	$scope.logout = function() {
		$rootScope.authenticated = false;
		LocalStorage.clear();
	};
	
}).controller('empUnassigned', function($rootScope, $scope, $http, $location, $route, LocalStorage) {

	var token = LocalStorage.retrieveToken();
	var config = {
			headers : { 
				"Authorization" : token,
				"Accept" : "application/json"
			}
	}
		
	
	$http.get("api/employee/unassigned", config)
				.success(function(response) {
					$scope.emps = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			LocalStorage.clear();
			$rootScope.authenticated = false;
			$location.path("/login");
		}
    });

		$scope.email = '';
		$scope.assign = function(email, lat, lon) {
			var url = 'api/report/open?lat='
					+ lat + '&lon=' + lon;
			$http.get(url, config).success(function(data) {
				$scope.email = email;
				$scope.reports = data;
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}
		    });
		};

		$scope.assignReport = function(id) {
			var url = 'api/admin/assign?email='
					+ $scope.email + '&id=' + id;
			$http
					.get(url, config)
					.success(
							function(data) {
								//$('#myModal').modal('hide');
								//angular.element(document.getElementById('myModal')).hide();
								angular.element( document.querySelector('#myModal')).hide();
								$location.path("/employee");
							}).error(function(resp, status) {
								if (status === 401 || status === 403) {
									LocalStorage.clear();
									$rootScope.authenticated = false;
									$location.path("/login");
								}
						    });
		};
		
			
}).controller('report', function($rootScope, $scope, $http, $location, $route, LocalStorage) {
	var token = LocalStorage.retrieveToken();
	var config = {
			headers : { 
				"Authorization" : token,
				"Accept" : "application/json"
			}
	}
	$http.get("api/report/", config).success(function(response) {
		$scope.reports = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			LocalStorage.clear();
			$rootScope.authenticated = false;
			$location.path("/login");
		}
    });
}).controller('citizen', function($rootScope, $scope, $http, $location, $route, LocalStorage) {
	var token = LocalStorage.retrieveToken();
	var config = {
			headers : { 
				"Authorization" : token,
				"Accept" : "application/json"
			}
	}
	$http.get("api/admin/citizen", config).success(function(response) {
		$scope.citizens = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			LocalStorage.clear();
			$rootScope.authenticated = false;
			$location.path("/login");
		}
    });
	
	$scope.getReports = function(email) {
		var url = 'api/citizen/report?email='+ email
		$http.get(url, config).success(function(data) {
			$scope.reports = data;
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				LocalStorage.clear();
				$rootScope.authenticated = false;
				$location.path("/login");
			}
	    });
	};

}).controller('emp', function($rootScope, $scope, $http, $location, $route, LocalStorage) {
	var token = LocalStorage.retrieveToken();
	var config = {
			headers : { 
				"Authorization" : token,
				"Accept" : "application/json"
			}
	}
	$http.get("api/employee/", config)
		.success(function(response) {
			$scope.emps = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			LocalStorage.clear();
			$rootScope.authenticated = false;
			$location.path("/login");
		}
    });
}).controller(
		'addEmp',
		function($rootScope, $scope, $http, $location, $route, LocalStorage) {
			$scope.fName = '';
			$scope.lName = '';
			$scope.pwd = '';
			$scope.pwd2 = '';
			$scope.email = '';
			$scope.phoneNum = '';
			$scope.lat = '';
			$scope.lon = '';
			$scope.error = false;
			$scope.incomplete = false;

			$scope.$watch('pwd', function() {
				$scope.test();
			});
			$scope.$watch('pwd2', function() {
				$scope.test();
			});
			$scope.$watch('fName', function() {
				$scope.test();
			});
			$scope.$watch('lName', function() {
				$scope.test();
			});
			$scope.$watch('email', function() {
				$scope.test();
			});
			$scope.$watch('phoneNum', function() {
				$scope.test();
			});
			$scope.$watch('lat', function() {
				$scope.test();
			});
			$scope.$watch('lon', function() {
				$scope.test();
			});
			$scope.test = function() {
				if ($scope.pwd !== $scope.pwd2) {
					$scope.error = true;
				} else {
					$scope.error = false;
				}

				$scope.incomplete = false;
				if (!$scope.fName.length || !$scope.lName.length
						|| !$scope.pwd.length || !$scope.pwd2.length
						|| !$scope.email.length || !$scope.phoneNum.length
						|| !$scope.lat.length || !$scope.lon.length) {

					$scope.incomplete = true;
				}
			};

			$scope.register = function() {
				var dataObj = {
					email : $scope.email,
					firstName : $scope.fName,
					lastName : $scope.lName,
					password : $scope.pwd,
					phoneNum : $scope.phoneNum,
					latitude : $scope.lat,
					longitude : $scope.lon
				};

				var token = LocalStorage.retrieveToken();
				var config = {
						headers : { 
							"Authorization" : token,
							"Accept" : "application/json"
						}
				}
				
				$http.post('api/admin/create',dataObj, config).success(function(data) {
					$location.path("/employee");
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						LocalStorage.clear();
						$rootScope.authenticated = false;
						$location.path("/login");
					}
			    });
			};
		});