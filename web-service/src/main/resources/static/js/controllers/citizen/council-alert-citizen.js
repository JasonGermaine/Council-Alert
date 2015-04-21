angular.module('councilalert')
	.controller('citizen', function($rootScope, $scope, $http, $location, $route, LocalStorage,	$modal) {
		
		$rootScope.reports = {};
		$scope.citzError = false;
		$scope.errorMessage = '';
		$scope.noMoreData = false;
		
		$scope.searchCitz = [];
		$scope.searchError = false;

		$scope.citizens = [];
		
		$http.get("api/admin/citizen", LocalStorage.getHeader()).success(
				function(response) {
					$scope.resetError();
					$scope.searchCitz = [];
					$scope.citizens = response;
					
					// Hide the more button if we have no more data to come
					if (response.length < 30) {
						$scope.noMoreData = true;
					} else {
						$scope.noMoreData = false;
					}
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
			$scope.searchError = false;
			$scope.errorMessage = '';			
		};
		
		$scope.loadMore = function() {
			$http.get("api/admin/citizen/" + $scope.citizens.length, LocalStorage.getHeader())
				.success(function(response) {
					if (response.length) {
						var skip = false;
						
						// Ensure that we do not add a citizen that we have already
						// manually retrieved
						angular.forEach(response, function(ctz) {					
							angular.forEach($scope.searchCitz, function(searchedCtz) {
								if (searchedCtz.email === ctz.email) {
									skip = true;
								}   	    	
						    });
							if (!skip) {
								$scope.citizens.push(ctz);
							}
					    });
						if (response.length < 30) {
							$scope.noMoreData = true;
						}
					} else {
						$scope.noMoreData = true;
					}
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						$scope.logout();
					}
					$scope.displayError();
				});
		}
		
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

		$scope.search = function(query) {
			$http.get("api/citizen/" + query, LocalStorage.getHeader())
				.success(function(response) {
					$scope.resetError();
					$scope.addCitzIfNotExists(response);
				}).error(function(resp, status) {
					if (status === 401 || status === 403) {
						$scope.logout();
					}
					$scope.searchError = true;
				});
		}
		
		$scope.addCitzIfNotExists = function(ctz) {
			var skip = false;
			
			angular.forEach($scope.citizens, function(citz) {
				if (citz.email === ctz.email) {
					skip = true;
				}				
		    });
			if (!skip) {
				$scope.citizens.push(ctz);
				$scope.searchCitz.push(ctz);
			}
		}
	});