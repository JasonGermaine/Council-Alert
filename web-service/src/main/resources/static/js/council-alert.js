angular.module('councilalert', [ 'ngRoute', 'ui.bootstrap', 'uiGmapgoogle-maps', 'tableSort', 'chart.js' ])
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
}).service('dashboardService', function() {
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
	
}).controller('navigation', function($rootScope, $scope, $http, $location, $route, LocalStorage, dashboardService) {

	$scope.credentials = {};

	$scope.toReport = function(key) {
		dashboardService.setKey(key);
		$location.path("/report");
	};
	
	$scope.toEmployee = function(key) {
		dashboardService.setKey(key);
		$location.path("/employee");
	};
	
	$scope.getStats = function() {
		if ($scope.authenticated == true) {
			$http.get("api/admin/stats", LocalStorage.getHeader()).success(function(response) {
				$scope.stats = response;
				$scope.reportLabels = ['Incomplete', 'Complete'];
				$scope.reportData = [ $scope.stats.report_incomplete, $scope.stats.report_complete];
				$scope.empLabels = ['Inactive', 'Active'];
				$scope.empData = [ $scope.stats.emp_unassigned, $scope.stats.emp_assigned];
				$scope.colours = [
				                   { // black
				                     fillColor: 'rgba(34,2,0,0.2)',
				                     strokeColor: 'rgba(34,2,0,1)',
				                     pointColor: 'rgba(34,2,0,1)',
				                     pointStrokeColor: '#fff',
				                     pointHighlightFill: '#fff',
				                     pointHighlightStroke: 'rgba(34,2,0,0.8)'
				                   },
				                   { // dark grey
				                     fillColor: 'rgba(77,83,96,0.2)',
				                     strokeColor: 'rgba(77,83,96,1)',
				                     pointColor: 'rgba(77,83,96,1)',
				                     pointStrokeColor: '#fff',
				                     pointHighlightFill: '#fff',
				                     pointHighlightStroke: 'rgba(77,83,96,1)'
				                   }
				                 ];
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
			$http.get("api/admin/employee/" + $scope.email, LocalStorage.getHeader()).success(function(response) {
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
				
				$http.get("api/admin/employee/" + $scope.credentials.username, LocalStorage.getHeader()).success(function(response) {
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
}).controller('assignReport', function($rootScope, $scope, $http, $location, $route, LocalStorage, 
		$modalInstance, reports, email) {

	
	$scope.reports = reports;
	$scope.email = email;
	$scope.assignError = false;
	$scope.errorMessage = '';
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
	
	$scope.assignReport = function(id) {
		var url = 'api/admin/employee/assign?email='
				+ $scope.email + '&id=' + id;
		
		$http.get(url, LocalStorage.getHeader())
			.success(function(data, status, headers, config) {
				$scope.cancel();
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}  else if (status === 409) {
					$scope.assignError = true;
					$scope.errorMessage = "The entered employee or report are already assigned.";						
				}  else if (status === 400) {
					$scope.assignError = true;
					$scope.errorMessage = "The entered employee or report no longer exist.";						
				} else {
					$scope.assignError = true;
					$scope.errorMessage = "An unexpected error occurred. Please try again.";
				}
			});
	};

	
}).controller('profile', function($rootScope, $scope, $http, $location, $route, LocalStorage, $modal) {
	$scope.request = {};
	
	$scope.currentUser = null;
	$scope.currentUser = angular.copy($rootScope.user);
	
	$scope.phoneEditable = false;

	$scope.phoneEdit = function () {	
		$scope.phoneEditable = true;
	};
	
	$scope.clearPhoneUnedit = function () {			
		$scope.phoneEditable = false;
		$scope.currentUser.phoneNum = angular.copy($rootScope.user.phoneNum);
	};

	$scope.update = function () {					
		$scope.updateRequest = {};
		$scope.updateRequest.deviceId = $scope.currentUser.deviceId;
		$scope.updateRequest.phoneNum = $scope.currentUser.phoneNum;
		$scope.updateRequest.latitude = $scope.currentUser.latitude;
		$scope.updateRequest.longitude = $scope.currentUser.longitude;
		
		$http.put("api/admin/employee/"+$rootScope.user.email, $scope.updateRequest, LocalStorage.getHeader())
		.success(function(resp) {
			
			$rootScope.user.phoneNum = angular.copy($scope.currentUser.phoneNum);
			$rootScope.user.latitude = angular.copy($scope.currentUser.latitude);
			$rootScope.user.longitude = angular.copy($scope.currentUser.longitude);
			
			if ($scope.currentUser.deviceId === 'DELETED') {
				$rootScope.user.deviceId = null;
			}
			
			$location.path("/");
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {					
				LocalStorage.clear();
				$rootScope.authenticated = false;
				$location.path("/login");
			}
		});
	};
	
	$scope.clearDevice = function () {					
		$scope.currentUser.deviceId = 'DELETED';
	};

	$scope.resetDevice = function () {					
		$scope.currentUser.deviceId = angular.copy($rootScope.user.deviceId);
	};
	
	$scope.phoneUnedit = function () {	
		$scope.phoneEditable = false;
	};
	
	$scope.setUnedit = function () {	
		$scope.editable = false;
		$scope.currentUser = $rootScope.user;
	};
	
	$scope.reset = function () {	
		$scope.resetLocation();
		$scope.resetDevice();
		$scope.clearPhoneUnedit();
	};
	
	$scope.resetLocation = function () {	
		$scope.currentUser.latitude = angular.copy($rootScope.user.latitude);
		$scope.currentUser.longitude = angular.copy($rootScope.user.longitude);
		$scope.moveMarker($rootScope.user.latitude, $rootScope.user.longitude);
	};

	 $scope.moveMarker = function (lat, lon) {
		 $scope.map = {
	            center: {
	            	latitude: lat, 
	            	longitude: lon            	
	            },
	            zoom: 12,
	            marker: {
	                id:0,
	                coords: {
		            	latitude: lat, 
		            	longitude: lon
		            	}
	            }
	        };
	};
	
	$scope.moveMarker($scope.currentUser.latitude, $scope.currentUser.longitude);
	
		$scope.changePassword = function () {
			var modalInstance = $modal.open({
				templateUrl: 'changePassword.html',
			    controller: 'changePassword',
			    size: 'md'
		    });
		}; 
		
		
		$scope.changeLocation = function () {
			var modalInstance = $modal.open({
				templateUrl: 'modals/changeLocation.html',
			    controller: 'changeLocation',
			    size: 'md'
		    });
			
			modalInstance.result.then(function (location) {
			      $scope.currentUser.latitude = location.latitude;
			      $scope.currentUser.longitude = location.longitude;		      
			      $scope.moveMarker(location.latitude, location.longitude);
			      $scope.newLocation = true;
			    }, function () {
			      
			 });
	}; 
	
}).controller('changeLocation', function($rootScope, $scope, $http, $location, $route, LocalStorage,
		$modalInstance) {
	$scope.location = {};
	$scope.change = function () {
	    $modalInstance.close($scope.location);
	};
	  
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};		 
}).controller('changePassword', function($rootScope, $scope, $http, $location, $route, LocalStorage,
		$modalInstance) {

	$scope.request = {};	
	$scope.change = function () {
		$http.put("api/admin/employee/password/" + $rootScope.user.email, $scope.request, LocalStorage.getHeader())
			.success(function(resp) {
				$scope.cancel();
				$location.path("/");
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {					
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}
				$scope.passwordError = true;
			});
	};
	
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};		 
}).controller('report', function($rootScope, $scope, $http, $location, $route, LocalStorage, $modal, dashboardService) {
	$rootScope.reports = {};	
	
	 $scope.showEmp = function (email) {
		 $http.get("api/employee/" + email, LocalStorage.getHeader()).success(function(response) {
			 $scope.employee = response;
				var modalInstance = $modal.open({
					templateUrl: 'modals/showEmployeeDetail.html',
				    controller: 'displayEmpDetails',
				    size: 'lg',
				    resolve: {
				    	employee: function () {
				    		return $scope.employee;
				        }
				      }
				    });
		 }).error(function(response) {
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}
			});
	 };
	
	 $scope.getReports = function(url) {
		 $http.get(url, LocalStorage.getHeader()).success(function(response) {
				$scope.reports = response;
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}
		    });
	 };
	 
	$scope.getAll = function() {
		$scope.getReports("api/report/");		
	};
		
	$scope.getToday = function() {		
		$scope.getReports("api/report/today");
	};
	
	$scope.getComplete = function() {
		$scope.getReports("api/report/complete");	
	};
	
	$scope.getIncomplete = function() {
		$scope.getReports("api/report/incomplete");
	};	
	
	$scope.assign = function(id, lat, lon) {
		var url = 'api/employee/open?lat='
			+ lat + '&lon=' + lon;
	
		$http.get(url, LocalStorage.getHeader()).success(function(data) {
			$scope.reportId = id;
			$scope.employees = data;
			$scope.openEmp();
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				LocalStorage.clear();
				$rootScope.authenticated = false;
				$location.path("/login");
			}
		});
	};
		
	$scope.openEmp = function () {
				
		var modalInstance = $modal.open({
			templateUrl: 'availableEmp.html',
		    controller: 'availableEmp',
		    size: 'lg',
		    resolve: {
		    	employees: function () {
		    		return $scope.employees;
		        }, 
		        reportId: function() {
		        	return $scope.reportId;
		        }
		      }
		    });
		 };
	
	$scope.open = function (report) {
		
		$scope.report = report;
		
		var modalInstance = $modal.open({
			templateUrl: '/modals/showReportDetail.html',
		    controller: 'showReportDetail',
		    size: 'lg',
		    resolve: {
		    	report: function () {
		    		return $scope.report;
		        }
		      }
	    });
	};
		 
	

	var key = dashboardService.getKey();
	if (key === "report_today") {
		$scope.getToday();
	} else if (key === "report_incomplete") {
		$scope.getIncomplete();
	} else if (key === "report_complete") {
		$scope.getComplete();
	}
	dashboardService.setKey("");

}).controller('availableEmp', function($rootScope, $scope, $http, $location, $route, LocalStorage,
		$modalInstance, employees, reportId) {

	$scope.employees = employees;
	$scope.reportId = reportId;
	
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
	
	$scope.assignEmp = function(email) {
		var url = 'api/admin/employee/assign?email='
				+ email + '&id=' + reportId;
		
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
	
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
		 
}).controller('showReportDetail', function($rootScope, $scope, $http, $location, $route, $timeout, LocalStorage,
		$modalInstance, report, $modal) {
	
	$scope.report = report;
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
	
	$scope.openImage = function (image) {
		$scope.image = image;
		var modalInstance = $modal.open({
			templateUrl: 'modals/showImage.html',
		    controller: 'displayImage',
		    size: 'md',
		    resolve: {
		    	image: function () {
		    		return $scope.image;
		        }
		      }
		    });
	};

	$scope.changeStatus = function (reportId, status) { 
		$http.put("api/admin/report/" + reportId + "/" + status,
				{}, LocalStorage.getHeader())
			.success(function(data) {
				$scope.cancel();
			}).error(function(resp, status) {			
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}
			});
	};
	
	$scope.unassign = function (reportId) { 
		$http.get("api/admin/report/unassign/" + reportId, LocalStorage.getHeader())
			.success(function(data) {
				$scope.cancel();
			}).error(function(resp, status) {
			
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				}
			});
	};
	
	$scope.mapSetup = function() {
		 $scope.map = {
		            center: {
		            	latitude: $scope.report.latitude, 
		            	longitude: $scope.report.longitude            	
		            },
		            zoom: 12,
		            marker: {
		                id:0,
		                coords: {
		                	latitude: $scope.report.latitude, 
		                	longitude: $scope.report.longitude
		                }
		            }
		        };
	 }

	$timeout( function(){ $scope.mapSetup(); }, 250);
		 
}).controller('displayImage', function($rootScope, $scope, $http, $location, $route, $modalInstance, image, LocalStorage) {
	
	$scope.image = image;
	
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
		var url = 'api/citizen/report/'+ email
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


}).controller('displayUserReports', function($rootScope, $scope, $http, $location, $route, $modal, LocalStorage,
		$modalInstance, userReports) {

	$scope.userReports = userReports;
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
		 
	$scope.openReport = function (report) {
		
		$scope.report = report;
		
		var modalInstance = $modal.open({
			templateUrl: '/modals/showReportDetail.html',
		    controller: 'showReportDetail',
		    size: 'lg',
		    resolve: {
		    	report: function () {
		    		return $scope.report;
		        }
		      }
	    });
	};
}).controller('emp', function($rootScope, $scope, $http, $location, $route, LocalStorage, $modal, dashboardService) {
	
	$scope.empError = false;
	$scope.errorMessage = '';
	
	
	$scope.displayError = function() {
		$scope.empError = true;
		$scope.errorMessage = 'An unexpected error has occurred.';
	};
	
	$scope.resetError = function() {
		$scope.empError = false;
		$scope.errorMessage = '';
	};	
	
	$scope.getAll = function () {
		$scope.getEmployees("api/employee/");
	};
	
	$scope.getUnassigned = function () {
		$scope.getEmployees("api/employee/unassigned");
	};
	
	$scope.getAssigned = function () {
		$scope.getEmployees("api/employee/assigned");
	};
	
	$scope.getEmployees = function(url) {
		$http.get(url, LocalStorage.getHeader())
		.success(function(response) {
			$scope.emps = response;
			$scope.resetError();
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				LocalStorage.clear();
				$rootScope.authenticated = false;
				$location.path("/login");
			}
			$scope.displayError();
		});
	};
	
	$scope.showReport = function (reportId) {
		
		$http.get("api/report/" + reportId, LocalStorage.getHeader())
		.success(function(response) {
			$scope.report = response;
			$scope.resetError();
			var modalInstance = $modal.open({
				templateUrl: '/modals/showReportDetail.html',
			    controller: 'showReportDetail',
			    size: 'lg',
			    resolve: {
			    	report: function () {
			    		return $scope.report;
			        }
			      }
		    });
		}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				LocalStorage.clear();
				$rootScope.authenticated = false;
				$location.path("/login");
			}
			$scope.displayError();
		});
	};
	
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
	
	 $scope.openDisplay = function (employee) {
			$scope.employee = employee;
			
			var modalInstance = $modal.open({
				templateUrl: 'modals/showEmployeeDetail.html',
			    controller: 'displayEmpDetails',
			    size: 'lg',
			    resolve: {
			    	employee: function () {
			    		return $scope.employee;
			        }
			      }
			    });
			 };
		 
				$scope.email = '';
				$scope.reports = {};
				
				$scope.assign = function(email, lat, lon) {
					var url = 'api/report/open?lat='
						+ lat + '&lon=' + lon;
				
					$http.get(url, LocalStorage.getHeader()).success(function(data) {
						$scope.email = email;
						$scope.reports = data;
						$scope.resetError();
						$scope.openAssign();
					}).error(function(resp, status) {
						if (status === 401 || status === 403) {
							LocalStorage.clear();
							$rootScope.authenticated = false;
							$location.path("/login");
						}
						$scope.displayError();
					});
				};
					
				$scope.openAssign = function () {
							
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

					 
			var key = dashboardService.getKey();
			if (key === "emp_all") {
				$scope.getAll();
			} else if (key === "emp_inactive") {
				$scope.getUnassigned();
			} else if (key === "emp_active") {
				$scope.getAssigned();
			}
			dashboardService.setKey("");

}).controller('removeEmp', function($rootScope, $scope, $http, $location, $route, $modalInstance, employee, LocalStorage) {
	
	$scope.removeError = false;
	$scope.errorMessage = '';
	$scope.employee = employee;
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
	
	$scope.ok = function () {
	
		$http.delete("api/admin/employee/" + $scope.employee.email, LocalStorage.getHeader())
			.success(function(response) {
				$scope.cancel();
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				} else if (status === 400) {
					$scope.removeError = true;
					$scope.errorMessage = "Employee has already been removed.";						
				} else {
					$scope.removeError = true;
					$scope.errorMessage = "An unexpected error occurred. Please try again.";
				}				
			});
	};
}).controller('displayEmpDetails', function($rootScope, $scope, $http, $location, $route, $timeout, $modalInstance, employee, LocalStorage) {
	
	$scope.emp = employee;
	$scope.edit = false;

	 $scope.mapSetup = function() {
		 $scope.map = {
		            center: {
		            	latitude: $scope.emp.latitude, 
		            	longitude: $scope.emp.longitude            	
		            },
		            zoom: 12,
		            marker: {
		                id:0,
		                coords: {
		                	latitude: $scope.emp.latitude, 
		                	longitude: $scope.emp.longitude
		                }
		            }
		        };
	 }

	$timeout( function(){ $scope.mapSetup(); }, 250);
	
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};
	
	$scope.ok = function () {

	};
}).controller(
		'addEmp',
		function($rootScope, $scope, $http, $location, $route, LocalStorage) {
			$scope.emp = {};
			$scope.pwd2 = '';

			$scope.error = false;
			$scope.incomplete = false;

			$scope.nameRegex = '[A-Za-z]*';
			$scope.numRegex = '[0-9+/-]*';
			$scope.pwdRegex = '[A-Za-z0-9!?.$%]*';
			
			$scope.$watch('emp.pwd', function() {
				$scope.test();
			});
			$scope.$watch('pwd2', function() {
				$scope.test();
			});
			$scope.$watch('emp.latitude', function() {
				$scope.test();
			});
			$scope.$watch('emp.longitude', function() {
				$scope.test();
			});
			
			$scope.test = function() {
				if ($scope.emp.password !== $scope.pwd2) {
					$scope.error = true;
				} else {
					$scope.error = false;
				}

				$scope.incomplete = false;
				if (!$scope.emp.latitude.length || !$scope.emp.longitude.length) {
					$scope.incomplete = true;
				}
			};
			
			$scope.errorMessage = '';
			$scope.formError = false;
			
			$scope.register = function() {
				
				$http.post('api/admin/employee', $scope.emp, LocalStorage.getHeader()).success(function(data) {
					$location.path("/");
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						LocalStorage.clear();
						$rootScope.authenticated = false;
						$location.path("/login");
					} else if (status === 409) {
						$scope.formError = true;
						$scope.errorMessage = "An Employee is already registered with that email.";
					} else if (status === 400) {
						$scope.formError = true;
						$scope.errorMessage = "Invalid data inputted.";						
					} else {
						$scope.formError = true;
						$scope.errorMessage = "An unexpected error occurred. Please try again.";
					}
			    });
			};
		});