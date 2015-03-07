angular.module('councilalert', [ 'ngRoute' ]).config(function($routeProvider) {

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

}).controller('navigation',

function($rootScope, $scope, $http, $location, $route) {

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

	$scope.credentials = {};
	$scope.login = function() {
		$http.post('login', $.param($scope.credentials), {
			headers : {
				"content-type" : "application/x-www-form-urlencoded"
			}
		}).success(function(data) {
			authenticate(function() {
				if ($rootScope.authenticated) {					
					$scope.error = false;
					$rootScope.authenticated = true;
					$location.path("/employee");
				} else {					
					$scope.error = true;
					$rootScope.authenticated = false;
					$location.path("/login");
				}
			});
		}).error(function(data) {
			console.log("Login failed")			
			$scope.error = true;
			$rootScope.authenticated = false;
			$location.path("/login");
		})
	};

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
		$http.get("api/employee/unassigned")
				.success(function(response) {
					$scope.emps = response;
		});

		$scope.email = '';
		$scope.assign = function(email, lat, lon) {
			var url = 'api/report/open?lat='
					+ lat + '&lon=' + lon;
			$http.get(url).success(function(data) {
				$scope.email = email;
				$scope.reports = data;
			});
		};

		$scope.assignReport = function(id) {
			var url = 'api/employee/assign?email='
					+ $scope.email + '&id=' + id;
			$http
					.get(url)
					.success(
							function(data) {
								//$('#myModal').modal('hide');
								//angular.element(document.getElementById('myModal')).hide();
								angular.element( document.querySelector('#myModal')).hide();
								$location.path("/employee");
							});
		};
}).controller('report', function($rootScope, $scope, $http, $location, $route) {
	$http.get("api/report/").success(function(response) {
		$scope.reports = response;
	});
}).controller('citizen', function($rootScope, $scope, $http, $location, $route) {
	$http.get("api/citizen/").success(function(response) {
		$scope.citizens = response;
	});
	
	$scope.getReports = function(email) {
		var url = 'api/citizen/report?email='+ email
		$http.get(url).success(function(data) {
			$scope.reports = data;
		});
	};

}).controller('emp', function($rootScope, $scope, $http, $location, $route) {
	$http.get("api/employee/")
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

				$http.post('api/employee/create', dataObj).success(function(data) {
					$location.path("/employee");
				});
			};
		});