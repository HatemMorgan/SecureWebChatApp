// (function () {
//     'use strict';

    angular.module('mainApp').factory('MainService', MainService);

    MainService.$inject = ['$http', '$rootScope'];
    function MainService($http, $rootScope) {

        // service return object
        // it contains only public methods that can be called from outside
        var service = {};

        service.GetServerEncPubKey = GetServerEncPubKey;
        service.GetServerSignPubKey = GetServerSignPubKey;

        return service;

        var serverEncPubKey;
        var serverSignPubKey;

        // get server public keys
        function getServerPubKey(cb){

            loadServerPubKey(function(serverPubKeys){
                serverEncPubKey = serverPubKeys.encryptionPubKey;
                serverSignPubKey = serverPubKeys.signaturePubKey;
                cb();
            });
          }


          // return serverEncPubKey if exist. if not load it
        function GetServerEncPubKey(cb){
          if(serverEncPubKey){
            cb(serverEncPubKey);
          }else{
              getServerPubKey(function(){
              cb(serverEncPubKey);
            });
          }
        }

        // return serverEncPubKey if exist. if not load it
      function GetServerSignPubKey(){
        if(serverSignPubKey){
          return serverSignPubKey;
        }else{
          return  getServerPubKey(function(){
            return serverSignPubKey;
          });
        }
      }


        // send a GET request to server to get its public keys
        function loadServerPubKey(callback) {

          var req = {
             method: 'GET',
             url: 'rest/publicKey'
         };


             return $http(req).then(function successCallback(response){
               console.log("here");
                   if(response){
                     console.log(response.data);
                     callback(response.data);
                   }else{
                      callback(null);
                   }
               },
               function errorCallback(response) {
                 console.log("errr heree");
                  console.log(response.statusText);
                  alert("An error occured while logging in please try again");
               });

        }



    }


// })();
