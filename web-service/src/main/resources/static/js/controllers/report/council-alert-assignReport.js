angular.module('councilalert')
	.controller('assignReport', function($rootScope, $scope, $http, $location, $route, LocalStorage, 
			DistanceFactory, $modalInstance, reports, employee) {

		// Append the distance field onto each object
		angular.forEach(reports, function(report) {
	    	  report.distance = parseFloat(
	    			  (DistanceFactory.getDistanceBetweenLocations(
	    					  employee.latitude,employee.longitude,report.latitude,report.longitude)
	    			  ).toFixed(4)); 	    	
	      });
	
		$scope.reports = reports;
		$scope.email = employee.email;
		$scope.assignError = false;
		$scope.errorMessage = '';
		
		$scope.cancel = function () {
			  $modalInstance.dismiss('cancel');
		};
		
		$scope.assignReport = function(id) {
			var url = 'api/admin/employee/assign?email=' + $scope.email + '&id=' + id;
			
			$http.get(url, LocalStorage.getHeader())
				.success(function(data, status, headers, config) {
					$modalInstance.close(id);
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						LocalStorage.clear();
						$rootScope.authenticated = false;
						$location.path("/login");
					} else if (resp.message) {
						$scope.assignError = true;
						$scope.errorMessage = resp.message;
					} else {
						$scope.assignError = true;
						$scope.errorMessage = "An unexpected error occurred. Please try again.";
					} 
				});
		};
});