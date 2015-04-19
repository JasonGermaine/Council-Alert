angular.module('councilalert')
	.controller('citizen', function($rootScope, $scope, $http, $location, $route, LocalStorage,	$modal) {
		
		$rootScope.reports = {};
		$scope.citzError = false;
		$scope.errorMessage = '';
			
		
		$http.get("api/admin/citizen", LocalStorage.getHeader()).success(
				function(response) {
					$scope.resetError();
					$scope.citizens = response;
				}).error(function(resp, status) {
			if (status === 401 || status === 403) {
				$scope.logout();
			}
			$scope.displayError();
		});
		
		$scope.getReports = function(email) {
			var url = 'api/citizen/report/' + email
			$http.get(url, LocalStorage.getHeader()).success(
					function(data) {
						$scope.resetError();
						$scope.userReports = data;
						$scope.open();
					}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					$scope.logout();
				}
				$scope.displayError();
			});
		};

		
		$scope.logout = function() {
			$rootScope.authenticated = false;
			LocalStorage.clear();
			$location.path("/login");
		};
		
		$scope.displayError = function() {
			$scope.citzError = true;
			$scope.errorMessage = 'An unexpected error has occurred.';
		};

		$scope.resetError = function() {
			$scope.citzError = false;
			$scope.errorMessage = '';
		};
		
		// Opens a model displaying citizen reports
		$scope.open = function() {
			var modalInstance = $modal.open({
				templateUrl : 'displayUserReports.html',
				controller : 'displayUserReports',
				size : 'md',
				resolve : {
					userReports : function() {
						return $scope.userReports;
					}
				}
			});
		};
	});