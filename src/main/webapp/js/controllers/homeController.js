angular.module('mainApp').controller('homeController', function($scope,$rootScope,$location,AuthenticationService) {

	(function initController() {
		console.log($rootScope.globals);
			// check if $rootScope.globals exist or not which means that user user credentials are loaded from cookies
			if(!$rootScope.globals || ($rootScope.globals && !$rootScope.globals.currentUser)){
				$location.path('/login');
			}
		})();

	$scope.logout = function(){
		AuthenticationService.ClearCredentials();
		$location.path('/login');
	}

	$scope.register = function() {
		window.location = "#/registeration"
	}

	$scope.login = function() {
		window.location = "#/login"
	}

});
