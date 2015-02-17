function userController($scope, $http) {
			$scope.fName = '';
			$scope.lName = '';
			$scope.pwd = '';
			$scope.pwd2 = '';
			$scope.email = '';
			$scope.phoneNum = '';
			$scope.lat = '';
			$scope.lon = '';
			$scope.error = false;
			$scope.incomplete = false;

			$scope.$watch('pwd', function() {
				$scope.test();
			});
			$scope.$watch('pwd2', function() {
				$scope.test();
			});
			$scope.$watch('fName', function() {
				$scope.test();
			});
			$scope.$watch('lName', function() {
				$scope.test();
			});
			$scope.$watch('email', function() {
				$scope.test();
			});
			$scope.$watch('phoneNum', function() {
				$scope.test();
			});
			$scope.$watch('lat', function() {
				$scope.test();
			});
			$scope.$watch('lon', function() {
				$scope.test();
			});
			$scope.test = function() {
				if ($scope.pwd !== $scope.pwd2) {
					$scope.error = true;
				} else {
					$scope.error = false;
				}
				$scope.incomplete = false;
				if (!$scope.fName.length || !$scope.lName.length
					|| !$scope.pwd.length || !$scope.pwd2.length
					|| !$scope.email.length || !$scope.phoneNum.length
					|| !$scope.lat.length || !$scope.lon.length) {
					
					$scope.incomplete = true;
				}
			};

			$scope.register = function() {
				var dataObj = {
						email : $scope.email,
						firstName : $scope.fName,
						lastName : $scope.lName,
						password : $scope.pwd,
						phoneNum : $scope.phoneNum,
						latitude : $scope.lat,
						longitude : $scope.lon
				};	
				$http.post('http://**/api/employee/create', dataObj)
						.success(function(data) {
							window.location.href = "displayEmployee.html";
						});
			};
		}