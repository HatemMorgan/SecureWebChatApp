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
							$scope.chatKey = key;
							// messages which will be used by ng-repeat in contacts.html
							 contactsService.GetMessages($scope.receiver,$scope.chatKey,function(success,messages){
								 if(success){
									 console.log(messages);
									$scope.messages = messages;
								}
							});
					}
			});
	}

	$scope.sendMessage = function(){
		 contactsService.encryptAndSend($scope.message, $scope.chatKey, $scope.receiver,function(success){
				 if(success){
					 console.log("Message Sent");
					 $scope.messages.add($scope.message);
				 }else{
					 alert("Message was not sent. Please try again");
				 }
			});

	}

});
