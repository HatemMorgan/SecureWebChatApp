angular.module('mainApp').factory('inboxService',inboxService);
inboxService.$inject = ['$http','$rootScope', 'MainService', 'AuthenticationService'];

function inboxService($http, $rootScope, MainService, AuthenticationService, contactsService){

	console.log('in contacts service');

	var service = {};
  service.GetInbox = GetInbox;
	return service;


	function GetInbox(callback){
    var username = AuthenticationService.GetUserName();
    var password = AuthenticationService.GetPassword();
		var token = AuthenticationService.CreateAccessToken(username,password);
  	var req = {
				method: 'GET',
				url: 'rest/inbox',
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
        var encryptedInbox = response.data.inbox;
        console.log(encryptedInbox);
				callback(encryptedInbox);
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
