angular.module('councilalert', [ 'ngRoute', 'ui.bootstrap' ])
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
}).config( function($routeProvider, $locationProvider, $httpProvider) {
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
	}).when('/employee/unassigned', {
		templateUrl : '/employee/displayUnassignedEmployee.html',
		controller : 'empUnassigned'
	}).when('/report', {
		templateUrl : '/report/displayReport.html',
		controller : 'report'
	}).when('/citizen', {
		templateUrl : '/citizen/displayCitizen.html',
		controller : 'citizen'
	}).otherwise('/');

	$locationProvider.html5Mode(true);
	
}).controller('navigation', function($rootScope, $scope, $http, $location, $route, LocalStorage) {

	$scope.credentials = {};

	
	$scope.getStats = function() {
		if ($scope.authenticated == true) {
			var token = LocalStorage.retrieveToken();
			var config = {
					headers : { 
						"Authorization" : token,
						"Accept" : "application/json"
					}
			}
			$http.get("api/admin/stats", config).success(function(response) {
				$scope.stats = response;
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}
		    });
		}
	};
	
	$scope.init = function() {
		var email = LocalStorage.retrieveEmail();
		$scope.email = email;
		var token = LocalStorage.retrieveToken(); 
		if(email != null && email != '' && token != null && token != '') {			
			var user = {
					email: $scope.email
			};
			$http.post("api/admin/retrieve?email=" + $scope.email, user, LocalStorage.getHeader()).success(function(response) {
				$rootScope.user = response;
				$rootScope.authenticated = true;
				$location.path("/");
				
				$scope.getStats();
			}).error(function(response) {
				$rootScope.authenticated = false;
				LocalStorage.clear();
				$location.path("/login");
			});					
		} else {
			$rootScope.authenticated = false;
			LocalStorage.clear();
			$location.path("/login");
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
				
				var user = {
						email : $scope.credentials.username						
				}
				
				$http.post("api/admin/retrieve?email=" + $scope.credentials.username, user, LocalStorage.getHeader()).success(function(response) {
					$rootScope.user = response;
					$rootScope.authenticated = true;
					$location.path("/");
				}).error(function(response) {
					$rootScope.authenticated = false;
					$rootScope.error = true;
					LocalStorage.clear();					
				});
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
		$location.path("/login");
	};
	
	$scope.getStats();
}).controller('empUnassigned', function($rootScope, $scope, $http, $location, $route, LocalStorage, $modal) {
		
	$http.get("api/employee/unassigned", LocalStorage.getHeader())
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
	$scope.reports = {};
	
	$scope.assign = function(email, lat, lon) {
		var url = 'api/report/open?lat='
			+ lat + '&lon=' + lon;
	
		$http.get(url, LocalStorage.getHeader()).success(function(data) {
			$scope.email = email;
			$scope.reports = data;
			$scope.open();
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				LocalStorage.clear();
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
			
}).controller('assignReport', function($rootScope, $scope, $http, $location, $route, LocalStorage, 
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
				.get(url, LocalStorage.getHeader())
				.success(
						function(data) {
							$scope.cancel();
						}).error(function(resp, status) {
							if (status === 401 || status === 403) {
								LocalStorage.clear();
								$rootScope.authenticated = false;
								$location.path("/login");
							}
					    });
	};

	
}).controller('report', function($rootScope, $scope, $http, $location, $route, LocalStorage, $modal) {
	$rootScope.reports = {};

	$http.get("api/report/", LocalStorage.getHeader()).success(function(response) {
		$scope.reports = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			LocalStorage.clear();
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
		 
}).controller('displayComment', function($rootScope, $scope, $http, $location, $route, LocalStorage,
		$modalInstance, report) {

	$scope.report = report;
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
		 
}).controller('citizen', function($rootScope, $scope, $http, $location, $route, LocalStorage, $modal) {
	$rootScope.reports = {};

	$http.get("api/admin/citizen", LocalStorage.getHeader()).success(function(response) {
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
		$http.get(url, LocalStorage.getHeader()).success(function(data) {
			$scope.userReports = data;
			$scope.open();
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				LocalStorage.clear();
				$rootScope.authenticated = false;
				$location.path("/login");
			}
	    });
	};
	
	$scope.open = function () {
		
		var modalInstance = $modal.open({
			templateUrl: 'displayUserReports.html',
		    controller: 'displayUserReports',
		    size: 'md',
		    resolve: {
		    	userReports: function () {
		    		return $scope.userReports;
		        }
		      }
		    });
		 };


}).controller('displayUserReports', function($rootScope, $scope, $http, $location, $route, LocalStorage,
		$modalInstance, userReports) {

	$scope.userReports = userReports;
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
		 
}).controller('emp', function($rootScope, $scope, $http, $location, $route, LocalStorage, $modal) {
	$http.get("api/employee/", LocalStorage.getHeader())
		.success(function(response) {
			$scope.emps = response;
	}).error(function(resp, status) {
		if (status === 401 || status === 403) {
			LocalStorage.clear();
			$rootScope.authenticated = false;
			$location.path("/login");
		}
    });
	
	$scope.openRemove = function (employee) {
		$scope.employee = employee;
		
		var modalInstance = $modal.open({
			templateUrl: 'removeEmployee.html',
		    controller: 'removeEmp',
		    size: 'md',
		    resolve: {
		    	employee: function () {
		    		return $scope.employee;
		        }
		      }
		    });
		 };
		 
}).controller('removeEmp', function($rootScope, $scope, $http, $location, $route, $modalInstance, employee, LocalStorage) {
	
	$scope.employee = employee;
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
	
	$scope.ok = function () {
	
		$http.delete("api/admin/employee?email=" + $scope.employee.email, LocalStorage.getHeader())
			.success(function(response) {
				$scope.cancel();
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}
				$scope.error = true;
			});
	};
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
				
				$http.post('api/admin/create',dataObj, LocalStorage.getHeader()).success(function(data) {
					$location.path("/");
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						LocalStorage.clear();
						$rootScope.authenticated = false;
						$location.path("/login");
					}
			    });
			};
		});