angular.module('mainApp').factory('contactsService',contactsService);
contactsService.$inject = ['$http','$rootScope', 'MainService', 'AuthenticationService'];

function contactsService($http, $rootScope, MainService, AuthenticationService, contactsService){

	console.log('in contacts service');

	var service = {};
	service.GetContacts = GetContacts;
	return service;


	function GetContacts(callback){
    var username = AuthenticationService.GetUserName();
    var password = AuthenticationService.GetPassword();
		var token = AuthenticationService.CreateAccessToken(username,password);
  	var req = {
				method: 'GET',
				url: 'rest/contacts',
				headers:
				{
					'x-access-token': token
				}
		};

		return $http(req).then(function successCallback(response){
			if(response){
				// callback with true flag indicating that registeration
				// completed successfully
				// console.log(response.data.contacts);
				callback(response.data.contacts);
			}else{
				alert(response);
				// callback with true flag indicating that registeration
				// failed
				callback(null);
			}
		},
		function errorCallback(response) {
			alert(response);
			console.log(respone.data);
			callback(false,response.data);
		});
	}

}
