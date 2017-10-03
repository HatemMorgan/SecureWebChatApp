angular.module('mainApp').factory('contactsService',contactsService);
contactsService.$inject = ['$http','$rootScope', 'MainService', 'AuthenticationService'];

function contactsService($http, $rootScope, MainService, AuthenticationService, contactsService){

	console.log('in contacts service');

	var service = {};
	service.GetContacts = GetContacts;
  service.InitChat = InitChat;
  service.GetMessages = GetMessages;
  service.encryptAndSend = encryptAndSend;
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

  function InitChat(receiverName, callback){
    var username = AuthenticationService.GetUserName();
    var password = AuthenticationService.GetPassword();
    var token = AuthenticationService.CreateAccessToken(username,password);

    var req = {
      method: 'GET',
      url: 'rest/contacts/'+receiverName+'/pubKeys',
      headers:
      {
        'x-access-token': token
      }
    };
    return $http(req).then(function successCallback(response){
			if(response){
        var receiverPubKey = response.data.encryptionPubKey.split(":");
        console.log(receiverPubKey);
        var symmetricKey = cryptico.bytes2string(cryptico.generateAESKey());

        var n = receiverPubKey[0];
        var e = receiverPubKey[1];
        var receiverRSAKey = cryptico.generateRSAKey("xx", 512);
        cryptico.setPublicKey(receiverRSAKey,n,e);

        var senderRSAKey = AuthenticationService.GetRSAEncObj(username,password);

        var keyEncBySender = cryptico.RSAEncrypt(symmetricKey,senderRSAKey);
        var keyEncByReceiver = cryptico.RSAEncrypt(symmetricKey,receiverRSAKey);

        var digest = keyEncBySender + ":" + keyEncByReceiver;
          console.log(cryptico.getPubKey(AuthenticationService.GetRSASignObj(username,password)));
        var signature = cryptico.RSASign(digest,AuthenticationService.GetRSASignObj(username,password));


        var token2 = AuthenticationService.CreateAccessToken(username,password);

        var req2 = {
          method: 'POST',
          url: 'rest/chat/init?receiverName='+receiverName,
          data: {   keyEncBySender: keyEncBySender,
                    keyEncByReceiver: keyEncByReceiver,
                    signature: signature
                },
          headers:
          {
            'x-access-token': token2
          }
        };

        return $http(req2).then(function successCallback(response){
    			if(response){
            console.log(response);
    				callback(true,symmetricKey);
    			}else{
            console.log(response);
    				alert(response);
    				callback(false,null);
    			}
    		},
    		function errorCallback(response) {
          // check if status was 302 which indicates that a key was previously generated between two users
          if(response.status == 302){

            // get chat symmetric Key
            var token3 = AuthenticationService.CreateAccessToken(username,password);

            var req3 = {
              method: 'GET',
              url: 'rest/chat/chatKey?receiverName='+receiverName,
              headers:
              {
                'x-access-token': token3
              }
            };
            return $http(req3).then(function successCallback(response){
        			if(response){
                console.log(response.data.data);
                // console.log(response.data.data.split(":")[0]);
                console.log(senderRSAKey);
                var chatKey = cryptico.RSADecrypt(response.data.data.split(":")[0], senderRSAKey);
                console.log(chatKey);
        				callback(true, chatKey);
        			}else{
        				alert(response);
                console.log(response);
        				callback(false, null);
        			}
        		},
        		function errorCallback(response) {
        			alert(response);
        			console.log(respone);
        			callback(false,response.data.errMessage);
        		});


            // callback(true);
          }else{
            console.log(response);
            alert(response);
            callback(false,response.data.errMessage);

          }

    		});

			}else{
				alert(response);
				callback(null);
			}
		},
		function errorCallback(response) {
			alert(response);
      console.log(respone);
      callback(false,response.data.errMessage);

		});


  }
  function GetMessages(receiver,aesKey,callback){
    var username = AuthenticationService.GetUserName();
    var password = AuthenticationService.GetPassword();
    var token = AuthenticationService.CreateAccessToken(username,password);

    var req = {
      method: 'GET',
      url: 'rest/chat/getMessages?receiverName='+receiver,
      headers:
      {
        'x-access-token': token
      }
    };

    return $http(req).then(function successCallback(response){

			if(response){
        var mess = response.data.messages;

        for(var i = 0; i<mess.length; i++){
            mess.text = cryptico.AESDecrypt(mess.text,aesKey);
        }

        callback(true,mess);
			}else{
				alert(response);
				callback(false,null);
			}
		},
		function errorCallback(response) {
			alert(response);
			console.log(respone.data.errMessage);
			callback(false,response.data.errMessage);
		});
  }

  function encryptAndSend(newMessage,key, receiverName,callback){
    console.log(key);
    var username = AuthenticationService.GetUserName();
    var password = AuthenticationService.GetPassword();
    var token = AuthenticationService.CreateAccessToken(username,password);
    var encryptedMess = cryptico.AESEncrypt(newMessage, key);
    var digest = username + ":" + receiverName + ":" + encryptedMess;
    var signature = cryptico.RSASign(digest, AuthenticationService.GetRSASignObj(username,password));
    var req = {
        method: 'POST',
        url: 'rest/chat/sendMessage?receiverName=' + receiverName,
        data: {
          encMessage: encryptedMess,
          signature: signature
        },
        headers:
        {
          'x-access-token': token
        }
    };
    return $http(req).then(function successCallback(response){
			if(response){
				callback(true);
			}else{
				alert(response);
				callback(false);
			}
		},
		function errorCallback(response) {
			alert(response);
			callback(false);
		});

  }

}
