angular.module('councilalert').controller('removeEmp', function($rootScope, $scope, $http, $location, $route, $modalInstance, employee, LocalStorage) {
	
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
				} else if (resp.message) {
					$scope.removeError = true;
					$scope.errorMessage = resp.message;
				} else {
					$scope.removeError = true;
					$scope.errorMessage = "An unexpected error occurred. Please try again.";
				}
		    });
	};
});