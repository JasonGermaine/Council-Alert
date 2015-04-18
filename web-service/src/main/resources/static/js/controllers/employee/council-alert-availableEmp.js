angular
		.module('councilalert')
		.controller(
				'availableEmp',
				function($rootScope, $scope, $http, $location, $route,
						LocalStorage, $modal, $modalInstance, employees, report) {

					$scope.employees = employees;
					$scope.report = report;
					$scope.empAssignError = false;
					$scope.errorMessage = '';
					$scope.employee = {};

					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};

					$scope.assignEmp = function(email) {
						var url = 'api/admin/employee/assign?email=' + email
								+ '&id=' + $scope.report.id;

						$http
								.get(url, LocalStorage.getHeader())
								.success(function(data) {
									$modalInstance.close(email);
									$scope.empAssignError = false;
									$scope.errorMessage = '';
								})
								.error(
										function(resp, status) {
											if (status === 401
													|| status === 403) {
												LocalStorage.clear();
												$rootScope.authenticated = false;
												$location.path("/login");
											} else if (resp.message) {
												$scope.empAssignError = true;
												$scope.errorMessage = resp.message;
											} else {
												$scope.empAssignError = true;
												$scope.errorMessage = "An unexpected error occurred. Please try again.";
											}
										});
					};
					

					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};

				});