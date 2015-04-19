angular.module('councilalert')
	.controller('displayUserReports', function($rootScope, $scope, $http, $location, $route, $modal,
		LocalStorage, $modalInstance, userReports) {

		$scope.userReports = userReports;
		
		$scope.cancel = function() {
			$modalInstance.dismiss('cancel');
		};

		$scope.openReport = function(report) {
			$scope.report = report;

			var modalInstance = $modal.open({
				templateUrl : '/modals/showReportDetail.html',
				controller : 'showReportDetail',
				size : 'lg',
				resolve : {
					report : function() {
						return $scope.report;
					}
				}
			});
		};
	});