angular.module('mainApp').controller('contactsController',function($scope, contactsService,chatService){
	console.log("in Contacts Controller");

	 contactsService.GetContacts(function(contacts){
		 	$scope.names = contacts;
	});

	$scope.beginChat = function(receiverName){
			$scope.receiver = receiverName;
			chatService.InitChat(receiverName,function(initiated,key){
					if(initiated){
							$scope.chatKey = key;
							// messages which will be used by ng-repeat in contacts.html
							 chatService.GetMessages($scope.receiver,$scope.chatKey,function(success,messages){
								 if(success){
									$scope.messages = messages;
								}
							});
					}
			});
	}

	$scope.sendMessage = function(){
		 chatService.encryptAndSend($scope.message, $scope.chatKey, $scope.receiver,function(success,insertedMessage){
				 if(success){
					 $scope.messages.push(insertedMessage);
					 $scope.message = "";
				 }else{
					 alert("Message was not sent. Please try again");
				 }
			});

	}

});
