(function () {
    'use strict';
var app = angular.module('mainApp', ['ngRoute', 'ngCookies','luegg.directives']);
// console.log(app);
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
	$routeProvider.when('/register', {
		templateUrl : 'partials/registerPage.html',
		controller : 'registerController'
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

  // route for the Inbox page
	$routeProvider.when('/inbox', {
		templateUrl : 'partials/inbox.html',
		controller : 'inboxController'
	});

	// route for the chat page
	$routeProvider.when(':id/chat', {
		templateUrl : 'partials/chat.html',
		controller : 'chatController'
	});

	// if none of the above states are matched, use this as the fallback
	$routeProvider.otherwise({
		redirectTo : '/login'
	});
});

app.run(run);

run.$inject = ['$rootScope', '$location', '$cookies', '$http'];
	 function run($rootScope, $location, $cookies, $http) {
			 // keep user logged in after page refresh
       if( $cookies.getObject('globals')){
			      $rootScope.globals = $cookies.getObject('globals') || {};
            $location.path('/home');
     }
			//  if ($rootScope.globals.currentUser) {
			// 		 $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
			//  }




			 $rootScope.$on('$locationChangeStart', function (event, next, current) {
					 // redirect to login page if not logged in and trying to access a restricted page
					 var restrictedPage = $.inArray($location.path(), ['/login', '/register']) === -1;
					 var loggedIn = $rootScope.globals;
					 if (restrictedPage && !loggedIn) {
							 $location.path('/login');
					 }
			 });
	 }

})();
