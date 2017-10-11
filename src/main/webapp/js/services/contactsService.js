angular.module('mainApp').factory('contactsService',contactsService);
contactsService.$inject = ['$http','$rootScope', 'MainService', 'AuthenticationService'];

function contactsService($http, $rootScope, MainService, AuthenticationService, contactsService){

	// console.log('in contacts service');

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
				var contacts = response.data.contacts;
				var digest = "[" + contacts.join() + "]";

				MainService.GetServerSignPubKey(function(serverRSASigPubKey){
					// console.log(serverRSASigPubKey);
					// console.log(digest);
					var keyParams = serverRSASigPubKey.split(":");
					var n = keyParams[0];
					var e = keyParams[1];

					RSAKeyPair = cryptico.generateRSAKey("xx", 512);
					cryptico.setPublicKey(RSAKeyPair,n,e);
					var verified = cryptico.verify(digest,response.data.signature,RSAKeyPair);
					// console.log(verified);
				 // if the Signature is verified then return contacts
					if(verified){
						callback(contacts);
					}else{
						alert("The response is corrupted. Signature is not valid. Please check your internet connection and try again");
					}

				});



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
