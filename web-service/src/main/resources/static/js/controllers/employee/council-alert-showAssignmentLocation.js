angular.module('councilalert').controller(
		'showAssignmentLocation',
		function($rootScope, $scope, $http, $location, $route, LocalStorage,
				$modalInstance, $timeout, employee, report) {

			$scope.employee = employee;
			$scope.report = report;

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};

			$scope.mapSetup = function() {
				$scope.map = {
					center : {
						latitude : $scope.employee.latitude,
						longitude : $scope.employee.longitude
					},
					zoom : 12
				};
			}

			$scope.markers = [ {
				id : 0,
				coords : {
					latitude : $scope.employee.latitude,
					longitude : $scope.employee.longitude
				}
			}, {
				id : 2,
				coords : {
					latitude : $scope.report.latitude,
					longitude : $scope.report.longitude
				}
			} ];
			$timeout(function() {
				$scope.mapSetup();
			}, 250);

		});