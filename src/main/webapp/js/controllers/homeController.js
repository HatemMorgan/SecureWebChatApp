angular.module('mainApp').controller('homeController', function($scope) {

	$scope.register = function() {
		window.location = "#/registeration"
	}

	$scope.login = function() {
		window.location = "#/login"
	}

});
