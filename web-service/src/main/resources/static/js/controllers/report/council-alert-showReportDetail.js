angular
		.module('councilalert')
		.controller(
				'showReportDetail',
				function($rootScope, $scope, $http, $location, $route,
						$timeout, LocalStorage, $modalInstance, report, $modal) {

					$scope.report = report;
					$scope.reportError = false;
					$scope.errorMessage = '';

					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};

					$scope.resetError = function() {
						$scope.reportError = false;
						$scope.errorMessage = '';
					};

					$scope.manageError = function(resp, status) {
						if (status === 401 || status === 403) {
							LocalStorage.clear();
							$rootScope.authenticated = false;
							$location.path("/login");
						} else if (resp.message) {
							$scope.reportError = true;
							$scope.errorMessage = resp.message;
						} else {
							$scope.reportError = true;
							$scope.errorMessage = "An unexpected error occurred. Please try again.";
						}
					};

					$scope.openImage = function(image) {
						$scope.image = image;
						var modalInstance = $modal.open({
							templateUrl : 'modals/showImage.html',
							controller : 'displayImage',
							size : 'md',
							resolve : {
								image : function() {
									return $scope.image;
								}
							}
						});
					};

					$scope.changeStatus = function(reportId, status) {
						$http.put(
								"api/admin/report/" + reportId + "/" + status,
								{}, LocalStorage.getHeader()).success(
								function(data) {
									$scope.resetError();
									$scope.cancel();
								}).error(function(resp, status) {
							$scope.manageError(resp, status);
						});
					};

					$scope.unassign = function(reportId) {
						$http.get("api/admin/report/unassign/" + reportId,
								LocalStorage.getHeader()).success(
								function(data) {
									$scope.resetError();
									$scope.cancel();
								}).error(function(resp, status) {
							$scope.manageError(resp, status);
						});
					};

					$scope.mapSetup = function() {
						$scope.map = {
							center : {
								latitude : $scope.report.latitude,
								longitude : $scope.report.longitude
							},
							zoom : 12,
							marker : {
								id : 0,
								coords : {
									latitude : $scope.report.latitude,
									longitude : $scope.report.longitude
								}
							}
						};
					}

					$timeout(function() {
						$scope.mapSetup();
					}, 250);

				});