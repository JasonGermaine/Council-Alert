angular.module('councilalert').controller('changePassword', function($rootScope, $scope, $http, $location, $route, LocalStorage,
		$modalInstance) {

	$scope.pwdRegex = '[A-Za-z0-9!?.$%]*';
	$scope.passwordError = false;
	$scope.errorMessage = '';
	
	$scope.request = {};	
	$scope.change = function () {
		$http.put("api/admin/employee/password/" + $rootScope.user.email, $scope.request, LocalStorage.getHeader())
			.success(function(resp) {
				$scope.passwordError = false;
				$scope.errorMessage = '';
				$scope.cancel();
				$location.path("/");
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {					
					LocalStorage.clear();
					$rootScope.authenticated = false;
					$location.path("/login");
				} else if (resp.message) {
					$scope.passwordError = true;
					$scope.errorMessage = resp.message;
				} else {
					$scope.passwordError = true;
					$scope.errorMessage = 'An unexpected error has occurred.';
				}
			});
	};
	
	$scope.cancel = function () {
		  $modalInstance.dismiss('cancel');
	};		 
});