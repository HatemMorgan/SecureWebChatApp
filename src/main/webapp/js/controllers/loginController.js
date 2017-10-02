angular.module('mainApp').controller('loginController',function($scope,$location,$rootScope, AuthenticationService){
  console.log("in loginController");

  (function initController() {
            // check if $rootScope.globals exist or not which means that user user credentials are loaded from cookies
            if($rootScope.globals && $rootScope.globals.currentUser ){
              console.log($rootScope.globals);
              $location.path('/home');
            }else{
             // reset login status
             AuthenticationService.ClearCredentials();

             // set loginError flag of span tag that shows error message if login failed
             $scope.loginError = false;
           }
         })();

  $scope.login = function login() {
    console.log("hereee");
        // enable dataLoading to show loading gif
        $scope.dataLoading = true;
        // call AuthenticationService to login
        AuthenticationService.Login($scope.username,$scope.password, function (response) {
            if (response.status == 200) {
                AuthenticationService.SetCredentials($scope.username,$scope.password);
                $location.path('/home');
            } else {
                $scope.dataLoading = false;
                $scope.loginError = true;

            }
        });
    };

  // var userName = "test123";
  // var password = "test";
  // var hashedPassword = cryptico.sha1(password);
  //
  // var RSASignKeys = cryptico.generateRSAKey("??"+password+"??"+userName+"??",512)
  //
  // var utcSeconds = 1234567890;
  // var d = new Date(); // The 0 there is the key, which sets the date to the epoch
  // var utc = d.toUTCString();
  // var epoch = d.getTime();
  //
  // var signature = cryptico.RSASign(hashedPassword+":"+epoch,RSASignKeys);
  //
  // var accessToken = userName+":"+epoch+":"+signature;
  // console.log(accessToken);

});
