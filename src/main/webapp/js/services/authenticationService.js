// (function () {
//     'use strict';

        angular.module('mainApp').factory('AuthenticationService', AuthenticationService);

    AuthenticationService.$inject = ['$http', '$cookies', '$rootScope'];
    function AuthenticationService($http, $cookies, $rootScope) {

        // service return object
        // it contains only public methods that can be called from outside
        var service = {};

        service.Login = Login;
        service.SetCredentials = SetCredentials;
        service.ClearCredentials = ClearCredentials;
        service.CreateAccessToken = CreateToken;
        service.GetRSAEncObj = GetRSAEncObj;
        service.GetRSASignObj = GetRSASignObj;
        service.CreateTokenFromEncodedAuthData = CreateTokenFromEncodedAuthData;
        service.GetUserName = GetUserName;
        service.GetPassword = GetPassword;

        return service;

        function CreateTokenFromEncodedAuthData(authdata){
          var decoded = Base64.decode(authdata).split(":");
          var userName = decoded[0];
          var password = decoded[1];
          $rootScope.username = userName;
          $rootScope.password = password;
          var token = this.CreateToken(userName,password);
          return token;
        }

        function CreateToken(userName,password){
          var hashedPassword = cryptico.sha1(password);

          var RSASignKeys = cryptico.generateRSAKey("??"+password+"??"+userName+"??",512)

          var utcSeconds = 1234567890;
          var d = new Date(); // The 0 there is the key, which sets the date to the epoch
          var utc = d.toUTCString();
          var epoch = d.getTime();

          var signature = cryptico.RSASign(hashedPassword+":"+epoch,RSASignKeys);

          var accessToken = userName+":"+epoch+":"+signature;
          return accessToken;
        }

        function GetRSAEncObj(userName,password){
           var RSAKeys = cryptico.generateRSAKey(userName+"?"+password, 512);
           return RSAKeys;
        }

        function GetRSASignObj(userName,password){
           var RSASignKeys = cryptico.generateRSAKey("??"+password+"??"+userName+"??",512)
           return RSASignKeys;
        }


        function Login(username, password, callback) {
          $rootScope.username = username;
          $rootScope.password = password;
          var token = service.CreateAccessToken(username,password);
          var req = {
             method: 'GET',
             url: 'rest/login',
             headers:
                     {
                       'x-access-token': token
                     }
         };

            /* logging in
             ----------------------------------------------*/
             return $http(req).then(function successCallback(response){
                   if(response){

                     callback(response);
                   }else{
                      callback(null);
                   }
               },
               function errorCallback(response) {
                  alert("An error occured while logging in please try again");
               });

        }

        function GetUserName(){
          return $rootScope.globals.currentUser.username;
        }

        function GetPassword(){
          return Base64.decode($rootScope.globals.currentUser.authdata).split(':')[1];
        }

        function SetCredentials(username, password) {
            var authdata = Base64.encode(username + ':' + password);

            $rootScope.globals = {
                currentUser: {
                    username: username,
                    authdata: authdata
                }
            };

            // set default auth header for http requests
            // $http.defaults.headers.common['Authorization'] = 'Basic ' + authdata;

            // store user details in globals cookie that keeps user logged in for 1 week (or until they logout)
            // var cookieExp = new Date();
            // cookieExp.setDate(cookieExp.getDate() + 7);
            // $cookies.putObject('globals', $rootScope.globals, { expires: cookieExp });
        }

        function ClearCredentials() {
            $rootScope.globals = {};
            $cookies.remove('globals');
            $http.defaults.headers.common.Authorization = 'Basic';
        }
    }

    // Base64 encoding service used by AuthenticationService
    var Base64 = {

        keyStr: 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=',

        encode: function (input) {
            var output = "";
            var chr1, chr2, chr3 = "";
            var enc1, enc2, enc3, enc4 = "";
            var i = 0;

            do {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                    this.keyStr.charAt(enc1) +
                    this.keyStr.charAt(enc2) +
                    this.keyStr.charAt(enc3) +
                    this.keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            } while (i < input.length);

            return output;
        },

        decode: function (input) {
            var output = "";
            var chr1, chr2, chr3 = "";
            var enc1, enc2, enc3, enc4 = "";
            var i = 0;

            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            var base64test = /[^A-Za-z0-9\+\/\=]/g;
            if (base64test.exec(input)) {
                window.alert("There were invalid base64 characters in the input text.\n" +
                    "Valid base64 characters are A-Z, a-z, 0-9, '+', '/',and '='\n" +
                    "Expect errors in decoding.");
            }
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

            do {
                enc1 = this.keyStr.indexOf(input.charAt(i++));
                enc2 = this.keyStr.indexOf(input.charAt(i++));
                enc3 = this.keyStr.indexOf(input.charAt(i++));
                enc4 = this.keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }

                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";

            } while (i < input.length);

            return output;
        }
    };

// })();
