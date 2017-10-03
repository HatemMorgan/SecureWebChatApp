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

				// data = digest+":"+signature
				// digest = flatened list of messages
				var data = response.data.signature.split("--");

				//Verifying the signature
				MainService.GetServerSignPubKey(function(serverRSASigPubKey){

					var keyParams = serverRSASigPubKey.split(":");
					var n = keyParams[0];
					var e = keyParams[1];

					RSAKeyPair = cryptico.generateRSAKey("xx", 512);
					cryptico.setPublicKey(RSAKeyPair,n,e);
					var verified = cryptico.verify(data[0],data[1],RSAKeyPair);

				 // if the Signature is verified then return messages after decrypting them.
					if(verified){
						var myPubKey = AuthenticationService.GetRSAEncObj(username,password);
		        var encryptedInbox = response.data.inbox;
						// decrypted each inbox message by chatkey after decrypting chatkey by user's RSA private key
						for(var i = 0; i < encryptedInbox.length; i++){
							var symmetricKey = cryptico.RSADecrypt(encryptedInbox[i].encryptedChatKey, myPubKey);
							var decryptedMess = cryptico.AESDecrypt(encryptedInbox[i].message, symmetricKey);
							encryptedInbox[i].message = decryptedMess;

						}
						callback(encryptedInbox);
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
