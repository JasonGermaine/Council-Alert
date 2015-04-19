angular.module('councilalert')
	.controller('addEmp', function($rootScope, $scope, $http, $location, $route, LocalStorage) {
					
		$scope.emp = {};
		$scope.pwd2 = '';
		$scope.error = false;
		$scope.incomplete = false;
		$scope.nameRegex = '[A-Za-z]*';
		$scope.numRegex = '[0-9+/-]*';
		$scope.pwdRegex = '[A-Za-z0-9!?.$%]*';
		$scope.errorMessage = '';
		$scope.formError = false;

		$scope.$watch('emp.pwd', function() {
			$scope.test();
		});
		
		$scope.$watch('pwd2', function() {
			$scope.test();
		});

		$scope.$watch('emp.latitude', function() {
			$scope.test();
		});

		$scope.$watch('emp.longitude', function() {
			$scope.test();
		});

		// A validation listener for form fields
		$scope.test = function() {
			if ($scope.emp.password !== $scope.pwd2) {
				$scope.error = true;
			} else {
				$scope.error = false;
			}

			$scope.incomplete = false;
			if (!$scope.emp.latitude.length
					|| !$scope.emp.longitude.length) {
				$scope.incomplete = true;
			}
		};

		$scope.register = function() {
			$http.post('api/admin/employee', $scope.emp, LocalStorage.getHeader())
				.success(function(data) {
					$location.path("/");
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						LocalStorage.clear();
						$rootScope.authenticated = false;
						$location.path("/login");
					} else if (resp.message) {
						$scope.formError = true;
						$scope.errorMessage = resp.message;
					} else {
						$scope.formError = true;
						$scope.errorMessage = "An unexpected error occurred. Please try again.";
					}
				});
		};
	});