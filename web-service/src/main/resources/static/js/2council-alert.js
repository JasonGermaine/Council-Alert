angular.module('councilalert', [ 'ngRoute', 'ui.bootstrap' ])
.config( function($routeProvider, $locationProvider, $httpProvider) {
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
	}).when('/employee/assign', {
		templateUrl : 'assignReport.html',
		controller : 'assignReport'
	}).when('/report', {
		templateUrl : 'displayReport.html',
		controller : 'report'
	}).when('/citizen', {
		templateUrl : 'displayCitizen.html',
		controller : 'citizen'
	}).otherwise('/');
	
	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	$locationProvider.html5Mode(true);
	
}).controller('navigation', function($rootScope, $scope, $http, $location, $route) {
	
	$scope.credentials = {};

	var authenticate = function(credentials, callback) {

		var headers = credentials ? {
			authorization : "Basic "
					+ btoa(credentials.username + ":"
							+ credentials.password)
		} : {};

		$http.get('api/admin/ping', {
			headers : headers
		}).success(function(data) {
			$rootScope.authenticated = true;
			callback && callback(true);
		}).error(function() {
			$rootScope.authenticated = false;
			callback && callback(false);
		});

	}
	
	$scope.login = function() {
		authenticate($scope.credentials, function(authenticated) {
			if (authenticated) {
				$location.path("/");
				$scope.error = false;
				$rootScope.authenticated = true;
			} else {
				$location.path("/login");
				$scope.error = true;
				$rootScope.authenticated = false;
			}
		})
	};
	
	$scope.logout = function() {
		$rootScope.authenticated = false;
	};
	
}).controller('empUnassigned', function($rootScope, $scope, $http, $location, $route, $modal) {
		
	$http.get("api/employee/unassigned")
				.success(function(response) {
					$scope.emps = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			$rootScope.authenticated = false;
			$location.path("/login");
		}
    });

	$scope.email = '';
	$scope.reports = {};
	
	$scope.assign = function(email, lat, lon) {
		var url = 'api/report/open?lat='
			+ lat + '&lon=' + lon;
	
		$http.get(url).success(function(data) {
			$scope.email = email;
			$scope.reports = data;
			$scope.open();
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				$rootScope.authenticated = false;
				$location.path("/login");
			}
		});
	};
		
	$scope.open = function () {
				
		var modalInstance = $modal.open({
			templateUrl: 'unassignedReport.html',
		    controller: 'assignReport',
		    size: 'lg',
		    resolve: {
		    	reports: function () {
		    		return $scope.reports;
		        }, 
		        email: function() {
		        	return $scope.email;
		        }
		      }
		    });
		 };
			
}).controller('assignReport', function($rootScope, $scope, $http, $location, $route, 
		$modalInstance, reports, email) {

	
	$scope.reports = reports;
	$scope.email = email;
	
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
	
	$scope.assignReport = function(id) {
		var url = 'api/admin/assign?email='
				+ $scope.email + '&id=' + id;
		$http
				.get(url)
				.success(
						function(data) {
							$scope.cancel();
						}).error(function(resp, status) {
							if (status === 401 || status === 403) {								
								$rootScope.authenticated = false;
								$location.path("/login");
							}
					    });
	};

	
}).controller('report', function($rootScope, $scope, $http, $location, $route, $modal) {
	$rootScope.reports = {};

	$http.get("api/report/").success(function(response) {
		$scope.reports = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			$rootScope.authenticated = false;
			$location.path("/login");
		}
    });
	
	$scope.open = function (report) {
		
		$scope.report = report;
		
		var modalInstance = $modal.open({
			templateUrl: 'displayComments.html',
		    controller: 'displayComment',
		    size: 'lg',
		    resolve: {
		    	report: function () {
		    		return $scope.report;
		        }
		      }
	    });
	};
		 
}).controller('displayComment', function($rootScope, $scope, $http, $location, $route,
		$modalInstance, report) {

	$scope.report = report;
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
		 
}).controller('citizen', function($rootScope, $scope, $http, $location, $route) {
	$rootScope.reports = {};

	$http.get("api/admin/citizen").success(function(response) {
		$scope.citizens = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			$rootScope.authenticated = false;
			$location.path("/login");
		}
    });
	
	$scope.getReports = function(email) {
		var url = 'api/citizen/report?email='+ email
		$http.get(url).success(function(data) {
			$scope.reports = data;
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				$rootScope.authenticated = false;
				$location.path("/login");
			}
	    });
	};

}).controller('emp', function($rootScope, $scope, $http, $location, $route) {

	$http.get("api/employee/")
		.success(function(response) {
			$scope.emps = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			$rootScope.authenticated = false;
			$location.path("/login");
		}
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
				
				$http.post('api/admin/create',dataObj).success(function(data) {
					$location.path("/employee");
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						$rootScope.authenticated = false;
						$location.path("/login");
					}
			    });
			};
		});