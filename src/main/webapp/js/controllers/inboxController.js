angular.module('mainApp').controller('inboxController',function($scope,$location,inboxService,chatService,AuthenticationService){
	console.log("in Contacts Controller");

	 inboxService.GetInbox(function(inbox){
     console.log("heree");
		 	$scope.inbox = inbox;
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

	$scope.logout = function(){
		AuthenticationService.ClearCredentials();
		$location.path('/login');
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
