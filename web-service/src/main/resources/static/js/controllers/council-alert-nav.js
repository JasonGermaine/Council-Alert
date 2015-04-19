angular.module('councilalert').controller('navigation', function($rootScope, $scope, $http, $location, $route, LocalStorage, dashboardService) {

	$scope.credentials = {};
	$scope.errorMessage = '';
	
	$scope.toReport = function(key) {
		dashboardService.setKey(key);
		$location.path("/report");
	};
	
	$scope.toEmployee = function(key) {
		dashboardService.setKey(key);
		$location.path("/employee");
	};
	
	// Get statistics for visualization
	$scope.getStats = function() {
		if ($scope.authenticated == true) {
			$http.get("api/admin/stats", LocalStorage.getHeader()).success(function(response) {
				$scope.stats = response;
				$scope.reportLabels = ['Incomplete', 'Complete'];
				$scope.empLabels = ['Inactive', 'Active'];
				$scope.reportData = [ $scope.stats.report_incomplete, $scope.stats.report_complete];
				$scope.empData = [ $scope.stats.emp_unassigned, $scope.stats.emp_assigned];
				$scope.colours = [
	                   { // black
	                     fillColor: 'rgba(34,2,0,0.2)',
	                     strokeColor: 'rgba(34,2,0,1)',
	                     pointColor: 'rgba(34,2,0,1)',
	                     pointStrokeColor: '#fff',
	                     pointHighlightFill: '#fff',
	                     pointHighlightStroke: 'rgba(34,2,0,0.8)'
	                   },
	                   { // dark grey
	                     fillColor: 'rgba(77,83,96,0.2)',
	                     strokeColor: 'rgba(77,83,96,1)',
	                     pointColor: 'rgba(77,83,96,1)',
	                     pointStrokeColor: '#fff',
	                     pointHighlightFill: '#fff',
	                     pointHighlightStroke: 'rgba(77,83,96,1)'
	                   }
				];
			}).error(function(resp, status) {
				if (status === 401 || status === 403) {
					$scope.logout();
				}
		    });
		}
	};
	
	// Method to be called on application init
	$scope.init = function() {
		$scope.email = LocalStorage.retrieveEmail();
		var token = LocalStorage.retrieveToken(); 
		
		// check if user is already logged in
		if($scope.email != null && $scope.email != '' && token != null && token != '') {			
			var user = {
					email: $scope.email
			};
			$http.get("api/admin/employee/" + $scope.email, LocalStorage.getHeader())
			.success(function(response) {
				$rootScope.user = response;
				$rootScope.authenticated = true;
				$location.path("/");				
				$scope.getStats();
			}).error(function(response) {
				$scope.logout();
			});					
		} else {
			$scope.logout();
		}
	}
	
	$scope.login = function() {		
		
		// OAuth2 Request Data
		var authdata = btoa("angular-client:council-alert-angular-secret");
		var request = {
            "username" : $scope.credentials.username,
            "password" : $scope.credentials.password,
            "grant_type" : "password",
            "scope" : "read write trust",
            "client_id" : "angular-client",
            "client_secret" : "council-alert-angular-secret"
		};
		var config = {
			headers : { 
				"Authorization" : 'Basic ' + authdata,
				"Content-type" : 'application/x-www-form-urlencoded',
				"Accept" : "application/json"
			}
		};		
			
		$http.post('oauth/token', $.param(request), config)
			.success(function(response) {
				$scope.oauth_resp = response;
				if ($scope.oauth_resp.access_token) {
					// store the oauth token
					LocalStorage.storeToken('Bearer ' + $scope.oauth_resp.access_token);
					LocalStorage.storeEmail($scope.credentials.username);			
					
					$http.get("api/admin/employee/" + $scope.credentials.username, LocalStorage.getHeader()).success(function(response) {
						$rootScope.user = response;
						$rootScope.authenticated = true;
						$location.path("/");
					}).error(function(response) {
						$rootScope.authenticated = false;
						$rootScope.error = true;
						LocalStorage.clear();					
					});
				}
			}).error(function(data, status) {
				$rootScope.authenticated = false;
				$rootScope.error = true;			
				LocalStorage.clear();
				if ( status === 401 || status === 400) {
					$scope.errorMessage = 'Incorrect credentials entered.';
				} else {
					$scope.errorMessage = 'There was a problem logging in. Please try again.';
				}
			})
	};
	
	$scope.logout = function() {
		$rootScope.authenticated = false;
		LocalStorage.clear();
		$location.path("/login");
	};
	
	// Get stats on page load
	$scope.getStats();
});