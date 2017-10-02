angular.module('mainApp').factory('contactsService', contactsService);
contactsService.$inject = ['$http','$rootScope'];

function getContacts($http,$rootScope){
  var req = {
     method: 'GET',
     url: 'rest/contacts',
     data: { data: body }
 };

 return $http(req).then(function successCallback(response){
       if(response){
         // callback with true flag indicating that registeration
         // completed successfully
         console.log(response.data.contacts);
         callback(true,response.data.contacts);
       }else{
         alert(response);
         // callback with true flag indicating that registeration
         // failed
          callback(false,null);
       }
   },
   function errorCallback(response) {
     alert(response);
     console.log(respone.data);
     callback(false,response.data);
   });
}
