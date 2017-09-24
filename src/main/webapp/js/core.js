var app = angular.module('mainApp', [ 'ui.bootstrap', 'ngRoute' ]);
console.log(app);
/**
 * Angular Routes
 */
app.config(function($routeProvider) {

	// route for the home page
	$routeProvider.when('/home', {
		templateUrl : 'partials/home.html',
		controller : 'homeController'
	});

	// route for the registeration page
	$routeProvider.when('/registeration', {
		templateUrl : 'partials/registerPage.html',
		controller : 'registerationController'
	});

	// route for the login page
	$routeProvider.when('/login', {
		templateUrl : 'partials/loginPage.html',
		controller : 'loginController'
	});

	// route for the Contacts page
	$routeProvider.when('/contacts', {
		templateUrl : 'partials/contacts.html',
		controller : 'contactsController'
	});

	// route for the chat page
	$routeProvider.when(':id/chat', {
		templateUrl : 'partials/chat.html',
		controller : 'chatController'
	});

	// if none of the above states are matched, use this as the fallback
	$routeProvider.otherwise({
		redirectTo : '/home'
	});
});
