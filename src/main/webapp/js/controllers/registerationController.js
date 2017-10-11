angular.module('mainApp').controller('registerController',function($scope,$rootScope,RegistrationService, $location){
  //  console.log("in registerController");

   (function initController() {
           // check if $rootScope.globals exist or not which means that user user credentials are loaded from cookies
           if($rootScope.globals && $rootScope.globals.currentUser ){
             console.log($rootScope.globals);
             $location.path('/home');
           }
      })();

   $scope.register = function register() {
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

});
