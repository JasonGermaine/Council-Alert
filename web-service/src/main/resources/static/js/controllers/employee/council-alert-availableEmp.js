angular
		.module('councilalert')
		.controller(
				'availableEmp',
				function($rootScope, $scope, $http, $location, $route,
						LocalStorage, $modalInstance, employees, reportId) {

					$scope.employees = employees;
					$scope.reportId = reportId;
					$scope.empAssignError = false;
					$scope.errorMessage = '';

					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};

					$scope.assignEmp = function(email) {
						var url = 'api/admin/employee/assign?email=' + email
								+ '&id=' + reportId;

						$http
								.get(url, LocalStorage.getHeader())
								.success(function(data) {
									$scope.cancel();
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