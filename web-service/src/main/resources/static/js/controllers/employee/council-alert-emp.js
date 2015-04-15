angular.module('councilalert').controller(
		'emp',
		function($rootScope, $scope, $http, $location, $route, LocalStorage,
				$modal, dashboardService) {

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

			$scope.getAll = function() {
				$scope.getEmployees("api/employee/");
			};

			$scope.getUnassigned = function() {
				$scope.getEmployees("api/employee/unassigned");
			};

			$scope.getAssigned = function() {
				$scope.getEmployees("api/employee/assigned");
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

			$scope.showReport = function(reportId) {

				$http.get("api/report/" + reportId, LocalStorage.getHeader())
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

			$scope.assign = function(email, lat, lon) {
				var url = 'api/report/open?lat=' + lat + '&lon=' + lon;

				$http.get(url, LocalStorage.getHeader()).success(
						function(data) {
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

			$scope.openAssign = function() {

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