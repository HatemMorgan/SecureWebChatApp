angular.module('mainApp').controller('contactsController',function($scope, contactsService){
	console.log("in Contacts Controller");

	 contactsService.GetContacts(function(contacts){
		 console.log(contacts);
		 	$scope.names = contacts;
	});

	$scope.beginChat = function(receiverName){
			$scope.receiver = receiverName;
			contactsService.InitChat(receiverName,function(initiated,key){
					if(initiated){
						console.log("Chatting with "+$scope.receiver);
							console.log(key);
							$scope.chatKey = key;
							console.log(	$scope.chatKey);

							// messages which will be used by ng-repeat in contacts.html
							 contactsService.GetMessages($scope.receiver,$scope.chatKey,function(success,messages){
								 if(success){
									$scope.messages = messages;
									console.log("hereee");
								}
							});
					}
			});
	}

	$scope.sendMessage = function(){
		console.log(	$scope.chatKey);
		 contactsService.encryptAndSend($scope.message, $scope.chatKey, $scope.receiver,function(success){
				 if(success){
					 $scope.messages.add($scope.message);
				 }else{
					 alert("Message was not sent. Please try again");
				 }
			});

	}

});
