app.controller('registerController',function($scope){
   console.log("in registerController");

var userName = "test123";
var password = "test";
var hashedPassword = cryptico.sha1(password);

   var RSAKeys = cryptico.generateRSAKey(userName+"?"+password, 512);
   var rsaEncPubKey = cryptico.getPubKey(RSAKeys);

   var RSASignKeys = cryptico.generateRSAKey("??"+password+"??"+userName+"??",512)
   var rsaSignPubKey = cryptico.getPubKey(RSASignKeys);

   var client = {
     userName: userName,
     password: hashedPassword,
     rsaEncPubKey:rsaEncPubKey,
     rsaSignPubKey: rsaSignPubKey
   };


   var n = "a82269a0017f50c5cc7dba58886c4828eb0b280011743e613512880f98af1db656ee2e6822ae245d190be2b7c39722cf1f55a29ce7a66d80df9b9343761edd73";
   var e = "0000000000000000000000000000000000010001";

   RSAKeys = cryptico.generateRSAKey("xx", 512);
   cryptico.setPublicKey(RSAKeys,n,e);

   var cipherText = cryptico.RSAEncrypt(hashedPassword,RSAKeys);

   var data  = client.userName + "-" + client.password + "-" + client.rsaEncPubKey + "-" + client.rsaSignPubKey;
   var signature = cryptico.RSASign(data,RSASignKeys);

   console.log(client.userName + "-" + cipherText + "-" + client.rsaEncPubKey + "-" + client.rsaSignPubKey + "-" + signature);

});
