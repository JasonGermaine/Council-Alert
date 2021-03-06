angular.module('councilalert')
	.controller('changeLocation', function($rootScope, $scope, $http, $location, $route, 
		LocalStorage, $modalInstance) {
	
		$scope.location = {};
		
		// Callback function with the newly entered location
		$scope.change = function () {
		    $modalInstance.close($scope.location);
		};
		  
		$scope.cancel = function () {
			  $modalInstance.dismiss('cancel');
		};		 
});