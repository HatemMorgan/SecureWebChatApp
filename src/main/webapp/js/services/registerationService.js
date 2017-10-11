// (function () {
//     'use strict';

    angular.module('mainApp').factory('RegistrationService', RegistrationService);
    RegistrationService.$inject = ['$http','$rootScope','MainService'];
    function RegistrationService($http,$rootScope,MainService) {

        var service = {};
        service.Register = Register;
        return service;

        function Register(user,callback){

          // create encryption RSA key pairs and signature RSA key pairs
          var RSAKeys = cryptico.generateRSAKey(user.userName+"?"+user.password, 512);
          var rsaEncPubKey = cryptico.getPubKey(RSAKeys);

          var RSASignKeys = cryptico.generateRSAKey("??"+user.password+"??"+user.userName+"??",512)
          var rsaSignPubKey = cryptico.getPubKey(RSASignKeys);

          // hash password using SHA1
          var hashedPassword = cryptico.sha1(user.password);


          var client = {
            userName: user.userName,
            password: hashedPassword,
            rsaEncPubKey:rsaEncPubKey,
            rsaSignPubKey: rsaSignPubKey
          };


          var serverEncPubKey = MainService.GetServerEncPubKey(function(serverEncPubKey){

            var keyParams = serverEncPubKey.split(":");
            var n = keyParams[0];
            var e = keyParams[1];

            RSAKeys = cryptico.generateRSAKey("xx", 512);
            cryptico.setPublicKey(RSAKeys,n,e);

            var cipherText = cryptico.RSAEncrypt(hashedPassword,RSAKeys);

            var data  = client.userName + "-" + client.password + "-" + client.rsaEncPubKey + "-" + client.rsaSignPubKey;
            var signature = cryptico.RSASign(data,RSASignKeys);
            var body = client.userName + "-" + cipherText + "-" + client.rsaEncPubKey + "-" + client.rsaSignPubKey + "-" + signature;

            // send post request to register API to register new user
            var req = {
               method: 'POST',
               url: 'rest/register',
               data: { data: body }
           };

           return $http(req).then(function successCallback(response){
                 if(response){
                   // callback with true flag indicating that registeration
					         // completed successfully
                   callback(true,response);
                 }else{
                   alert(response);
                   // callback with true flag indicating that registeration
					         // failed
                    callback(false,null);
                 }
             },
             function errorCallback(response) {
              //  console.log(response.data.errMessage);
               alert(response.data.errMessage);
                callback(false,null);
             });



    });

  }
  }



// })();
