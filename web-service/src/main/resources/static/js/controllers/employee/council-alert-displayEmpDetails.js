angular.module('councilalert').controller(
		'displayEmpDetails',
		function($rootScope, $scope, $http, $location, $route, $timeout,
				$modalInstance, employee, LocalStorage) {

			$scope.emp = employee;
			$scope.edit = false;

			$scope.mapSetup = function() {
				$scope.map = {
					center : {
						latitude : $scope.emp.latitude,
						longitude : $scope.emp.longitude
					},
					zoom : 12,
					marker : {
						id : 0,
						coords : {
							latitude : $scope.emp.latitude,
							longitude : $scope.emp.longitude
						}
					}
				};
			}

			$timeout(function() {
				$scope.mapSetup();
			}, 250);

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};

			$scope.ok = function() {

			};
		});