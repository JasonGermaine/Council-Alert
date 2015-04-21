angular.module('councilalert')
	.controller('emp', function($rootScope, $scope, $http, $location, $route, LocalStorage,
				$modal, dashboardService) {

		$scope.emps = [];
		$scope.searchEmps = [];
		$scope.sortUrl = "api/employee/all";
		$scope.empError = false;
		$scope.searchError = false;
		$scope.errorMessage = '';
		$scope.sortType = '';
		$scope.email = '';
		$scope.reports = {};
		$scope.noMoreData = false;
		
		$scope.getEmployees = function(url) {
			$http.get(url, LocalStorage.getHeader())
				.success(function(response) {
					$scope.searchEmps = [];
					$scope.emps = response;
					$scope.resetError();
					if (response.length < 30) {
						$scope.noMoreData = true;
					} else {
						$scope.noMoreData = false;
					}
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						$scope.logout();
					}
					$scope.displayError();
				});
		};

		$scope.showReport = function(emp) {

			$http.get("api/report/" + emp.reportId, LocalStorage.getHeader())
				.success(function(response) {
					$scope.report = response;
					$scope.resetError();
					var modalInstance = $modal.open({
						templateUrl : '/modals/showReportDetail.html',
						controller : 'showReportDetail',
						size : 'lg',
						resolve : {
							report : function() {
								return $scope.report;
							}
						}
					});
						
					// Callback function from modal
					modalInstance.result.then(function(action) {
						if ($scope.sortType === 'ASSIGNED' 
								&& (action === 'UNASSIGN' || action === 'COMPLETE')) {
								
								// remove employee from list if needed
								$scope.emps.splice($scope.emps.indexOf(emp), 1);
						}
					}, function() {});
					
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						$scope.logout();
					}
					$scope.displayError();
				});
		};

		$scope.openRemove = function(employee) {
			$scope.employee = employee;
			var modalInstance = $modal.open({
				templateUrl : 'modals/removeEmployee.html',
				controller : 'removeEmp',
				size : 'md',
				resolve : {
					employee : function() {
						return $scope.employee;
					}
				}
			});
			
			// Remove employee from the list on success
			modalInstance.result.then(function() {
				$scope.emps.splice($scope.emps.indexOf($scope.employee), 1);
			}, function() {});
		};

		$scope.openDisplay = function(employee) {
			$scope.employee = employee;

			var modalInstance = $modal.open({
				templateUrl : 'modals/showEmployeeDetail.html',
				controller : 'displayEmpDetails',
				size : 'lg',
				resolve : {
					employee : function() {
						return $scope.employee;
					}
				}
			});				
		};
		
		$scope.assign = function(emp) {
			var url = 'api/report/open?lat=' + emp.latitude + '&lon=' + emp.longitude;
			$http.get(url, LocalStorage.getHeader()).success(
					function(data) {
						$scope.email = emp.email;
						$scope.reports = data;
						$scope.resetError();
						$scope.openAssign(emp);
					}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					$scope.logout();
				}
				$scope.displayError();
			});
		};

		$scope.openAssign = function(emp) {
			var modalInstance = $modal.open({
				templateUrl : 'modals/unassignedReport.html',
				controller : 'assignReport',
				size : 'lg',
				resolve : {
					reports : function() {
						return $scope.reports;
					},
					employee : function() {
						return emp;
					}
				}
			});
			
			modalInstance.result.then(function(reportId) {
				emp.reportId = reportId;
				if ($scope.sortType === 'UNASSIGNED') {
					$scope.emps.splice($scope.emps.indexOf(emp), 1);
				}
			}, function() {});
		};
		
		$scope.logout = function() {
			$rootScope.authenticated = false;
			LocalStorage.clear();
			$location.path("/login");
		};
		
		$scope.displayError = function() {
			$scope.empError = true;
			$scope.errorMessage = 'An unexpected error has occurred.';
		};

		$scope.resetError = function() {
			$scope.empError = false;
			$scope.errorMessage = '';
			$scope.searchError = false;
		};

		$scope.getAll = function() {
			$scope.sortUrl = "api/employee/all";
			$scope.getEmployees("api/employee/");
			$scope.sortType = 'ALL';
		};

		$scope.getUnassigned = function() {
			$scope.sortUrl = "api/employee/unassigned";
			$scope.getEmployees($scope.sortUrl);
			$scope.sortType = 'UNASSIGNED';
		};

		$scope.getAssigned = function() {
			$scope.sortUrl = "api/employee/assigned"
			$scope.getEmployees($scope.sortUrl);
			$scope.sortType = 'ASSIGNED';
		};

		$scope.loadMore = function() {
			$http.get($scope.sortUrl + "/" + $scope.emps.length, LocalStorage.getHeader())
				.success(function(response) {
					$scope.resetError();
					if (response.length) {
						var skip = false;
						
						// Ensure that we do not add an employee that we have already
						// manually retrieved
						angular.forEach(response, function(employee) {					
							angular.forEach($scope.searchEmps, function(searchedEmp) {
								if (searchedEmp.email === employee.email) {
									skip = true;
								}   	    	
						    });
							if (!skip) {
								$scope.emps.push(employee);
							}
					    });
						if (response.length < 30) {
							$scope.noMoreData = true;
						}
					} else {
						$scope.noMoreData = true;
					}
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						$scope.logout();
					}
					$scope.displayError();
				});
		}

		$scope.search = function(query) {
			$http.get("api/employee/" + query, LocalStorage.getHeader())
				.success(function(response) {
					$scope.resetError();
					$scope.addEmpIfNotExists(response);
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						$scope.logout();
					}
					$scope.searchError = true;
				});
		}
		
		$scope.addEmpIfNotExists = function(emp) {
			var skip = false;
			
			angular.forEach($scope.emps, function(employee) {
				if (employee.email === emp.email) {
					skip = true;
				}				
		    });
			if (!skip) {
				$scope.emps.push(emp);
				$scope.searchEmps.push(emp);
			}
		}
		
		// Determine if a shortcut button has been pressed through the dashboard
		var key = dashboardService.getKey();
		if (key === "emp_all") {
			$scope.getAll();
		} else if (key === "emp_inactive") {
			$scope.getUnassigned();
		} else if (key === "emp_active") {
			$scope.getAssigned();
		}
		dashboardService.setKey("");

	});