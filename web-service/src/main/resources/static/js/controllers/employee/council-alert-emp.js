angular.module('councilalert').controller(
		'emp',
		function($rootScope, $scope, $http, $location, $route, LocalStorage,
				$modal, dashboardService) {

			$scope.empError = false;
			$scope.errorMessage = '';
			$scope.sortType = '';
			
			$scope.displayError = function() {
				$scope.empError = true;
				$scope.errorMessage = 'An unexpected error has occurred.';
			};

			$scope.resetError = function() {
				$scope.empError = false;
				$scope.errorMessage = '';
			};

			$scope.getAll = function() {
				$scope.getEmployees("api/employee/");
				$scope.sortType = 'ALL';
			};

			$scope.getUnassigned = function() {
				$scope.getEmployees("api/employee/unassigned");
				$scope.sortType = 'UNASSIGNED';
			};

			$scope.getAssigned = function() {
				$scope.getEmployees("api/employee/assigned");
				$scope.sortType = 'ASSIGNED';
			};

			$scope.getEmployees = function(url) {
				$http.get(url, LocalStorage.getHeader()).success(
						function(response) {
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
							
							modalInstance.result.then(function(action) {
								if ($scope.sortType === 'ASSIGNED' 
										&& (action === 'UNASSIGN' || action === 'COMPLETE')) {
									$scope.emps.splice($scope.emps.indexOf(emp), 1);
								}
							}, function() {
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
				
				modalInstance.result.then(function() {
					$scope.emps.splice($scope.emps.indexOf($scope.employee), 1);
				}, function() {
				});
				
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

			$scope.email = '';
			$scope.reports = {};
			
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
						LocalStorage.clear();
						$rootScope.authenticated = false;
						$location.path("/login");
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
						email : function() {
							return $scope.email;
						}
					}
				});
				
				modalInstance.result.then(function(reportId) {
					emp.reportId = reportId;
					if ($scope.sortType === 'UNASSIGNED') {
						$scope.emps.splice($scope.emps.indexOf(emp), 1);
					}
				}, function() {
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

		});