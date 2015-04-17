angular.module('councilalert').controller('assignReport', function($rootScope, $scope, $http, $location, $route, LocalStorage, 
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
				$modalInstance.close(id);
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

	
});