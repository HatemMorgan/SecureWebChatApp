angular.module('mainApp').controller('registerController',function($scope,RegistrationService, $location){
   console.log("in registerController");

   $scope.register = function register() {
     console.log("hereeeeee");
     // enable dataLoading to show loading gif
     $scope.dataLoading = true;
     var user = {
       userName: $scope.username,
       password: $scope.password
     };
     
     RegistrationService.Register(user,function(success,response){
        if(success){
             $location.path('/login');
        }else{
          this.dataLoading = false;
           // handle error
           // TODO
        }
     });
  }

// var userName = "test123";
// var password = "test";
// var hashedPassword = cryptico.sha1(password);
//
//    var RSAKeys = cryptico.generateRSAKey(userName+"?"+password, 512);
//    var rsaEncPubKey = cryptico.getPubKey(RSAKeys);
//
//    var RSASignKeys = cryptico.generateRSAKey("??"+password+"??"+userName+"??",512)
//    var rsaSignPubKey = cryptico.getPubKey(RSASignKeys);
//
//    var client = {
//      userName: userName,
//      password: hashedPassword,
//      rsaEncPubKey:rsaEncPubKey,
//      rsaSignPubKey: rsaSignPubKey
//    };



});
