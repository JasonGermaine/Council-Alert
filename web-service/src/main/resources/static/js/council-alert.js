angular.module('councilalert', [ 'ngRoute' ]).config(function($routeProvider, $locationProvider) {
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
	
}).controller('navigation',

function($rootScope, $scope, $http, $location, $route) {

	$rootScope.token = "";
	$scope.credentials = {};
	
	$scope.login = function() {		
		var authdata = btoa("angular-client:council-alert-angular-secret");
		
		var request =
              "username=" + $scope.credentials.username
              + "&password=" + $scope.credentials.password
              + "&grant_type=password"
              + "&scope=read%20write%20trust"
              + "&client_id=angular-client"
              + "&client_secret=council-alert-angular-secret"
          ;

		var request = {
            "username" : $scope.credentials.username,
            "password" : $scope.credentials.password,
            "grant_type" : "password",
            "scope" : "read write trust",
            "client_id" : "angular-client",
            "client_secret" : "council-alert-angular-secret"
		};
		
		//http://localhost:8081/oauth/authorize?client_id=web&response_type=token 
		
		console.log(request);
		var config = {
			headers : { 
				"Authorization" : 'Basic ' + authdata,
				"Content-type" : 'application/x-www-form-urlencoded',
				"Accept" : "application/json"
			}
		};
		$http.post('oauth/token', $.param(request), config).
		success(function(response) {
			if (response.access_token) {
				$rootScope.authenticated = false;
				$rootScope.data = response;
				$rootScope.token_header = 'Bearer ' + $rootScope.data.access_token;
				console.log($rootScope.token_header);
				$rootScope.authenticated = true;
				$location.path("/");
			}
		}).error(function(data) {
			$rootScope.authenticated = false;
			$rootScope.authenticated = true;
			$location.credentials.password='';
		})
	};
	
	$scope.logout = function() {
		$rootScope.authenticated = false;
		$rootScope.token_header = '';
	};
	
	  $scope.status = {
	    isopen: false
	  };

	  $scope.toggled = function(open) {
	    $log.log('Dropdown is now: ', open);
	  };

	  $scope.toggleDropdown = function($event) {
	    $event.preventDefault();
	    $event.stopPropagation();
	    $scope.status.isopen = !$scope.status.isopen;
	  };
	/*
	$scope.tab = function(route) {
		return $route.current && route === $route.current.controller;
	};

	var authenticate = function(callback) {

		$http.get('employee/check').success(function(data, status) {
			$rootScope.authenticated = false;
			if(status === 202) {
				$rootScope.authenticated = true;
			}
			callback && callback();
		}).error(function() {
			$rootScope.authenticated = false;
			callback && callback();
		});

	}

	authenticate();

	$scope.logout = function() {
		$http.post('logout', {}).success(function() {
			$rootScope.authenticated = false;
			$location.path("/");
		}).error(function(data) {
			console.log("Logout failed")
			$rootScope.authenticated = false;
			$location.path("/");
		});
	}
	*/
}).controller('empUnassigned', function($rootScope, $scope, $http, $location, $route) {

	var config = {
			headers : { 
				"Authorization" : $rootScope.token_header,
				"Accept" : "application/json"
			}
		};
		
	
	$http.get("api/employee/unassigned", config)
				.success(function(response) {
					$scope.emps = response;
	});

		$scope.email = '';
		$scope.assign = function(email, lat, lon) {
			var url = 'api/report/open?lat='
					+ lat + '&lon=' + lon;
			$http.get(url, config).success(function(data) {
				$scope.email = email;
				$scope.reports = data;
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
							});
		};
}).controller('report', function($rootScope, $scope, $http, $location, $route) {
	var config = {
			headers : { 
				"Authorization" : $rootScope.token_header,
				"Accept" : "application/json"
			}
		};
	$http.get("api/report/", config).success(function(response) {
		$scope.reports = response;
	});
}).controller('citizen', function($rootScope, $scope, $http, $location, $route) {
	var config = {
			headers : { 
				"Authorization" : $rootScope.token_header,
				"Accept" : "application/json"
			}
		};
	$http.get("api/admin/citizen", config).success(function(response) {
		$scope.citizens = response;
	});
	
	$scope.getReports = function(email) {
		var url = 'api/citizen/report?email='+ email
		$http.get(url, config).success(function(data) {
			$scope.reports = data;
		});
	};

}).controller('emp', function($rootScope, $scope, $http, $location, $route) {
	var config = {
			headers : { 
				"Authorization" : $rootScope.token_header,
				"Accept" : "application/json"
			}
		};
	$http.get("api/employee/", config)
		.success(function(response) {
			$scope.emps = response;
	});
}).controller(
		'addEmp',
		function($rootScope, $scope, $http, $location, $route) {
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

				var config = {
						headers : { 
							"Authorization" : $rootScope.token_header,
							"Accept" : "application/json"
						}
					};
				
				$http.post('api/admin/create',dataObj, config).success(function(data) {
					$location.path("/employee");
				});
			};
		});