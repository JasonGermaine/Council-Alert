angular
		.module('councilalert')
		.controller(
				'profile',
				function($rootScope, $scope, $http, $location, $route,
						LocalStorage, $modal) {
				
					$scope.profileError = false;
					$scope.errorMessage = '';
					$scope.numRegex = '[0-9+/-]*';
					
					$scope.request = {};

					$scope.currentUser = null;
					$scope.currentUser = angular.copy($rootScope.user);

					$scope.phoneEditable = false;

					$scope.phoneEdit = function() {
						$scope.phoneEditable = true;
					};

					$scope.clearPhoneUnedit = function() {
						$scope.phoneEditable = false;
						$scope.currentUser.phoneNum = angular
								.copy($rootScope.user.phoneNum);
					};

					$scope.update = function() {
						$scope.updateRequest = {};
						$scope.updateRequest.deviceId = $scope.currentUser.deviceId;
						$scope.updateRequest.phoneNum = $scope.currentUser.phoneNum;
						$scope.updateRequest.latitude = $scope.currentUser.latitude;
						$scope.updateRequest.longitude = $scope.currentUser.longitude;

						$http
								.put(
										"api/admin/employee/"
												+ $rootScope.user.email,
										$scope.updateRequest,
										LocalStorage.getHeader())
								.success(
										function(resp) {
											
											$scope.profileError = false;
											$scope.errorMessage = '';
											
											$rootScope.user.phoneNum = angular
													.copy($scope.currentUser.phoneNum);
											$rootScope.user.latitude = angular
													.copy($scope.currentUser.latitude);
											$rootScope.user.longitude = angular
													.copy($scope.currentUser.longitude);

											if ($scope.currentUser.deviceId === 'DELETED') {
												$rootScope.user.deviceId = null;
											}

											$location.path("/");
										}).error(function(resp, status) {
											if (status === 401 || status === 403) {
												LocalStorage.clear();
												$rootScope.authenticated = false;
												$location.path("/login");
											} else if (resp.message) {
												$scope.profileError = true;
												$scope.errorMessage = resp.message;
											} else {
												$scope.profileError = true;
												$scope.errorMessage = "An unexpected error has occurred.";
											}

								});
					};

					$scope.clearDevice = function() {
						$scope.currentUser.deviceId = 'DELETED';
					};

					$scope.resetDevice = function() {
						$scope.currentUser.deviceId = angular
								.copy($rootScope.user.deviceId);
					};

					$scope.phoneUnedit = function() {
						$scope.phoneEditable = false;
					};

					$scope.setUnedit = function() {
						$scope.editable = false;
						$scope.currentUser = $rootScope.user;
					};

					$scope.reset = function() {
						$scope.resetLocation();
						$scope.resetDevice();
						$scope.clearPhoneUnedit();
					};

					$scope.resetLocation = function() {
						$scope.currentUser.latitude = angular
								.copy($rootScope.user.latitude);
						$scope.currentUser.longitude = angular
								.copy($rootScope.user.longitude);
						$scope.moveMarker($rootScope.user.latitude,
								$rootScope.user.longitude);
					};

					$scope.moveMarker = function(lat, lon) {
						$scope.map = {
							center : {
								latitude : lat,
								longitude : lon
							},
							zoom : 12,
							marker : {
								id : 0,
								coords : {
									latitude : lat,
									longitude : lon
								}
							}
						};
					};

					$scope.moveMarker($scope.currentUser.latitude,
							$scope.currentUser.longitude);

					$scope.changePassword = function() {
						var modalInstance = $modal.open({
							templateUrl : 'modals/changePassword.html',
							controller : 'changePassword',
							size : 'md'
						});
					};

					$scope.changeLocation = function() {
						var modalInstance = $modal.open({
							templateUrl : 'modals/changeLocation.html',
							controller : 'changeLocation',
							size : 'md'
						});

						modalInstance.result.then(function(location) {
							$scope.currentUser.latitude = location.latitude;
							$scope.currentUser.longitude = location.longitude;
							$scope.moveMarker(location.latitude,
									location.longitude);
							$scope.newLocation = true;
						}, function() {

						});
					};

				});