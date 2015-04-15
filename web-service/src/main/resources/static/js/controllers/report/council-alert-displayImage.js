angular.module('councilalert').controller(
		'displayImage',
		function($rootScope, $scope, $http, $location, $route, $modalInstance,
				image, LocalStorage) {

			$scope.image = image;

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});