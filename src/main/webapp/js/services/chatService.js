/*
app.factory('chatService',function ($http){ 
  
  return { 
     

     sendMessage : function(message,cb){  
     
  
                     	 var req = {  
                                        method : 'POST', 
                                        url : '/api/booking', 
                                        data : {booking: booking} ,
                                       headers:
                                              {
                                                'x-access-token': response
                                              }
                                      };  
                                       return $http(req)

                                              .success(function(response) {
                                                //console.log("response --> "+response);
                                                  cb(response);
                                              })
                                              .error(function(response) {
                                                  console.log(response.statusText);
                                                  alert("An error occured please try again");
                                          });
                                 })
                                        .error(function(response){
                                        console.log(response.statusText);
                                         alert("An error occured please try again");
                                        }) ;                         



    
  }  
});
*/