angular.module('mainApp').factory('chatService',chatService);
chatService.$inject = ['$http','$rootScope', 'MainService', 'AuthenticationService'];

function chatService($http, $rootScope, MainService, AuthenticationService, contactsService){

	console.log('in contacts service');

	var service = {};
  service.InitChat = InitChat;
  service.GetMessages = GetMessages;
  service.encryptAndSend = encryptAndSend;
	return service;

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
        var symmetricKey = cryptico.bytes2string(cryptico.generateAESKey());

        var n = receiverPubKey[0];
        var e = receiverPubKey[1];
        var receiverRSAKey = cryptico.generateRSAKey("xx", 512);
        cryptico.setPublicKey(receiverRSAKey,n,e);

        var senderRSAKey = AuthenticationService.GetRSAEncObj(username,password);

        var keyEncBySender = cryptico.RSAEncrypt(symmetricKey,senderRSAKey);
        var keyEncByReceiver = cryptico.RSAEncrypt(symmetricKey,receiverRSAKey);

        var digest = keyEncBySender + ":" + keyEncByReceiver;
          // console.log(cryptico.getPubKey(AuthenticationService.GetRSASignObj(username,password)));
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
								var receivedData = response.data.data.split(":");
								var encryptedChatKey = receivedData[0];
                var chatKey = cryptico.RSADecrypt(encryptedChatKey, senderRSAKey);
								var signature = receivedData[1];

								//Verifying the signature
								MainService.GetServerSignPubKey(function(serverRSASigPubKey){
									var keyParams = serverRSASigPubKey.split(":");
									var n = keyParams[0];
									var e = keyParams[1];

									RSAKeyPair = cryptico.generateRSAKey("xx", 512);
									cryptico.setPublicKey(RSAKeyPair,n,e);
									var verified = cryptico.verify(encryptedChatKey,signature,RSAKeyPair);
								 // if the Signature is verified then return contacts
									if(verified){
										callback(true, chatKey);
									}else{
										alert("The response is corrupted. Signature is not valid. Please check your internet connection and try again");
									}

								});

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
						// this messages are sorted by id. The most recent message is at the first
						var mess = response.data.messages;
						var decryptedMessages = [];
						// decrypt messages
						for(var i = 0; i<mess.length; i++){
							// use push to reverse array to make the most recent messages at the end of chat history in contacts.html
								mess[i].text = cryptico.AESDecrypt(mess[i].text,aesKey);
								decryptedMessages.unshift(mess[i]);
						}
						callback(true,decryptedMessages);
					}else{
						alert("The response is corrupted. Signature is not valid. Please check your internet connection and try again");
					}

				});


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
				var insertedMessage = response.data.insertedMessage;
				insertedMessage.text = newMessage;
				callback(true,insertedMessage);
			}else{
				alert(response);
				callback(false,null);
			}
		},
		function errorCallback(response) {
			alert(response);
			callback(false,null);
		});

  }

}
