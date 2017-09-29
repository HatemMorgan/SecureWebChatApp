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


   var n = "b2597cd17eb7ea18f71299eb39896485abd920eb24fe777309e1658a8457f992631257e3071b190dd2d648074ea3370ad50287411c022f46a3c90061a6afc607";
   var e = "0000000000000000000000000000000000010001";

   RSAKeys = cryptico.generateRSAKey("xx", 512);
   cryptico.setPublicKey(RSAKeys,n,e);

   var cipherText = cryptico.RSAEncrypt(hashedPassword,RSAKeys);

   var data  = client.userName + "-" + client.password + "-" + client.rsaEncPubKey + "-" + client.rsaSignPubKey;
   var signature = cryptico.RSASign(data,RSASignKeys);

   console.log(client.userName + "-" + cipherText + "-" + client.rsaEncPubKey + "-" + client.rsaSignPubKey + "-" + signature);

});
