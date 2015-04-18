angular.module('councilalert').controller(
		'report',
		function($rootScope, $scope, $http, $location, $route, LocalStorage,
				$modal, dashboardService) {
			$rootScope.reports = {};
			$scope.reportError = false;
			$scope.errorMessage = '';
			$scope.sortType = '';
			
			$scope.displayError = function() {
				$scope.reportError = true;
				$scope.errorMessage = 'An unexpected error has occurred.';
			};

			$scope.resetError = function() {
				$scope.reportError = false;
				$scope.errorMessage = '';
			};

			$scope.showEmp = function(email) {
				$http.get("api/employee/" + email, LocalStorage.getHeader())
						.success(function(response) {
							$scope.resetError();
							$scope.employee = response;
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
						}).error(function(response) {
							if (status === 401 || status === 403) {
								LocalStorage.clear();
								$rootScope.authenticated = false;
								$location.path("/login");
							}
							$scope.displayError();

						});
			};

			$scope.getReports = function(url) {
				$http.get(url, LocalStorage.getHeader()).success(
						function(response) {
							$scope.reports = response;
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

			$scope.getAll = function() {
				$scope.getReports("api/report/");
				$scope.sortType = 'ALL';
			};

			$scope.getToday = function() {
				$scope.getReports("api/report/today");
				$scope.sortType = 'TODAY';
			};

			$scope.getComplete = function() {
				$scope.getReports("api/report/complete");
				$scope.sortType = 'CLOSED';
			};

			$scope.getIncomplete = function() {
				$scope.getReports("api/report/incomplete");
				$scope.sortType = 'OPEN';
			};

			$scope.assign = function(report) {				
				var url = 'api/employee/open?lat=' + report.latitude + '&lon=' + report.longitude;

				$http.get(url, LocalStorage.getHeader()).success(
						function(data) {
							$scope.reportId = report.id;
							$scope.employees = data;
							$scope.openEmp(report);
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

			$scope.openEmp = function(report) {
				$scope.report = report;
				var modalInstance = $modal.open({
					templateUrl : 'modals/availableEmp.html',
					controller : 'availableEmp',
					size : 'lg',
					resolve : {
						employees : function() {
							return $scope.employees;
						},
						report : function() {
							return $scope.report;
						}
					}
				});
				
				modalInstance.result.then(function(email) {
					report.employeeId = email;					
				}, function() {
				});
			};

			$scope.open = function(report) {

				$scope.report = report;

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
					if (($scope.sortType === 'OPEN' && action === 'COMPLETE')
							|| ($scope.sortType === 'CLOSED' && action === 'REOPEN')) {
						$scope.reports.splice($scope.reports.indexOf($scope.report), 1);
					} else if (action === 'UNASSIGN') {
						$scope.report.employeeId = null;
					} else if (action === 'COMPLETE') {
						$scope.report.employeeId = null;
						$scope.report.status = true;
					} else if (action === 'REOPEN') {
						$scope.report.status = false;
					}
				}, function() {
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

		});